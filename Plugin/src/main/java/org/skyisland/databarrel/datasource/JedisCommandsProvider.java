package org.skyisland.databarrel.datasource;

import redis.clients.jedis.JedisCommands;

import java.io.Closeable;
import java.util.function.Supplier;

public interface JedisCommandsProvider<T extends JedisCommands & Closeable> extends Supplier<T>, Closeable {

    @Override
    void close();

    @Override
    T get();
}
