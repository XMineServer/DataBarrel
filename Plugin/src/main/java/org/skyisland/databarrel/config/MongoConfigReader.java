package org.skyisland.databarrel.config;

import com.mongodb.ServerAddress;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MongoConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public MongoConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }

    public List<MongoConfiguration> loadConfigurations() {
        if (section == null) return List.of();
        List<MongoConfiguration> resultList = new ArrayList<>();
        for (String redisKey : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(redisKey);
            if (subSection == null) {
                logger.warn("Can't load redis {}. Is not a section.", redisKey);
                continue;
            }
            resultList.add(readMongoSection(subSection));
        }
        return Collections.unmodifiableList(resultList);
    }

    private MongoConfiguration readMongoSection(ConfigurationSection section) {
        return MongoConfiguration.builder()
                .name(section.getName())
                .user(section.getString("user"))
                .password(section.getString("password"))
                .source(section.getString("source", "admin"))
                .servers(readServerAddress(section))
                .build();
    }

    private List<ServerAddress> readServerAddress(ConfigurationSection section) {
        var result = new ArrayList<ServerAddress>();
        for (Map<?, ?> map : section.getMapList("servers")) {
            Object hostValue = map.get("host");
            Object portValue = map.get("port");
            if (hostValue == null || portValue == null) continue;
            if (!(portValue instanceof Number portNumber)) continue;
            result.add(new ServerAddress(hostValue.toString(), portNumber.intValue()));
        }
        return Collections.unmodifiableList(result);
    }

}
