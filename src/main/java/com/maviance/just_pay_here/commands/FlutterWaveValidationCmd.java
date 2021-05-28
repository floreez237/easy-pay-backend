package com.maviance.just_pay_here.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FlutterWaveValidationCmd {
    private String otp;
    @JsonProperty("flw_ref")
    private String flutterWaveRef;
    private String type;
}
