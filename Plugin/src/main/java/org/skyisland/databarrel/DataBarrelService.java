package org.skyisland.databarrel;

import com.mongodb.client.MongoClient;
import com.zaxxer.hikari.HikariDataSource;
import redis.clients.jedis.UnifiedJedis;
import org.apache.curator.framework.CuratorFramework;
import software.amazon.awssdk.services.s3.S3Client;

public interface DataBarrelService {

    HikariDataSource getHikariDataSource(String name);
    S3Client getS3Client(String name);
    CuratorFramework getZooKeeperClient(String name);
    UnifiedJedis getJedisProvider(String name);
    MongoClient getMongoClient(String name);

    static DataBarrelService getInstance() {
        return DataBarrelBootstrap.bedrockDataService;
    }
}
