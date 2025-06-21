package org.skyisland.bedrockdata.config;

import lombok.Builder;

@Builder
public record DatabaseConfiguration(
        String name,
        String host,
        String database,
        Integer port,
        DatabaseType type,
        String user,
        String password) {

    public String getJdbcUrl() {
        return "jdbc:%s://%s:%d/%s".formatted(type.getDbmsType(), host, port, database);
    }

}
