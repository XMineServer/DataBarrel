package org.skyisland.databarrel;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DataBarrelTest {

    private DataBarrelTest() {}

    public static void testConnection(Logger logger, String datasourceName, DataBarrelService service) {
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

}
