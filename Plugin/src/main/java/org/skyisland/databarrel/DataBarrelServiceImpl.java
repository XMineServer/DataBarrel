package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.curator.framework.CuratorFramework;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skyisland.databarrel.config.DatabaseConfiguration;
import org.skyisland.databarrel.config.RedisConfiguration;
import org.skyisland.databarrel.config.S3Configuration;
import org.skyisland.databarrel.config.ZooKeeperConfiguration;
import org.skyisland.databarrel.datasource.*;
import redis.clients.jedis.UnifiedJedis;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

final class DataBarrelServiceImpl implements Closeable, DataBarrelService {

    private final HikariDataSourceFactory hikariDataSourceFactory = new HikariDataSourceFactory();
    private final JedisProviderFactory jedisProviderFactory = new JedisProviderFactory();
    private final S3ClientFactory s3ClientFactory = new S3ClientFactory();
    private final ZooKeeperFactory zooKeeperFactory = new ZooKeeperFactory();

    private final Map<String, LazyDataProvider<DatabaseConfiguration, HikariDataSource>> hikariDataSourceMap = new HashMap<>();
    private final Map<String, LazyDataProvider<S3Configuration, S3Client>> s3ClientMap = new HashMap<>();
    private final Map<String, LazyDataProvider<ZooKeeperConfiguration, CuratorFramework>> zooKeeperClientMap = new HashMap<>();
    private final Map<String, LazyDataProvider<RedisConfiguration, UnifiedJedis>> jedisClientMap = new HashMap<>();
    private boolean isOpen = true;

    public DataBarrelServiceImpl(
            @NotNull Map<String, DatabaseConfiguration> hikariDataSourceMap,
            @NotNull Map<String, S3Configuration> s3ClientMap,
            @NotNull Map<String, RedisConfiguration> jedisClientMap,
            @NotNull Map<String, ZooKeeperConfiguration> zooKeeperClientMap
    ) {
        hikariDataSourceMap.forEach((key, config) ->
                this.hikariDataSourceMap.put(key, new LazyDataProvider<>(config, hikariDataSourceFactory))
        );
        s3ClientMap.forEach((key, config) ->
                this.s3ClientMap.put(key, new LazyDataProvider<>(config, s3ClientFactory))
        );
        jedisClientMap.forEach((key, config) ->
                this.jedisClientMap.put(key, new LazyDataProvider<>(config, jedisProviderFactory))
        );
        zooKeeperClientMap.forEach((key, config) ->
                this.zooKeeperClientMap.put(key, new LazyDataProvider<>(config, zooKeeperFactory))
        );
    }


    @Nullable
    @Override
    public HikariDataSource getHikariDataSource(String name, boolean autoShutdown) throws Exception {
        if (autoShutdown) {
            return hikariDataSourceMap.get(name).getProvider();
        } else {
            return hikariDataSourceMap.get(name).createProvider();
        }
    }

    @Nullable
    @Override
    public S3Client getS3Client(String name, boolean autoShutdown) throws Exception {
        if (autoShutdown) {
            return s3ClientMap.get(name).getProvider();
        } else {
            return s3ClientMap.get(name).createProvider();
        }
    }

    @Nullable
    @Override
    public CuratorFramework getZooKeeperClient(String name, boolean autoShutdown) throws Exception {
        if (autoShutdown) {
            return zooKeeperClientMap.get(name).getProvider();
        } else {
            return zooKeeperClientMap.get(name).createProvider();
        }
    }

    @Nullable
    @Override
    public UnifiedJedis getJedisProvider(String name, boolean autoShutdown) throws Exception {
        if (autoShutdown) {
            return jedisClientMap.get(name).getProvider();
        } else {
            return jedisClientMap.get(name).createProvider();
        }
    }

    @Override
    public synchronized void close() {
        if (isOpen) {
            hikariDataSourceMap.forEach((name, source) -> source.safeGetProvider().ifPresent(HikariDataSource::close));
            s3ClientMap.forEach((name, source) -> source.safeGetProvider().ifPresent(S3Client::close));
            jedisClientMap.forEach((name, source) -> source.safeGetProvider().ifPresent(UnifiedJedis::close));
            zooKeeperClientMap.forEach((name, source) -> source.safeGetProvider().ifPresent(CuratorFramework::close));
            isOpen = false;
        }
    }

    public synchronized boolean isOpen() {
        return isOpen;
    }
}
