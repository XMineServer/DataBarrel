package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.skyisland.databarrel.config.DatabaseConfigReader;
import org.skyisland.databarrel.datasource.HikariDataSourceFactory;
import org.skyisland.databarrel.exception.BootstrapException;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class DataBarrelBootstrap implements PluginBootstrap {

    private static final HikariDataSourceFactory HIKARI_DATA_SOURCE_FACTORY = new HikariDataSourceFactory();
    static DataBarrelServiceImpl bedrockDataService;
    private Logger logger;

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new DataBarrelPlugin(bedrockDataService);
    }

    @Override
    public void bootstrap(@NotNull BootstrapContext bootstrapContext) {
        this.logger = bootstrapContext.getLogger();
        var configPath = bootstrapContext.getDataDirectory().resolve("config.yml");
        var databaseConfigReader = createConfigReader(configPath);
        var databaseConfigurations = databaseConfigReader.loadConfigurations();
        Map<String, HikariDataSource> hikariDataSources = new HashMap<>();
        for (var config : databaseConfigurations) {
            HikariDataSource dataSource;
            try {
                dataSource = HIKARI_DATA_SOURCE_FACTORY.create(config);
            } catch (Throwable t) {
                hikariDataSources.values().forEach(HikariDataSource::close);
                throw new BootstrapException("Fail to load hikari datasource", t);
            }
            logger.info("Load {} DataSource", config.name());
            hikariDataSources.put(config.name(), dataSource);
        }
        bedrockDataService = new DataBarrelServiceImpl(hikariDataSources);
        registerShutdownHook();
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (bedrockDataService.isOpen()) {
                bedrockDataService.close();
                logger.warn("Disable DataBarrelService form shutdownHook");
            }
        }));
    }

    private DatabaseConfigReader createConfigReader(Path configPath) {
        if (!Files.exists(configPath)) {
            copyDefaultConfig(configPath);
        }
        YamlConfiguration configuration;
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configPath)))) {
            configuration = YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            throw new BootstrapException("Can't read config file", e);
        }
        ConfigurationSection section = configuration.getConfigurationSection("databases");
        return new DatabaseConfigReader(logger, section);
    }

    private void copyDefaultConfig(Path path) {
        URL configResource = getClass().getResource("/config.yml");
        if (configResource == null) {
            throw new InternalError("Can't found config.yml resource");
        }
        try {
            Files.createDirectories(path.getParent());
            try (InputStream is = new BufferedInputStream(configResource.openStream())) {
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new BootstrapException("Can't read config file", e);
        }
    }

}
