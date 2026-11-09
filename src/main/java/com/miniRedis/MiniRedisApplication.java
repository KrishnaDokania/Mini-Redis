
package com.miniRedis;

import com.miniRedis.command.CommandHandler;
import com.miniRedis.storage.RedisStorage;
import com.miniRedis.server.RedisServer;

public class MiniRedisApplication {

    public static void main(String[] args) {

        RedisStorage storage = new RedisStorage();

        CommandHandler handler = new CommandHandler(storage);

        RedisServer server = new RedisServer(6379, handler);

        server.start();
    }
}
