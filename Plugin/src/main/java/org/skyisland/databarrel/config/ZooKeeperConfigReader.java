package org.skyisland.databarrel.config;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZooKeeperConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public ZooKeeperConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }

    public List<ZooKeeperConfiguration> loadConfigurations() {
        List<ZooKeeperConfiguration> resultList = new ArrayList<>();
        for (String zooKeeperClientKey : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(zooKeeperClientKey);
            if (subSection == null) {
                logger.warn("Can't load ZooKeeper client {}. Is not a section.", zooKeeperClientKey);
                continue;
            }
            resultList.add(readZooKeeperSection(subSection));
        }
        return Collections.unmodifiableList(resultList);
    }

    private ZooKeeperConfiguration readZooKeeperSection(ConfigurationSection section) {
        return ZooKeeperConfiguration.builder()
                .name(section.getName())
                .quorum(section.getStringList("quorum"))
                .sessionTimeout(section.getInt("sessionTimeout", 3000))
                .namespace(section.getString("namespace", "/data-barrel"))
                .retry(ZooKeeperConfiguration.RetryConfiguration
                        .builder()
                        .maxRetries(section.getInt("maxRetries", 5))
                        .intervalMs(section.getInt("intervalMs", 1000))
                        .build())
                .build();
    }
}
