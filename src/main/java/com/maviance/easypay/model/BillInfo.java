package com.maviance.easypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BillInfo {
    private String billId;
    private Float billAmount;
    private Date billGeneratedDate;
    private Date billDueDate;
}
