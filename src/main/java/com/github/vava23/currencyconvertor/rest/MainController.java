package com.github.vava23.currencyconvertor.rest;

import java.math.BigDecimal;

import com.github.vava23.currencyconvertor.domain.ConversionResult;
import com.github.vava23.currencyconvertor.domain.CurrencyConvertorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    CurrencyConvertorService currencyConvertorService;
    
    /**
     * Currency Conversion endpoint
     */
    @GetMapping(params = {"source_currency","target_currency","amount"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ConversionResult convert(
        @RequestParam("source_currency") String sourceCurrencyParam,
        @RequestParam("target_currency") String targetCurrencyParam,
        @RequestParam("amount") String amountParam
    ) {
        // Validate params and return client error for incorrect params
        // All other exceptions will be considered unexpected
        try {
            currencyConvertorService.validateAmount(amountParam);
            currencyConvertorService.validateCurrency(sourceCurrencyParam);
            currencyConvertorService.validateCurrency(targetCurrencyParam);
        } catch (IllegalArgumentException e) {
            throw new IncorrectInputException(e.getMessage(), e);
        }

        // Calculate and return
        return currencyConvertorService.convert(
            new BigDecimal(amountParam), 
            sourceCurrencyParam, 
            targetCurrencyParam);
    }

    /**
     * Return for all unhandled exceptions
     */
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String onException() {
        return "Internal server error";
    }

    /**
     * Return for validation errors
     */
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectInputException.class)
    public String onIncorrectInputException(IncorrectInputException e) {
        return "Incorrect input (" + StringUtils.uncapitalize(e.getMessage()) + ")";
    }    
}
