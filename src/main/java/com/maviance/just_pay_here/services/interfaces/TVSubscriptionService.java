package com.maviance.just_pay_here.services.interfaces;

import com.maviance.just_pay_here.commands.TVSubscriptionPaymentCmd;
import com.maviance.just_pay_here.model.SubscriptionOffer;

import java.util.List;

public interface TVSubscriptionService {
    List<SubscriptionOffer> getAllOffers(String providerName);

    String payTvOffer(TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd,String sourcePTN);
}
