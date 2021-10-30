package com.github.vava23.currencyconvertor.client;

/**
 * Custom exception for client service checks
 */
public class RatesClientException extends RuntimeException {

    public RatesClientException(String message) {
        super(message);
    }

    public RatesClientException(String message, Throwable cause) {
        super(message, cause);
    }}