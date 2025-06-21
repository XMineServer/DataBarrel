package org.skyisland.bedrockdata;

import com.zaxxer.hikari.HikariDataSource;

public interface BedrockDataService {

    HikariDataSource getHikariDataSource(String name);

    static BedrockDataService getInstance() {
        return BedrockDataBootstrapper.bedrockDataService;
    }
}
