
package com.miniRedis.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RESPResponse {

    public void writeSimpleString(OutputStream output, String value)
            throws IOException {

        write(output, "+" + value + "\r\n");
    }

    public void writeError(OutputStream output, String message)
            throws IOException {

        write(output, "-" + message + "\r\n");
    }

    public void writeInteger(OutputStream output, long value)
            throws IOException {

        write(output, ":" + value + "\r\n");
    }

    public void writeBulkString(OutputStream output, String value)
            throws IOException {

        if (value == null) {
            write(output, "$-1\r\n");
            return;
        }

        byte[] data = value.getBytes(StandardCharsets.UTF_8);

        write(output, "$" + data.length + "\r\n");
        output.write(data);
        write(output, "\r\n");
        output.flush();
    }

    private void write(OutputStream output, String value)
            throws IOException {

        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }
}
