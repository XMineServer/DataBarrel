package org.skyisland.databarrel.sample;

import org.bukkit.plugin.java.JavaPlugin;
import org.skyisland.databarrel.DataBarrelService;
import org.skyisland.databarrel.DataBarrelTest;

public class SampleDataBarrelPlugin extends JavaPlugin {

    private static final String DATASOURCE_NAME = "mysql_database";
    private static final String S3_CLIENT_NAME = "default";
    private static final String ZOOKEEPER_NAME = "default";

    @Override
    public void onEnable() {
        DataBarrelService service = getServer().getServicesManager().load(DataBarrelService.class);
        if (service == null) {
            getSLF4JLogger().error("Can't found BedrockDataService");
            return;
        }
        DataBarrelTest.testSqlConnection(getSLF4JLogger(), DATASOURCE_NAME, service);
        DataBarrelTest.testS3Connection(getSLF4JLogger(), S3_CLIENT_NAME, service);
        DataBarrelTest.testZooKeeperConnection(getSLF4JLogger(), ZOOKEEPER_NAME, service);
    }
}
