package org.skyisland.databarrel.config;

import java.util.List;

public record RedisConfiguration(
        boolean isCluster,
        String host,
        int port,
        String user,
        String password,
        List<Node> clusterServers
) {
    public record Node(
            String host,
            int port
    ) {
    }
}
