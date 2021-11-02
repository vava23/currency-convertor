package com.github.vava23.currencyconvertor.domain;

import static java.text.MessageFormat.format;

import java.math.BigDecimal;
import java.util.List;

import com.github.vava23.currencyconvertor.client.RatesClientService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Currency convertion logic
 */
@Component
public class CurrencyConvertorService {
    private static final Logger log = LoggerFactory.getLogger(CurrencyConvertorService.class);
    /** Maximum acceptable amount, just to have some limit */
    private static final BigDecimal MAX_AMOUNT;
    /** Maximun amount string length */
    private static final int MAX_AMOUNT_LENGTH;
    /** Service for obtaining exchange rates */
    private RatesClientService ratesService;
    /** Supported currencies reference */
    @Autowired
    @Lazy
    private CurrencyReference currencyReference;

    static {
        MAX_AMOUNT = BigDecimal.valueOf(Long.MAX_VALUE);
        MAX_AMOUNT_LENGTH = MAX_AMOUNT.toString().length();
    }

    @Autowired
    public CurrencyConvertorService(RatesClientService ratesService) {
        this.ratesService = ratesService;
    }

    /**
     * Converts amount between specified currencies
     */
    public ConversionResult convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        log.info("Converting from {} {} to {}", amount, sourceCurrency, targetCurrency);

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
        return currencyReference.containsCurrency(currency.toUpperCase());
    }

    /**
     * Validates the money amount
     */
    public void validateAmount(BigDecimal amount) {
        if (amount == null)
            throw new IllegalArgumentException("Input amount not specified");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Input amount is negative");
    }

    /**
     * Validates the money amount
     */
    public void validateAmount(String amount) {
        if (amount == null)
            throw new IllegalArgumentException("Input amount not specified");
        if (amount.length() > MAX_AMOUNT_LENGTH) {
            throw new IllegalArgumentException(format("Input amount exceeds the maximum of {0} symbols", MAX_AMOUNT_LENGTH));
        }            
        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount);
        } catch (Exception e) {
            throw new IllegalArgumentException("Input amount cannot be parsed as a number");
        }
        if (amountValue.compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Input amount exceeds the maximum of " + MAX_AMOUNT.toString());
        }
        validateAmount(amountValue);
    }    

    /**
     * Validates exchange rate
     */
    public void validateRate(BigDecimal rate) {
        if (rate == null)
            throw new IllegalArgumentException("Input rate not specified");            
        if (rate.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(format("Input rate {0} is not positive", rate));
    }

    /**
     * Validates currency
     */
    public void validateCurrency(String currency) {
        if (currency == null)
            throw new IllegalArgumentException("Input currency not specified");
        if (StringUtils.isBlank(currency)) {
            throw new IllegalArgumentException("Input currency is empty");
        }
        if (!supportsCurrency(currency))
            throw new IllegalArgumentException(format("Currency {0} is not supported", currency));
    }    
}