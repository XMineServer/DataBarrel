package org.skyisland.databarrel;

import com.zaxxer.hikari.HikariDataSource;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.apache.curator.framework.CuratorFramework;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.skyisland.databarrel.config.DatabaseConfigReader;
import org.skyisland.databarrel.config.S3ConfigReader;
import org.skyisland.databarrel.config.ZooKeeperConfigReader;
import org.skyisland.databarrel.datasource.HikariDataSourceFactory;
import org.skyisland.databarrel.datasource.S3ClientFactory;
import org.skyisland.databarrel.datasource.ZooKeeperFactory;
import org.skyisland.databarrel.exception.BootstrapException;
import org.slf4j.Logger;
import software.amazon.awssdk.services.s3.S3Client;

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
    private static final S3ClientFactory S3_CLIENT_FACTORY = new S3ClientFactory();
    private static final ZooKeeperFactory ZOOKEEPER_FACTORY = new ZooKeeperFactory();
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
        var pluginConfig = loadConfig(configPath);
        var hikariDataSources = getDataSources(pluginConfig);
        var s3Clients = getS3Clients(pluginConfig);
        var zooKeeperClients = getZooKeepers(pluginConfig);
        bedrockDataService = new DataBarrelServiceImpl(hikariDataSources, s3Clients, zooKeeperClients);
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

    private Map<String, HikariDataSource> getDataSources(Configuration pluginConfig) {
        var databaseConfigReader = createDatabasesConfigReader(pluginConfig);
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
        return hikariDataSources;
    }

    private Map<String, S3Client> getS3Clients(Configuration pluginConfig) {
        var s3ConfigReader = createS3ConfigReader(pluginConfig);
        var s3Configurations = s3ConfigReader.loadConfigurations();
        Map<String, S3Client> s3Clients = new HashMap<>();
        for (var config : s3Configurations) {
            S3Client s3Client;
            try {
                s3Client = S3_CLIENT_FACTORY.S3ConfigFactory(config);

            } catch (Throwable t) {
                s3Clients.values().forEach(S3Client::close);
                throw new BootstrapException("Fail to load s3 config", t);
            }
            logger.info("Load {} S3Client", config.name());
            s3Clients.put(config.name(), s3Client);
        }
        return s3Clients;
    }

    private Map<String, CuratorFramework> getZooKeepers(Configuration pluginConfig) {
        var zooKeeperConfigReader = createZooKeeperConfigReader(pluginConfig);
        var zooKeeperConfigurations = zooKeeperConfigReader.loadConfigurations();
        Map<String, CuratorFramework> zooKeeperClients = new HashMap<>();
        for (var config : zooKeeperConfigurations) {
            CuratorFramework zooKeeperClient;
            try {
                zooKeeperClient = ZOOKEEPER_FACTORY.zooKeeperConfigFactory(config);
            } catch (Throwable t) {
                zooKeeperClients.values().forEach(CuratorFramework::close);
                throw new BootstrapException("Fail to load ZooKeeper config", t);
            }
            logger.info("Load {} ZooKeeper", config.name());
            zooKeeperClients.put(config.name(), zooKeeperClient);
        }
        return zooKeeperClients;
    }

    private DatabaseConfigReader createDatabasesConfigReader(Configuration config) {
        return new DatabaseConfigReader(logger, config.getConfigurationSection("databases"));
    }

    private S3ConfigReader createS3ConfigReader(Configuration config) {
        return new S3ConfigReader(logger, config.getConfigurationSection("s3"));
    }

    private ZooKeeperConfigReader createZooKeeperConfigReader(Configuration config) {
        return new ZooKeeperConfigReader(logger, config.getConfigurationSection("zookeeper"));
    }

    private Configuration loadConfig(Path configPath) {
        if (!Files.exists(configPath)) {
            copyDefaultConfig(configPath);
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configPath)))) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            throw new BootstrapException("Can't read config file", e);
        }
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
