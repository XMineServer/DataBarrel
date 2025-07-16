package org.skyisland.databarrel.config;

import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class S3ConfigReader {

    private final Logger logger;
    private final ConfigurationSection section;

    public S3ConfigReader(Logger logger, ConfigurationSection section) {
        this.logger = logger;
        this.section = section;
    }

    public List<S3Configuration> loadConfigurations() {
        if (section == null) return List.of();
        List<S3Configuration> resultList = new ArrayList<>();
        for (String databaseKey : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(databaseKey);
            if (subSection == null) {
                logger.warn("Can't load database {}. Is not a section.", databaseKey);
                continue;
            }
            resultList.add(readS3Section(subSection));
        }
        return Collections.unmodifiableList(resultList);
    }

    private S3Configuration readS3Section(ConfigurationSection section) {
        return S3Configuration.builder()
                .name(section.getName())
                .endpoint(section.getString("endpoint", "http://localhost:9000"))
                .region(section.getString("region", "eu-central-1"))
                .accessKeyId(section.getString("accessKeyId", "minioadmin"))
                .secretAccessKey(section.getString("secretAccessKey", "minioadmin"))
                .forcePathStyle(section.getBoolean("forcePathStyle", true))
                .build();
    }
}
