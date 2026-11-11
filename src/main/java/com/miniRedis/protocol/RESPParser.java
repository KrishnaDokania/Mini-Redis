
package com.miniRedis.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RESPParser {

    private static final int MAX_ARGUMENTS = 1000;
    private static final int MAX_BULK_LENGTH = 1024 * 1024;
    private static final int MAX_TOTAL_SIZE = 4 * 1024 * 1024;
    private static final int MAX_HEADER_LENGTH = 64;

    public String[] readCommand(InputStream input) throws IOException {

        int type = input.read();

        // Client disconnected
        if (type == -1) {
            return null;
        }

        // Commands must be RESP arrays
        if (type != '*') {
            throw new IOException("Expected RESP array");
        }

        // Read and validate array length
        int argumentCount = parseLength(readLine(input));

        if (argumentCount <= 0 || argumentCount > MAX_ARGUMENTS) {
            throw new IOException("Invalid argument count");
        }

        String[] arguments = new String[argumentCount];

        int totalSize = 0;

        for (int i = 0; i < argumentCount; i++) {

            // Every command argument must be a bulk string
            if (input.read() != '$') {
                throw new IOException("Expected bulk string");
            }

            int length = parseLength(readLine(input));

            // Null bulk strings aren't valid command arguments
            if (length < 0 || length > MAX_BULK_LENGTH) {
                throw new IOException("Invalid bulk string length");
            }

            totalSize += length;

            if (totalSize > MAX_TOTAL_SIZE) {
                throw new IOException("Request too large");
            }

            byte[] data = input.readNBytes(length);

            if (data.length != length) {
                throw new IOException("Incomplete bulk string");
            }

            // Verify CRLF after the data
            if (input.read() != '\r' || input.read() != '\n') {
                throw new IOException("Invalid bulk string terminator");
            }

            arguments[i] = new String(data, StandardCharsets.UTF_8);
        }

        return arguments;
    }

    private int parseLength(String value) throws IOException {

        try {

            // Reject empty values and non-numeric headers
            if (value.isEmpty()) {
                throw new NumberFormatException();
            }

            for (int i = 0; i < value.length(); i++) {

                char c = value.charAt(i);

                if (i == 0 && c == '-') {
                    continue;
                }

                if (c < '0' || c > '9') {
                    throw new NumberFormatException();
                }
            }

            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            throw new IOException("Invalid RESP length");
        }
    }

    private String readLine(InputStream input) throws IOException {

        StringBuilder result = new StringBuilder();

        while (true) {

            int current = input.read();

            if (current == -1) {
                throw new IOException("Unexpected end of input");
            }

            if (current == '\r') {

                if (input.read() != '\n') {
                    throw new IOException("Invalid line terminator");
                }

                return result.toString();
            }

            if (current == '\n' || current < 0x20 || current > 0x7E) {
                throw new IOException("Invalid RESP header character");
            }

            if (result.length() >= MAX_HEADER_LENGTH) {
                throw new IOException("RESP header too long");
            }

            result.append((char) current);
        }
    }
}
