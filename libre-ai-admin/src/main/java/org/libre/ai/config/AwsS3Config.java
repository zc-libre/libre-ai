package org.libre.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Config {

	private String accessKeyId;

	private String secretAccessKey;

	private String bucketName;

	private String region;

	private String endpoint;

	private boolean pathStyleAccess = false;

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

		S3ClientBuilder builder = S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(credentials));

		if (endpoint != null && !endpoint.isEmpty()) {
			builder.endpointOverride(URI.create(endpoint)).forcePathStyle(pathStyleAccess);
		}

		if (region != null && !region.isEmpty()) {
			builder.region(Region.of(region));
		}

		return builder.build();
	}

}