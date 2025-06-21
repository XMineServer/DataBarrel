package org.skyisland.bedrockdata.sample.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;
import org.skyisland.bedrockdata.BedrockDataService;
import org.skyisland.bedrockdata.BedrockDataTest;

@SuppressWarnings("UnstableApiUsage")
public class SampleBedrockDataPluginBootstrap implements PluginBootstrap {

    private static final String DATASOURCE_NAME = "mysql_database";

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        BedrockDataTest.testConnection(context.getLogger(), DATASOURCE_NAME, BedrockDataService.getInstance());
    }
}
