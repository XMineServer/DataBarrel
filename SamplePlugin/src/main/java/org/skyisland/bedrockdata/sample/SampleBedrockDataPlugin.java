package org.skyisland.bedrockdata.sample;

import org.bukkit.plugin.java.JavaPlugin;
import org.skyisland.bedrockdata.BedrockDataService;
import org.skyisland.bedrockdata.BedrockDataTest;

public class SampleBedrockDataPlugin extends JavaPlugin {

    private static final String DATASOURCE_NAME = "mysql_database";

    @Override
    public void onEnable() {
        BedrockDataService service = getServer().getServicesManager().load(BedrockDataService.class);
        if (service == null) {
            getSLF4JLogger().error("Can't found BedrockDataService");
            return;
        }
        BedrockDataTest.testConnection(getSLF4JLogger(), DATASOURCE_NAME, service);
    }
}
