package org.skyisland.bedrockdata.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.skyisland.bedrockdata.config.DatabaseConfiguration;

public class HikariDataSourceFactory {

    public HikariDataSource create(DatabaseConfiguration configuration) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(configuration.getJdbcUrl());
        hikariConfig.setDriverClassName(configuration.type().getDriverClass());
        hikariConfig.setUsername(configuration.user());
        hikariConfig.setPassword(configuration.password());
        return new HikariDataSource(hikariConfig);
    }

}
