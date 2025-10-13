package org.skyisland.databarrel;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import redis.clients.jedis.UnifiedJedis;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class DataBarrelTest {

    private DataBarrelTest() {
    }

    public static void testSqlConnection(Logger logger, String datasourceName, DataBarrelService service) {
        try (var dataSource = service.getHikariDataSource(datasourceName, false)) {
            try (Connection connection = dataSource.getConnection()) {
                logger.info("SQL: Success database connection!");

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
                    if (rs.next()) {
                        logger.info("SQL: Database version: {}", rs.getString(1));
                    }
                }

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("CREATE TABLE IF NOT EXISTS test_connection (id INT, name VARCHAR(20))");
                    logger.info("SQL: Test table created or already exists");

                    stmt.execute("INSERT INTO test_connection VALUES (1, 'test_user')");
                    logger.info("SQL: Test record added");

                    try (ResultSet rs = stmt.executeQuery("SELECT * FROM test_connection")) {
                        while (rs.next()) {
                            logger.info(
                                    "Data from table: id={}, name='{}'",
                                    rs.getInt("id"),
                                    rs.getString("name")
                            );
                        }
                    }

                    stmt.execute("DROP TABLE IF EXISTS test_connection");
                    logger.info("SQL: Test table removed");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            logger.error("SQL: Connection fail", e);
        }
    }

    public static void testS3Connection(Logger logger, String s3ClientName, DataBarrelService service) {
        try (S3Client s3Client = service.getS3Client(s3ClientName, false)) {
            if (s3Client == null) {
                logger.error("Can't found s3 client {}", s3ClientName);
                return;
            }
            String bucketName = UUID.randomUUID().toString();
            String fileName = "test-object.txt";
            String content = "test message";
            try {
                s3Client.createBucket((b) -> b.bucket(bucketName));
                logger.info("S3: Test bucket created: {}", bucketName);
            } catch (RuntimeException e) {
                logger.info("S3: Can't create test bucket: {}", bucketName);
                throw e;
            }
            try {
                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(fileName)
                                .build(),
                        RequestBody.fromString(content)
                );
                logger.info("S3: Test file downloaded: {}; with content: \"{}\"", fileName, content);
            } catch (RuntimeException e) {
                logger.info("S3: Failed to load test file: {}", fileName);
                throw e;
            }
            try {
                s3Client.deleteObject(b -> b.bucket(bucketName).key(fileName));
                logger.info("S3: Test file deleted: {}", fileName);
            } catch (RuntimeException e) {
                logger.info("S3: Failed to delete test file: {}", fileName);
                throw e;
            }
            try {
                s3Client.deleteBucket(b -> b.bucket(bucketName));
                logger.info("S3: Test bucket deleted: {}", bucketName);
            } catch (RuntimeException e) {
                logger.info("S3: Failed to delete test bucket: {}", bucketName);
                throw e;
            }
        } catch (Exception e) {
            logger.error("S3: Connection fail", e);
        }
    }

    public static void testZooKeeperConnection(Logger logger, String zooKeeperName, DataBarrelService service) {
        try (CuratorFramework client = service.getZooKeeperClient(zooKeeperName, false)) {
            if (client == null) {
                logger.error("Can't find ZooKeeper configuration: {}", zooKeeperName);
                return;
            }
            String path = "/" + UUID.randomUUID();
            String connectionString = client.getZookeeperClient().getCurrentConnectionString();
            byte[] content = "zookeeper message".getBytes();
            try {
                if (client.getState() == CuratorFrameworkState.LATENT) {
                    client.start();
                    client.blockUntilConnected(1, TimeUnit.SECONDS);
                    logger.info("ZooKeeper connected to: {}", connectionString);
                } else {
                    logger.info("ZooKeeper already connected to: {}", connectionString);
                }
            } catch (Exception e) {
                logger.info("Failed to start ZooKeeper connection: {}", connectionString);
                throw new RuntimeException(e);
            }
            try {
                client.create().forPath(path, content);
                logger.info("Test znode created: {}; with content: \"{}\"", path, new String(content));
            } catch (Exception e) {
                logger.info("Failed to create ZooKeeper node: {}", path);
                throw new RuntimeException(e);
            }
            try {
                byte[] data = client.getData().forPath(path);
                logger.info("Read data from {}: {}", path, new String(data));
            } catch (Exception e) {
                logger.info("Failed to read data from {}", path);
                throw new RuntimeException(e);
            }
            try {
                client.delete().forPath(path);
                logger.info("Test znode deleted: {}", path);
            } catch (Exception e) {
                logger.info("Failed to delete test znode: {}", path);
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            logger.error("ZooKeeper: Connection fail", e);
        }
    }

    public static void testJedisConnection(Logger logger, String jedisProviderName, DataBarrelService service) {
        try (UnifiedJedis jedis = service.getJedisProvider(jedisProviderName, false)) {
            if (jedis == null) {
                logger.error("Can't found jedis provider {}", jedisProviderName);
                return;
            }
            String testKey = "test";
            String testValue = "Test value";
            jedis.set(testKey, testValue);
            logger.info("Redis: Set test {}:{}", testKey, testValue);
            String storedValue = jedis.get(testKey);
            logger.info("Redis: Get test {}:{}", testKey, storedValue);
            jedis.del(testKey);
            logger.info("Redis: Del test {}", testKey);
            storedValue = jedis.get(testKey);
            logger.info("Redis: Get test {}:{}", testKey, storedValue);
        }catch (Exception e) {
            logger.error("Redis: Connection fail", e);
        }
    }

}
