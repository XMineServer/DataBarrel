package org.skyisland.databarrel.config;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public DatabaseConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }

    public List<DatabaseConfiguration> loadConfigurations() {
        if (section == null) return List.of();
        List<DatabaseConfiguration> resultList = new ArrayList<>();
        for (String databaseKey : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(databaseKey);
            if (subSection == null) {
                logger.warn("Can't load database {}. Is not a section.", databaseKey);
                continue;
            }
            resultList.add(readDatabaseSection(subSection));
        }
        return Collections.unmodifiableList(resultList);
    }

    private DatabaseConfiguration readDatabaseSection(ConfigurationSection section) {
        return DatabaseConfiguration.builder()
                .name(section.getName())
                .host(section.getString("host", "127.0.0.1"))
                .port(section.getInt("port", 3306))
                .database(section.getString("database"))
                .type(readDatabaseType(section.getString("type", "mysql")))
                .user(section.getString("user"))
                .password(section.getString("password"))
                .build();
    }

    private DatabaseType readDatabaseType(String value) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        logger.warn("Can't resolve database type {}. Use MYSQL.", value);
        return DatabaseType.MYSQL;
    }


}
