package org.skyisland.databarrel;

import com.mongodb.client.MongoClient;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.curator.framework.CuratorFramework;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.UnifiedJedis;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.Closeable;
import java.util.Map;

final class DataBarrelServiceImpl implements Closeable, DataBarrelService {

    private final Map<String, HikariDataSource> hikariDataSourceMap;
    private final Map<String, S3Client> s3ClientMap;
    private final Map<String, CuratorFramework> zooKeeperClientMap;
    private final Map<String, UnifiedJedis> jedisClientMap;
    private final Map<String, MongoClient> mongoClientMap;
    private boolean isOpen = true;

    public DataBarrelServiceImpl(
            @NotNull Map<String, HikariDataSource> hikariDataSourceMap,
            @NotNull Map<String, S3Client> s3ClientMap,
            @NotNull Map<String, UnifiedJedis> jedisClientMap,
            @NotNull Map<String, CuratorFramework> zooKeeperClientMap,
            @NotNull Map<String, MongoClient> mongoClientMap
    ) {
        this.hikariDataSourceMap = hikariDataSourceMap;
        this.s3ClientMap = s3ClientMap;
        this.zooKeeperClientMap = zooKeeperClientMap;
        this.jedisClientMap = jedisClientMap;
        this.mongoClientMap = mongoClientMap;
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

    @Nullable
    @Override
    public CuratorFramework getZooKeeperClient(String name) {
        return zooKeeperClientMap.get(name);
    }

    public UnifiedJedis getJedisProvider(String name) {
        return jedisClientMap.get(name);
    }

    @Override
    public MongoClient getMongoClient(String name) {
        return mongoClientMap.get(name);
    }

    @Override
    public synchronized void close() {
        if (isOpen) {
            hikariDataSourceMap.forEach((name, source) -> source.close());
            s3ClientMap.forEach((name, source) -> source.close());
            jedisClientMap.forEach((name, source) -> source.close());
            zooKeeperClientMap.forEach((name, source) -> source.close());
            isOpen = false;
        }
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }
}
