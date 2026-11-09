
package com.miniRedis.storage;

import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {

    private final ConcurrentHashMap<String, String> storage =
            new ConcurrentHashMap<>();

    // Store a key-value pair
    public void set(String key, String value) {
        storage.put(key, value);
    }

    // Retrieve a value using its key
    public String get(String key) {
        return storage.get(key);
    }

    // Delete a key
    public boolean delete(String key) {
        return storage.remove(key)!=null;
    }

    // Check whether a key exists
    public boolean exists(String key) {
        return storage.containsKey(key);
    }
}
