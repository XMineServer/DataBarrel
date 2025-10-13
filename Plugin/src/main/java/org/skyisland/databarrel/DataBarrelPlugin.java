package org.skyisland.databarrel;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class DataBarrelPlugin extends JavaPlugin {

    private final DataBarrelServiceImpl bedrockDataService;

    DataBarrelPlugin(DataBarrelServiceImpl bedrockDataService) {
        this.bedrockDataService = bedrockDataService;
    }

    @Override
    public void onEnable() {
        getServer().getServicesManager().register(
                DataBarrelService.class,
                bedrockDataService,
                this,
                ServicePriority.Highest);
    }

}
