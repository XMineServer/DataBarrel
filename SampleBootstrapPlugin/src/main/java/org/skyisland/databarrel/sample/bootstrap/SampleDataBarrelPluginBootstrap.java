package org.skyisland.databarrel.sample.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;
import org.skyisland.databarrel.DataBarrelService;
import org.skyisland.databarrel.DataBarrelTest;

@SuppressWarnings("UnstableApiUsage")
public class SampleDataBarrelPluginBootstrap implements PluginBootstrap {

    private static final String DATASOURCE_NAME = "mysql_database";
    private static final String S3_CLIENT_NAME = "default";
    private static final String ZOOKEEPER_NAME = "default";
    private static final String JEDIS_PROVIDER_NAME = "default";

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        DataBarrelTest.testSqlConnection(context.getLogger(), DATASOURCE_NAME, DataBarrelService.getInstance());
        DataBarrelTest.testS3Connection(context.getLogger(), S3_CLIENT_NAME, DataBarrelService.getInstance());
        DataBarrelTest.testZooKeeperConnection(context.getLogger(), ZOOKEEPER_NAME, DataBarrelService.getInstance());
        DataBarrelTest.testJedisConnection(context.getLogger(), JEDIS_PROVIDER_NAME, DataBarrelService.getInstance());
    }
}
