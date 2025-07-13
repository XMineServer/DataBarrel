package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import org.skyisland.databarrel.datasource.JedisCommandsProvider;
import org.apache.curator.framework.CuratorFramework;
import software.amazon.awssdk.services.s3.S3Client;

public interface DataBarrelService {

    HikariDataSource getHikariDataSource(String name);
    S3Client getS3Client(String name);
    CuratorFramework getZooKeeperClient(String name);
    JedisCommandsProvider<?> getJedisProvider(String name);

    static DataBarrelService getInstance() {
        return DataBarrelBootstrap.bedrockDataService;
    }
}
