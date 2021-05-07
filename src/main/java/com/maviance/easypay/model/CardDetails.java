package com.maviance.easypay.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.util.Date;


@Embeddable
@Data
public class CardDetails {
    private Integer CVC;
    private String cardholderName;
    private Date expiryDate;
}
