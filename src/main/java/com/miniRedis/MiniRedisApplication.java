
package com.miniRedis;

import com.miniRedis.storage.RedisStorage;

public class MiniRedisApplication {

    public static void main(String[] args) {

        RedisStorage redis = new RedisStorage();

        // Store data
        redis.set("name", "Krishna");
        redis.set("age", "22");

        // Retrieve data
        System.out.println(redis.get("name"));

        // Check existence
        System.out.println(redis.exists("age"));

        // Delete data
        System.out.println(redis.delete("name"));

        // Check after deletion
        System.out.println(redis.get("name"));
    }
}
