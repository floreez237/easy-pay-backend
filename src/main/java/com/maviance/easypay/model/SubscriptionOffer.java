package com.maviance.easypay.model;

import lombok.Getter;
import lombok.ToString;
import org.maviance.s3pjavaclient.model.Product;

@Getter
@ToString
public class SubscriptionOffer {
    private final String title;
    private final String offerId;
    private final Float cost;

    public SubscriptionOffer(Product product) {
        title = product.getName();
        cost = product.getAmountLocalCur();
        offerId = product.getPayItemId();
    }
}
