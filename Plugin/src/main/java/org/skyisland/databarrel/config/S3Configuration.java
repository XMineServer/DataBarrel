package org.skyisland.databarrel.config;

import lombok.Builder;

@Builder
public record S3Configuration(
        String name,
        String endpoint,
        String region,
        String accessKeyId,
        String secretAccessKey,
        boolean forcePathStyle
) {
}
