package com.github.vava23.currencyconvertor.rest;

/**
 * Custom exception for incorrect input parameters
 */
public class IncorrectInputException extends RuntimeException {

    public IncorrectInputException(String message) {
        super(message);
    }

    public IncorrectInputException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
