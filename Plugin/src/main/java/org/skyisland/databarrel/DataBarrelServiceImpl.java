package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Map;

final class DataBarrelServiceImpl implements Closeable, DataBarrelService {

    private final Map<String, HikariDataSource> hikariDataSourceMap;
    private boolean isOpen = true;

    public DataBarrelServiceImpl(@NotNull Map<String, HikariDataSource> hikariDataSourceMap) {
        this.hikariDataSourceMap = hikariDataSourceMap;
    }


    @Nullable
    @Override
    public HikariDataSource getHikariDataSource(String name) {
        return hikariDataSourceMap.get(name);
    }

    @Override
    public synchronized void close() {
        if (isOpen) {
            hikariDataSourceMap.forEach((name, source) -> source.close());
            isOpen = false;
        }
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }
}
