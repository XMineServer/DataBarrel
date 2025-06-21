package org.skyisland.databarrel.sample;

import org.bukkit.plugin.java.JavaPlugin;
import org.skyisland.databarrel.DataBarrelService;
import org.skyisland.databarrel.DataBarrelTest;

public class SampleDataBarrelPlugin extends JavaPlugin {

    private static final String DATASOURCE_NAME = "mysql_database";

    @Override
    public void onEnable() {
        DataBarrelService service = getServer().getServicesManager().load(DataBarrelService.class);
        if (service == null) {
            getSLF4JLogger().error("Can't found BedrockDataService");
            return;
        }
        DataBarrelTest.testConnection(getSLF4JLogger(), DATASOURCE_NAME, service);
    }
}
