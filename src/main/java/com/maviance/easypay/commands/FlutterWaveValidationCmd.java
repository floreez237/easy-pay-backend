package com.maviance.easypay.commands;

import lombok.Data;

@Data
public class FlutterWaveValidationCmd {
    private String otp;
    private String flutterWaveRef;
    private String type;
}
