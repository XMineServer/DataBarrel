package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.Closeable;
import java.util.Map;

final class DataBarrelServiceImpl implements Closeable, DataBarrelService {

    private final Map<String, HikariDataSource> hikariDataSourceMap;
    private final Map<String, S3Client> s3ClientMap;
    private boolean isOpen = true;

    public DataBarrelServiceImpl(@NotNull Map<String, HikariDataSource> hikariDataSourceMap,
                                 @NotNull Map<String, S3Client> s3ClientMap) {
        this.hikariDataSourceMap = hikariDataSourceMap;
        this.s3ClientMap = s3ClientMap;
    }


    @Nullable
    @Override
    public HikariDataSource getHikariDataSource(String name) {
        return hikariDataSourceMap.get(name);
    }

    @Nullable
    @Override
    public S3Client getS3Client(String name) {
        return s3ClientMap.get(name);
    }

    @Override
    public synchronized void close() {
        if (isOpen) {
            hikariDataSourceMap.forEach((name, source) -> source.close());
            s3ClientMap.forEach((name, source) -> source.close());
            isOpen = false;
        }
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }
}
