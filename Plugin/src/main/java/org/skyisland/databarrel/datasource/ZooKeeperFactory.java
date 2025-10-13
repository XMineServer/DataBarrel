package org.skyisland.databarrel.datasource;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.skyisland.databarrel.config.ZooKeeperConfiguration;

public class ZooKeeperFactory implements ProviderFactory<ZooKeeperConfiguration, CuratorFramework> {

    @Override
    public CuratorFramework create(ZooKeeperConfiguration configuration) {
        var curator = CuratorFrameworkFactory.builder()
                .connectString(String.join(",", configuration.quorum()))
                .sessionTimeoutMs(configuration.sessionTimeout())
                .namespace(configuration.namespace())
                .retryPolicy(new RetryNTimes(
                        configuration.retry().maxRetries(),
                        configuration.retry().intervalMs()))
                .build();
        curator.start();
        return curator;
    }
}
