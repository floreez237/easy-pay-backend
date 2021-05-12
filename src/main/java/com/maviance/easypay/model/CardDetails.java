package com.maviance.easypay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;
import java.util.Date;


@Embeddable
@Data
@NoArgsConstructor
@Slf4j
public class CardDetails {
    private Integer CVC;
    private String cardholderName;
    private Date expiryDate;

}
