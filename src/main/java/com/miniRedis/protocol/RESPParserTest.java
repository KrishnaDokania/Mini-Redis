
package com.miniRedis.protocol;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RESPParserTest {

    public static void main(String[] args) throws Exception {

        String request =
                "*3\r\n" +
                "$3\r\nSET\r\n" +
                "$7\r\nmessage\r\n" +
                "$11\r\nHello World\r\n";

        ByteArrayInputStream input =
                new ByteArrayInputStream(
                        request.getBytes(StandardCharsets.UTF_8)
                );

        RESPParser parser = new RESPParser();

        String[] result = parser.readCommand(input);

        System.out.println(Arrays.toString(result));
    }
}
