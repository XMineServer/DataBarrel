package org.skyisland.databarrel.datasource;

import org.skyisland.databarrel.config.S3Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class S3ClientFactory {
    public S3Client S3ConfigFactory(S3Configuration configuration) {
        return S3Client.builder()
                .endpointOverride(URI.create(configuration.endpoint()))
                .region(Region.of(configuration.region()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        configuration.accessKeyId(),
                                        configuration.secretAccessKey())))
                .forcePathStyle(configuration.forcePathStyle())
                .build();
    }
}
