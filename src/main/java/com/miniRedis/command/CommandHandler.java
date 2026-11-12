
package com.miniRedis.command;

import com.miniRedis.storage.RedisStorage;

public class CommandHandler {

    private final RedisStorage storage;

    public CommandHandler(RedisStorage storage) {
        this.storage = storage;
    }

    public String handle(String[] parts) {

        if (parts == null || parts.length == 0) {
            return "ERR empty command";
        }

        String command = parts[0].toUpperCase();

        switch (command) {

            case "SET":

                if (parts.length != 3) {
                    return "ERR wrong number of arguments for SET";
                }

                storage.set(parts[1], parts[2]);
                return "OK";

            case "GET":

                if (parts.length != 2) {
                    return "ERR wrong number of arguments for GET";
                }

                String value = storage.get(parts[1]);

                return value == null ? "(nil)" : value;

            case "DEL":

                if (parts.length != 2) {
                    return "ERR wrong number of arguments for DEL";
                }

                return storage.delete(parts[1]) ? "1" : "0";

            case "EXISTS":

                if (parts.length != 2) {
                    return "ERR wrong number of arguments for EXISTS";
                }

                return storage.exists(parts[1]) ? "1" : "0";
            case "PING":
                if (parts.length != 1) {
                    return "ERR wrong number of arguments for PING";
                }
                return "PONG";

            case "EXPIRE":
                if (parts.length != 3) {
                    return "ERR wrong number of arguments for EXPIRE";
                }

                try {
                    long seconds = Long.parseLong(parts[2]);

                    return storage.expire(parts[1], seconds) ? "1" : "0";

                } catch (NumberFormatException e) {
                    return "ERR invalid expiration time";
                }

            case "TTL":
                if (parts.length != 2) {
                    return "ERR wrong number of arguments for TTL";
                }

                return String.valueOf(storage.ttl(parts[1]));

            case "SETEX":
                if (parts.length != 4) {
                    return "ERR wrong number of arguments for SETEX";
                }

                try {
                    long seconds = Long.parseLong(parts[2]);

                    if (seconds <= 0) {
                        return "ERR invalid expiration time";
                    }

                    storage.setEx(parts[1], seconds, parts[3]);
                    return "OK";

                } catch (NumberFormatException e) {
                    return "ERR invalid expiration time";
                }

            default:
                return "ERR unknown command '" + command + "'";
        }
    }
}
