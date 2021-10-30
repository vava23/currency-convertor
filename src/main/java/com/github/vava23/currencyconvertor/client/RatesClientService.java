package com.github.vava23.currencyconvertor.client;

import static java.text.MessageFormat.format;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Wrapper client service for Exchange rates API
 */
@Component
public class RatesClientService {
    private static final String RATES_VIEW = "/v1/latest";
    private static final String SYMBOLS_VIEW = "/v1/symbols";
    @Value("${rates.host}")
    private String host;
    @Value("${rates.api.key}")
    private String apiKey;
    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    private void init() {
        // Get API key from env variable or properties (in order of precedence)
        String apiKeyEnv = System.getenv("apiKey");
        if (StringUtils.hasText(apiKeyEnv)) {
            apiKey = apiKeyEnv;
        }
    }

    /**
     * Retrieves the rate of source currency to target currency
     */
    public BigDecimal getRate(String sourceCurrency, String targetCurrency) {
        // Checks
        Objects.requireNonNull(sourceCurrency, "Source Currency in null");
        Objects.requireNonNull(targetCurrency, "Target Currency in null");

        // Get rates from server
        String url = format(
            "http://{0}{1}?access_key={2}&base={3}&symbols={4}", 
            host,
            RATES_VIEW,
            apiKey,
            sourceCurrency,
            targetCurrency);
        ExchangeRatesResult ratesResult;
        try {
            ratesResult = restTemplate.getForObject(url, ExchangeRatesResult.class);
        } catch (RestClientException e) {
            throw new RatesClientException(format("Failed to retrieve rates for currency {0} from {1}", targetCurrency, url), e);
        }

        // Extract and return the rate
        if (ratesResult != null && ratesResult.isSuccess()) {
            return ratesResult.getRates().get(targetCurrency);
        } else {
            throw new RatesClientException(format("Failed to retrieve rates for currency {0} from {1}", targetCurrency, url));
        }
    }

    /**
     * Get list if available currencies
     */
    public Set<String> getAvailableCurrencies() {
        // Get symbols from server
        String url = format(
            "http://{0}{1}?access_key={2}",
            host,
            SYMBOLS_VIEW,
            apiKey);
        SupportedSymbolsResult symbolsResult;
        try {
            symbolsResult = restTemplate.getForObject(url, SupportedSymbolsResult.class);
        } catch (RestClientException e) {
            throw new RatesClientException(format("Failed to retrieve supported currencies from {0}", url), e);
        }
        
        // Extract and return the symbols list
        if (symbolsResult != null && symbolsResult.isSuccess()) {
            return symbolsResult.getSymbols().keySet();
        } else {
            throw new RatesClientException("Failed to retrieve supported currencies");
        }
    }
    
}

