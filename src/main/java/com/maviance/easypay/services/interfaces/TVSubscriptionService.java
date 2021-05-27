package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.TVSubscriptionPaymentCmd;
import com.maviance.easypay.model.SubscriptionOffer;

import java.util.List;

public interface TVSubscriptionService {
    List<SubscriptionOffer> getAllOffers(String providerName);

    String payTvOffer(TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd,String sourcePTN);
}
