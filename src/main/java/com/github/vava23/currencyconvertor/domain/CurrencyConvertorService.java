package com.github.vava23.currencyconvertor.domain;

import java.math.BigDecimal;

/**
 * Currency converting logic
 */
public class CurrencyConvertorService {
    public CurrencyConvertorService() {        
    }

    /**
     * Converts amount between currencies using rate specified
     */
    public BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        // Input checks
        if (amount == null)
            throw new IllegalArgumentException("Input amount is null");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Input amount is negative");
        if (rate == null)
            throw new IllegalArgumentException("Input rate is null");            
        if (rate.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Input rate is not positive");

        return amount.multiply(rate).stripTrailingZeros();
    }
}