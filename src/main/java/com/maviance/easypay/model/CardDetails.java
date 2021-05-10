package com.maviance.easypay.model;

import com.maviance.easypay.exceptions.CustomException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.persistence.Embeddable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


@Embeddable
@Data
@NoArgsConstructor
@Slf4j
public class CardDetails {
    private Integer CVC;
    private String cardholderName;
    private Date expiryDate;

    public CardDetails(Map<String, String> params) {
        this.CVC = Integer.valueOf(params.get("cvc"));
        this.cardholderName = params.get("cardholderName");
        try {
            this.expiryDate = new SimpleDateFormat("dd/MM/yy").parse(params.get("expiryDate"));
        } catch (ParseException e) {
            log.error(e.getMessage());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
