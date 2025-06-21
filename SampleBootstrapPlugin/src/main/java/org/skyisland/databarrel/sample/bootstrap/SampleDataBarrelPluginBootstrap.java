package org.skyisland.databarrel.sample.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;
import org.skyisland.databarrel.DataBarrelService;
import org.skyisland.databarrel.DataBarrelTest;

@SuppressWarnings("UnstableApiUsage")
public class SampleDataBarrelPluginBootstrap implements PluginBootstrap {

    private static final String DATASOURCE_NAME = "mysql_database";

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        DataBarrelTest.testConnection(context.getLogger(), DATASOURCE_NAME, DataBarrelService.getInstance());
    }
}
