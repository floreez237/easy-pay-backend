package com.maviance.easypay.config;

import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.ChecksApi;
import org.maviance.s3pjavaclient.model.Ping;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Checks {
    private final ChecksApi checksApi;

    public Checks(ChecksApi checksApi) {
        this.checksApi = checksApi;
    }

    public boolean isS3pAvailable() {
        try {
            Ping ping = checksApi.pingGet();
            return ping != null;
        } catch (ApiException e) {
            log.error(e.getResponseBody());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return false;

    }
}
