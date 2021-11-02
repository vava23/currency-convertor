package com.github.vava23.currencyconvertor.domain;

/**
 * Error data object
 */
public class ConversionError {
    private String status;
    private String message;

    public ConversionError(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }    
}