package org.skyisland.databarrel.config;

import com.mongodb.ServerAddress;
import lombok.Builder;

import java.util.List;

@Builder
public record MongoConfiguration(
        String name,
        String source,
        String user,
        String password,
        List<ServerAddress> servers
) {
}
