package com.maviance.just_pay_here.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.maviance.s3pjavaclient.model.Bill;
import org.threeten.bp.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class BillInfo {
    private String billId;
    private String billPayItemId;
    private Float billAmount;
    private String billGeneratedDate;
    private String billDueDate;

    public BillInfo(Bill s3pBill) {
        billId = s3pBill.getBillNumber();
        billAmount = s3pBill.getAmountLocalCur();
        billGeneratedDate = s3pBill.getBillDate().format(DateTimeFormatter.ofPattern("dd-MM-yyy"));
        billDueDate = s3pBill.getBillDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyy"));
        billPayItemId = s3pBill.getPayItemId();
    }
}
