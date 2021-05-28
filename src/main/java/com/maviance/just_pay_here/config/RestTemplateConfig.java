package com.maviance.just_pay_here.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${flutterwave.auth.token}")
    private String flutterWaveToken;

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization","Bearer ".concat(flutterWaveToken));
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
