
package com.miniRedis.protocol;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RESPResponseTest {

    public static void main(String[] args) throws Exception {

        RESPResponse response = new RESPResponse();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeSimpleString(output, "OK");
        response.writeInteger(output, 1);
        response.writeBulkString(output, "Krishna");
        response.writeBulkString(output, null);

        System.out.println(
                output.toString(StandardCharsets.UTF_8)
                        .replace("\r", "\\r")
                        .replace("\n", "\\n\n")
        );
    }
}
