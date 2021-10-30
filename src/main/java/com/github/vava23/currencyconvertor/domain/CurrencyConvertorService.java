package com.github.vava23.currencyconvertor.domain;

import java.math.BigDecimal;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.vava23.currencyconvertor.client.RatesClientService;

/**
 * Currency convertion logic
 */
@Component
public class CurrencyConvertorService {
    /** Available currency codes */
    private Set<String> availableCurrencies = new HashSet<>();
    /** Service for obtaining exchange rates */
    RatesClientService ratesService;

    @Autowired
    public CurrencyConvertorService(RatesClientService ratesService) {
        this.ratesService = ratesService;
        this.setCurrencies(this.ratesService.getAvailableCurrencies());
    }

    /**
     * Sets the supported currencies
     */
    private void setCurrencies(Set<String> currencies) {
        if (currencies == null)
            throw new IllegalArgumentException("List of currencies is null");

        // Convert values to uppercase
        this.availableCurrencies = currencies.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }

    /**
     * Converts amount between specified currencies
     */
    public ConversionResult convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        // Prepare the currency codes just in case
        sourceCurrency = sourceCurrency.toUpperCase();
        targetCurrency = targetCurrency.toUpperCase();

        // Validate inputs
        List.of(sourceCurrency, targetCurrency).forEach(this::validateCurrency);
        validateAmount(amount);

        // Get rate
        BigDecimal rate;
        if (sourceCurrency.equals(targetCurrency)) {
            // Avoid unneccessary API calls
            rate = BigDecimal.ONE;
        } else {
            rate = ratesService.getRate(sourceCurrency, targetCurrency);
        }

        // Calculate and return the result
        BigDecimal resultAmount = this.convert(amount, rate);
        return new ConversionResult(
            "success",
            targetCurrency,
            resultAmount            
            );
    }

    /**
     * Converts amount between currencies using rate specified
     */
    public BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        // Input checks
        validateAmount(amount);
        validateRate(rate);

        return amount.multiply(rate).stripTrailingZeros();
    }    

    /**
     * Checks if a currency is supported for conversion
     */
    public boolean supportsCurrency(String currency) {
        return availableCurrencies.contains(currency.toUpperCase());
    }

    /**
     * Validates the money amount
     */
    public void validateAmount(BigDecimal amount) {
        if (amount == null)
            throw new IllegalArgumentException("Input amount is null");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Input amount is negative");
    }

    /**
     * Validates the money amount
     */
    public void validateAmount(String amount) {
        if (amount == null)
            throw new IllegalArgumentException("Input amount is null");
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input amount cannot be parsed");
        }
        validateAmount(amountValue);
    }    

    /**
     * Validates exchange rate
     */
    public void validateRate(BigDecimal rate) {
        if (rate == null)
            throw new IllegalArgumentException("Input rate is null");            
        if (rate.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(format("Input rate {0} is not positive", rate));
    }

    /**
     * Validates currency
     */
    public void validateCurrency(String currency) {
        if (currency == null)
            throw new IllegalArgumentException("Input currency is null");
        if (StringUtils.isBlank(currency)) {
            throw new IllegalArgumentException("Input currency is empty");
        }
        if (!supportsCurrency(currency))
            throw new IllegalArgumentException(format("Currency {0} is not supported", currency));
    }    
}