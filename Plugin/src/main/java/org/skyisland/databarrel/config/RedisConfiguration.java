package org.skyisland.databarrel.config;

import lombok.Builder;

import java.util.List;

@Builder
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
