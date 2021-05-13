package com.maviance.easypay.config;

import com.maviance.easypay.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiClient;
import org.maviance.s3pjavaclient.api.ChecksApi;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.api.HistoryApi;
import org.maviance.s3pjavaclient.api.MasterdataApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class S3PConfig {
    @Value("${s3p.base.url}")
    private String baseUrl;
    @Value("${s3p.access.token}")
    private String accessToken;
    @Value("${s3p.access.secret}")
    private String accessSecret;
    @Value("${s3p.client.debug}")
    private boolean debug;


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ApiClient apiClient() {
        final ApiClient apiClient = new ApiClient(baseUrl, accessToken, accessSecret);
        apiClient.setDebugging(debug);
        return apiClient;

    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CollectionApi collectionApi(ApiClient apiClient) {
        return new CollectionApi(apiClient);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ChecksApi checksApi(ApiClient apiClient) {
        return new ChecksApi(apiClient);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public HistoryApi historyApi(ApiClient apiClient) {
        return new HistoryApi(apiClient);
    }


    @Bean
    public CommandLineRunner runner(ApiClient apiClient, Checks checks) {

        MasterdataApi masterdataApi = new MasterdataApi(apiClient);
        return args -> {
            if (checks.isS3pAvailable()) {
                Constants.SERVICES.addAll(masterdataApi.serviceGet());
                Constants.MERCHANTS.addAll(masterdataApi.merchantGet());
            } else {
                throw new RuntimeException("S3P is not available");
            }
        };
    }

}
