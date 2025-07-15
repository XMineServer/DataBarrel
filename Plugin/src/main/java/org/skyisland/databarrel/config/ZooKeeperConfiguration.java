package org.skyisland.databarrel.config;

import lombok.Builder;

import java.util.Collection;

@Builder
public record ZooKeeperConfiguration(
        String name,
        Collection<String> quorum,
        Integer sessionTimeout,
        String namespace,
        RetryConfiguration retry
) {
    @Builder
    public record RetryConfiguration(
        Integer maxRetries,
        Integer intervalMs
    ) {
    }
}
