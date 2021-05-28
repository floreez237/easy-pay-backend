package com.maviance.just_pay_here.exceptions;

import lombok.Getter;

@Getter
public class ErrorMessage {
    private final String message;

    public ErrorMessage(CustomException exception) {
        message = exception.getMessage();
    }

    public ErrorMessage(String message) {
        this.message = message;
    }
}
