package com.github.vava23.currencyconvertor.domain;

import java.math.BigDecimal;

/**
 * Result data object
 */
public class ConversionResult {
    private String status;
    private String currency;
    private BigDecimal amount;

    public ConversionResult(String status, String currency, BigDecimal amount) {
        this.status = status;
        this.currency = currency;
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
