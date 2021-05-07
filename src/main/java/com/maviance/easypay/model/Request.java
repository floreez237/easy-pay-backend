package com.maviance.easypay.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue
    @Column(name = "request_id")
    private UUID requestId;
    private Float fees;
    @CreatedDate
    @Column(name = "date_created")
    private Date dateCreated;
    private  String source;
    @Column(name = "source_service_number")
    private String sourceServiceNumber;
    private String destination;
    @Column(name = "destination_service_number")
    private String destinationServiceNumber;
    private Float amount;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "CVC",column = @Column(name = "source_cvc")),
            @AttributeOverride(name = "cardholderName",column = @Column(name = "source_cardholder_name")),
            @AttributeOverride(name = "expiryDate",column = @Column(name = "source_expiry_date"))})
    private CardDetails sourceCardDetails;
}
