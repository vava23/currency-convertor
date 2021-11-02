package com.github.vava23.currencyconvertor.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.github.vava23.currencyconvertor.client.RatesClientService;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CurrencyReferenceTest {

    private static RatesClientService mockRatesClientService() {
        RatesClientService mockRatesService = Mockito.mock(RatesClientService.class);
        Mockito.when(mockRatesService.getAvailableCurrencies()).thenReturn(Set.of("USD", "EUR", "GBP", "JPY"));
        Mockito.when(mockRatesService.getRate("USD", "EUR")).thenReturn(new BigDecimal("0.8"));
        Mockito.when(mockRatesService.getRate("GBP", "USD")).thenReturn(new BigDecimal("1.333333"));
        return mockRatesService;
    }

    @Test
    void testCurrenciesUpdate() throws IllegalAccessException {
        // Currency is not supported
        RatesClientService ratesService = mockRatesClientService();
        CurrencyReference currencies = new CurrencyReference(ratesService);
        assertFalse(currencies.containsCurrency("XYZ"));
        
        // Date changed, same currency became available
        Mockito.when(ratesService.getAvailableCurrencies()).thenReturn(Set.of("USD", "EUR", "GBP", "JPY", "XYZ"));
        LocalDate yesterday = LocalDate.now().minusDays(1);
        FieldUtils.writeField(currencies, "lastCurrenciesUpdate", yesterday, true);
        assertTrue(currencies.containsCurrency("XYZ"));
    }
}
