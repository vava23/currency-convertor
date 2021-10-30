package com.github.vava23.currencyconvertor.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports={8090})
public class RatesClientServiceTest {
    @Autowired
    private RatesClientService ratesClientService;
    private MockServerClient client;
    private final String API_KEY = "f10cYXWBj0eYfBzCYcsSGw%3d%3d";
    private final String RATES_VIEW = "/v1/latest";
    private final String SYMBOLS_VIEW = "/v1/symbols";
    
    public RatesClientServiceTest(MockServerClient client) throws IOException, URISyntaxException {
        this.client = client;
        mockExchangeRates();
        mockSymbols();
    }

    /**
     * Mock Exchange Rates view response
     */
    private void mockExchangeRates() {
        String responseBody;
        try {
            responseBody = Files.readString(Path.of(getClass().getClassLoader().getResource("json/ExchangeRatesResponse.json").toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        client
                .when(request().withPath(RATES_VIEW)
                    .withMethod("GET")
                    .withQueryStringParameter("access_key", API_KEY)
                    .withQueryStringParameter("base", "USD")
                    .withQueryStringParameter("symbols", "GBP"))
                .respond(response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(responseBody));
    }

    /**
     * Mock Symbols view response
     */
    private void mockSymbols() {
        String responseBody;
        try {
            responseBody = Files.readString(Path.of(getClass().getClassLoader().getResource("json/SymbolsResponse.json").toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        client
                .when(request().withPath(SYMBOLS_VIEW)
                    .withMethod("GET")
                    .withQueryStringParameter("access_key", API_KEY))
                .respond(response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(responseBody));
    }

    @Test
    void testGetAvailableCurrenciesSuccess() {
        Set<String> currencies = ratesClientService.getAvailableCurrencies();
        assertTrue(currencies.contains("USD"));
        assertTrue(currencies.contains("EUR"));
        assertFalse(currencies.contains("123"));
        assertFalse(currencies.contains(""));
    }

    @Test
    void testGetRateSuccess() {
        assertEquals(new BigDecimal("0.72007"), ratesClientService.getRate("USD", "GBP"));
        assertEquals(BigDecimal.ONE, ratesClientService.getRate("USD", "USD"));
        assertEquals(BigDecimal.ZERO, ratesClientService.getRate("gbp", "GBP"));
    }

    @Test
    void testGetRateFail() {
        assertThrows(RatesClientException.class, () -> ratesClientService.getRate("USD", "123"));
        assertThrows(RatesClientException.class, () -> ratesClientService.getRate("123", "USD"));
        assertThrows(NullPointerException.class, () -> ratesClientService.getRate(null, "GBP"));
        assertThrows(NullPointerException.class, () -> ratesClientService.getRate("GBP", null));
    }    
}
