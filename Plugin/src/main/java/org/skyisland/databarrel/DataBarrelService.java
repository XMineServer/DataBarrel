package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;

public interface DataBarrelService {

    HikariDataSource getHikariDataSource(String name);

    static DataBarrelService getInstance() {
        return DataBarrelBootstrap.bedrockDataService;
    }
}
