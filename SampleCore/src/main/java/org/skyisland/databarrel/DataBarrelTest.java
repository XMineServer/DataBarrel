package org.skyisland.databarrel;

import org.slf4j.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public final class DataBarrelTest {

    private DataBarrelTest() {}

    public static void testSqlConnection(Logger logger, String datasourceName, DataBarrelService service) {
        var dataSource = service.getHikariDataSource(datasourceName);
        if (dataSource == null) {
            logger.error("Can't found dataSource {}", datasourceName);
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Success database connection!");

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
                if (rs.next()) {
                    logger.info("MySQL version: {}", rs.getString(1));
                }
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS test_connection (id INT, name VARCHAR(20))");
                logger.info("Test table created or already exists");

                stmt.execute("INSERT INTO test_connection VALUES (1, 'test_user')");
                logger.info("Test record added");

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
                logger.info("Test table removed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testS3Connection(Logger logger, String s3ClientName,  DataBarrelService service) {
        S3Client s3Client = service.getS3Client(s3ClientName);
        if (s3Client == null) {
            logger.error("Can't found s3 client {}", s3ClientName);
            return;
        }
        String bucketName = UUID.randomUUID().toString();
        String fileName = "test-object.txt";
        String content = "test message";
        try {
            s3Client.createBucket((b) -> b.bucket(bucketName));
            logger.info("Test bucket created: {}",  bucketName);
        } catch (RuntimeException e) {
            logger.info("Can't create test bucket: {}",  bucketName);
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
            logger.info("Test file downloaded: {}; with content: \"{}\"", fileName, content);
        } catch (RuntimeException e) {
            logger.info("Failed to load test file: {}", fileName);
            throw e;
        }
        try {
            s3Client.deleteObject(b -> b.bucket(bucketName).key(fileName));
            logger.info("Test file deleted: {}", fileName);
        } catch (RuntimeException e) {
            logger.info("Failed to delete test file: {}", fileName);
            throw e;
        }
        try {
            s3Client.deleteBucket(b ->  b.bucket(bucketName));
            logger.info("Test bucket deleted: {}", bucketName);
        } catch (RuntimeException e) {
            logger.info("Failed to delete test bucket: {}", bucketName);
            throw e;
        }
    }
}
