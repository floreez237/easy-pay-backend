package com.maviance.easypay.model;

import com.maviance.easypay.commands.BasePaymentCmd;
import com.maviance.easypay.commands.CashOutCommand;
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
    private String source;
    @Column(name = "source_service_number")
    private String sourceServiceNumber;
    private String destination;
    @Column(name = "destination_service_number")
    private String destinationServiceNumber;
    private Float amount;
    private String sourcePTN;
    private String destinationPTN;
    @Enumerated(EnumType.STRING)
    private Request.Status status;
    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "CVC", column = @Column(name = "source_cvc")),
            @AttributeOverride(name = "cardholderName", column = @Column(name = "source_cardholder_name")),
            @AttributeOverride(name = "expiryDate", column = @Column(name = "source_expiry_date"))})
    private CardDetails sourceCardDetails;

    public enum Status {
        SUCCESS, PENDING, ERROR;
    }

    public void configWithPaymentCommand(BasePaymentCmd command) {
        amount = command.getAmount();
        destination = command.getDestination();
        destinationServiceNumber = command.getDestinationServiceNumber();

    }

    public void configWithCashOutCommand(CashOutCommand cashOutCommand) {
        source = cashOutCommand.getSource();
        sourceServiceNumber = cashOutCommand.getSourceServiceNumber();
        sourceCardDetails = cashOutCommand.getSourceCardDetails();
    }

}
