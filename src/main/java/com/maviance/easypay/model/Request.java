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
//    @CreatedDate
    @Column(name = "date_created")
    private Date dateCreated = new Date();
    private String source;
    @Column(name = "source_service_number")
    private String sourceServiceNumber;
    @Column(name = "amount_debited_from_source")
    private Float amountDebitedFromSource;
    private String destination;
    @Column(name = "destination_service_number")
    private String destinationServiceNumber;
    @Column(name = "amount_credited_in_destination")
    private Float amountCreditedInDestination;
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
        SUCCESS, PENDING, ERROR,REIMBURSED;
    }

    public void configWithPaymentCommand(BasePaymentCmd command) {
        amountDebitedFromSource = command.getAmount();
        destination = command.getDestination();
        destinationServiceNumber = command.getDestinationServiceNumber();

    }

    public void configWithCashOutCommand(CashOutCommand cashOutCommand) {
        source = cashOutCommand.getSource();
        sourceServiceNumber = cashOutCommand.getSourceServiceNumber();
        sourceCardDetails = cashOutCommand.getSourceCardDetails();
        amountDebitedFromSource = cashOutCommand.getAmountWithFee();
    }

}
