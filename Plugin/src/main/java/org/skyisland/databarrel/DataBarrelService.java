package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import redis.clients.jedis.UnifiedJedis;
import org.apache.curator.framework.CuratorFramework;
import software.amazon.awssdk.services.s3.S3Client;

public interface DataBarrelService {

    HikariDataSource getHikariDataSource(String name, boolean autoShutdown) throws Exception;
    S3Client getS3Client(String name, boolean autoShutdown) throws Exception;
    CuratorFramework getZooKeeperClient(String name, boolean autoShutdown) throws Exception;
    UnifiedJedis getJedisProvider(String name, boolean autoShutdown) throws Exception;

    static DataBarrelService getInstance() {
        return DataBarrelBootstrap.bedrockDataService;
    }
}
