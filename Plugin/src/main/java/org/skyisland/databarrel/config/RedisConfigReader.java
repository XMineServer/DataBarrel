package org.skyisland.databarrel.config;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

public class RedisConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public RedisConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }

}
