package com.miniRedis;

import com.miniRedis.storage.RedisStorage;

public class MiniRedisApplication {

    public static void main(String[] args) {

        RedisStorage redis = new RedisStorage();

        redis.set("name", "Krishna");
        redis.set("city", "Prayagraj");

        System.out.println(redis.get("name"));
        System.out.println(redis.get("city"));

        System.out.println(redis.exists("name"));

        redis.delete("name");

        System.out.println(redis.get("name"));
    }
}