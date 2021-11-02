package com.github.vava23.currencyconvertor.rest;

import java.math.BigDecimal;

import com.github.vava23.currencyconvertor.domain.ConversionError;
import com.github.vava23.currencyconvertor.domain.ConversionResult;
import com.github.vava23.currencyconvertor.domain.CurrencyConvertorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @Autowired
    CurrencyConvertorService currencyConvertorService;
    
    /**
     * Currency Conversion endpoint
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ConversionResult convert(
        @RequestParam(name="source_currency", required=false) String sourceCurrencyParam,
        @RequestParam(name="target_currency", required=false) String targetCurrencyParam,
        @RequestParam(name="amount", required=false) String amountParam
    ) {
        log.info("Incoming request: source_currency={}, target_currency={}, amount={}", sourceCurrencyParam, targetCurrencyParam, amountParam);
        
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
        ConversionResult result = currencyConvertorService.convert(
            new BigDecimal(amountParam), 
            sourceCurrencyParam, 
            targetCurrencyParam);
        log.info("Returning result: {} {}", result.getAmount(), result.getCurrency());
        return result;
    }

    /**
     * Return for all unhandled exceptions
     */
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ConversionError onException(Exception e) {
        log.error("Unexpected {}: {}", e.getClass().getName(), e.getMessage());
        return new ConversionError("error", "Unexpected server error occured. Please try again later");
    }

    /**
     * Return for validation errors
     */
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectInputException.class)
    public ConversionError onIncorrectInputException(IncorrectInputException e) {
        log.warn("Returning error: parameter validation failed ({})", e.getMessage());
        return new ConversionError("error", "Incorrect input parameter specified (" + StringUtils.uncapitalize(e.getMessage()) + ")");
    }    
}
