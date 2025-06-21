package org.skyisland.bedrockdata;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class BedrockDataPlugin extends JavaPlugin {

    private final BedrockDataServiceImpl bedrockDataService;

    BedrockDataPlugin(BedrockDataServiceImpl bedrockDataService) {
        this.bedrockDataService = bedrockDataService;
    }

    @Override
    public void onEnable() {
        getServer().getServicesManager().register(
                BedrockDataService.class,
                bedrockDataService,
                this,
                ServicePriority.Highest);
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregister(bedrockDataService);
        if (bedrockDataService.isOpen()) {
            bedrockDataService.close();
        }
    }

}
