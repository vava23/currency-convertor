package com.github.vava23.currencyconvertor.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.vava23.currencyconvertor.client.RatesClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Reference of currencies with lazy initialization
 (* to avoid dependency on Rates API on startup)
 */
@Component
@Lazy
public class CurrencyReference {
    /** Service for obtaining exchange rates */
    RatesClientService ratesService;
    /** Available currency codes, updated daily if requested */
    private Set<String> currencies = new HashSet<>();
    /** Date of last currencies list update */
    private LocalDate lastCurrenciesUpdate;

    @Autowired
    public CurrencyReference(RatesClientService ratesService) {
        this.ratesService = ratesService;
        this.setCurrencies(this.ratesService.getAvailableCurrencies());        
    }

    /**
     * Checks if reference contains specified currency
     */
    public boolean containsCurrency(String currency) {
        updateCurrencies();
        return currencies.contains(currency);
    }

    /**
     * Sets the supported currencies
     */
    private void setCurrencies(Set<String> currencies) {
        if (currencies == null)
            throw new IllegalArgumentException("List of currencies is null");

        // Convert values to uppercase
        this.currencies = currencies.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        // Remember the update date
        lastCurrenciesUpdate = LocalDate.now();
    }    

    /**
     * 
     */
    private void updateCurrencies() {
        // If list of currencies is old, update it first
        if (LocalDate.now().isAfter(lastCurrenciesUpdate))
            this.setCurrencies(ratesService.getAvailableCurrencies());
    }
}
