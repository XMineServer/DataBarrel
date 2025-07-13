package org.skyisland.databarrel.config;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedisConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public RedisConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }
    public List<RedisConfiguration> loadConfigurations() {
        List<RedisConfiguration> resultList = new ArrayList<>();
        for (String redisKey : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(redisKey);
            if (subSection == null) {
                logger.warn("Can't load redis {}. Is not a section.", redisKey);
                continue;
            }
            resultList.add(readRedisSection(subSection));
        }
        return Collections.unmodifiableList(resultList);
    }

    private RedisConfiguration readRedisSection(ConfigurationSection section) {
        return RedisConfiguration.builder()
                .name(section.getName())
                .isCluster(section.getBoolean("cluster", false))
                .host(section.getString("host", "localhost"))
                .port(section.getInt("port", 6379))
                .user(section.getString("user"))
                .password(section.getString("password"))
                .clusterServers(readClusterNodes(section))
                .build();
    }

    private List<RedisConfiguration.Node> readClusterNodes(ConfigurationSection section) {
        var result = new ArrayList<RedisConfiguration.Node>();
        for (Map<?, ?> map : section.getMapList("nodes")) {
            Object hostValue = map.get("host");
            Object portValue = map.get("port");
            if (hostValue == null || portValue == null) continue;
            if (!(portValue instanceof Number portNumber)) continue;
            result.add(new RedisConfiguration.Node(hostValue.toString(), portNumber.intValue()));
        }
        return Collections.unmodifiableList(result);
    }

}
