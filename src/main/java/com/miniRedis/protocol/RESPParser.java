
package com.miniRedis.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RESPParser {

    public String[] readCommand(InputStream input) throws IOException {

        int type = input.read();

        // Client disconnected
        if (type == -1) {
            return null;
        }

        // RESP array must start with *
        if (type != '*') {
            throw new IOException("Expected RESP array");
        }

        // Read number of arguments
        int argumentCount = Integer.parseInt(readLine(input));

        if (argumentCount <= 0 || argumentCount > 1000) {
            throw new IOException("Invalid argument count");
        }

        String[] arguments = new String[argumentCount];

        for (int i = 0; i < argumentCount; i++) {

            // Each argument must be a bulk string
            if (input.read() != '$') {
                throw new IOException("Expected bulk string");
            }

            int length = Integer.parseInt(readLine(input));

            if (length < 0 || length > 1024 * 1024) {
                throw new IOException("Invalid bulk string length");
            }

            // Read exact number of bytes
            byte[] data = input.readNBytes(length);

            if (data.length != length) {
                throw new IOException("Incomplete bulk string");
            }

            // Check CRLF
            if (input.read() != '\r' || input.read() != '\n') {
                throw new IOException("Invalid bulk string terminator");
            }

            arguments[i] = new String(data, StandardCharsets.UTF_8);
        }

        return arguments;
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

                break;
            }

            result.append((char) current);
        }

        return result.toString();
    }
}
