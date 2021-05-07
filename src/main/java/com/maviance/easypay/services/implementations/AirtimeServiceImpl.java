package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.AirtimeCmd;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.AirtimeService;
import com.maviance.easypay.utils.Constants;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AirtimeServiceImpl implements AirtimeService {

    private final RequestRepo requestRepo;
    private final CollectionApi collectionApi;
    private final Checks checks;

    public AirtimeServiceImpl(RequestRepo requestRepo, CollectionApi collectionApi, Checks checks) {
        this.requestRepo = requestRepo;
        this.collectionApi = collectionApi;
        this.checks = checks;
    }

    @Override
    public String executeTopup(AirtimeCmd airtimeCmd) {
        if (checks.isS3pAvailable()) {
            Set<org.maviance.s3pjavaclient.model.Service> services = Constants.services;
            
        }
        return null;
    }
}
