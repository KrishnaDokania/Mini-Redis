
package com.miniRedis.command;

public class CommandParser {

    public String[] parse(String input) {

        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty command");
        }

        return input.trim().split("\\s+");
    }
}
