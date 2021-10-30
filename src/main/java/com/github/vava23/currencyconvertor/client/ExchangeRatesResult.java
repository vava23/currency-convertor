package com.github.vava23.currencyconvertor.client;

import java.util.Map;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Exchange Rates response data
 */
public class ExchangeRatesResult implements Serializable {
    private boolean success;
    private long timestamp;
    private String base;
    private LocalDate date;
    private Map<String, BigDecimal> rates;

    public boolean isSuccess() {
        return success;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getBase() {
        return base;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public Map<String, BigDecimal> getRates() {
        return rates;
    }
}
