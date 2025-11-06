package org.skyisland.databarrel.datasource;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.skyisland.databarrel.config.MongoConfiguration;

public class MongoClientFactory {

    public MongoClient create(MongoConfiguration configuration) {
        var credentials = MongoCredential.createScramSha256Credential(
                configuration.user(),
                configuration.source(),
                configuration.password().toCharArray()
        );
        var settings = MongoClientSettings.builder()
                .credential(credentials)
                .applyToClusterSettings(builder -> builder
                        .hosts(configuration.servers())
                )
                .build();
        return MongoClients.create(settings);
    }

}
