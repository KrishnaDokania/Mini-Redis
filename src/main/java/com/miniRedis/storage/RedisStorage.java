
package com.miniRedis.storage;

import com.miniRedis.model.RedisEntry;

import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {

    private final ConcurrentHashMap<String, RedisEntry> storage =
            new ConcurrentHashMap<>();

    // SET
    public void set(String key, String value) {
        storage.put(key, new RedisEntry(value));
    }

    // GET
    public String get(String key) {

        RedisEntry entry = storage.get(key);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            storage.remove(key, entry);
            return null;
        }

        return entry.getValue();
    }

    // DEL
    public boolean delete(String key) {

        RedisEntry entry = storage.get(key);

        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            storage.remove(key, entry);
            return false;
        }

        return storage.remove(key, entry);
    }

    // EXISTS
    public boolean exists(String key) {
        return get(key) != null;
    }

    // EXPIRE
    public boolean expire(String key, long seconds) {

        RedisEntry entry = storage.get(key);

        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            storage.remove(key, entry);
            return false;
        }

        if (seconds <= 0) {
            storage.remove(key, entry);
            return true;
        }

        long expiryTime =
                System.currentTimeMillis() + seconds * 1000L;

        entry.setExpiryTime(expiryTime);

        return true;
    }

    // TTL
    public long ttl(String key) {

        RedisEntry entry = storage.get(key);

        if (entry == null) {
            return -2;
        }

        if (entry.isExpired()) {
            storage.remove(key, entry);
            return -2;
        }

        long expiryTime = entry.getExpiryTime();

        if (expiryTime == -1) {
            return -1;
        }

        return Math.max(0,
                (expiryTime - System.currentTimeMillis()) / 1000);
    }

    // SETEX
    public boolean setEx(String key, long seconds, String value) {

        if (seconds <= 0) {
            return false;
        }

        long expiryTime =
                System.currentTimeMillis() + seconds * 1000L;

        RedisEntry entry = new RedisEntry(value);
        entry.setExpiryTime(expiryTime);

        storage.put(key, entry);

        return true;
    }
}
