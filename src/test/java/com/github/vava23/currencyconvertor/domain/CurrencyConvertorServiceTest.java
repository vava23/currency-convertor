package com.github.vava23.currencyconvertor.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import com.github.vava23.currencyconvertor.client.RatesClientService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

public class CurrencyConvertorServiceTest {
    private static BigDecimal correctAmount;
    private static BigDecimal correctRate;
    private static BigDecimal incorrectAmount;
    private static BigDecimal incorrectRate;
    private static CurrencyConvertorService convertor;

    @BeforeAll
    public static void setup() {
        convertor = new CurrencyConvertorService(mockRatesClientService());
        correctAmount = new BigDecimal("999.99");
        correctRate = new BigDecimal("7.654321");
        incorrectAmount = new BigDecimal("-999.99");
        incorrectRate = new BigDecimal("-7.654321");            
    }

    public static RatesClientService mockRatesClientService() {
        RatesClientService mockRatesService = Mockito.mock(RatesClientService.class);
        Mockito.when(mockRatesService.getAvailableCurrencies()).thenReturn(Set.of("USD", "EUR", "GBP", "JPY"));
        Mockito.when(mockRatesService.getRate("USD", "EUR")).thenReturn(new BigDecimal("0.8"));
        Mockito.when(mockRatesService.getRate("GBP", "USD")).thenReturn(new BigDecimal("1.333333"));
        return mockRatesService;
    }

    @Test
    public void testConvertWithRateSuccess() {
        BigDecimal result = convertor.convert(correctAmount, correctRate);
        assertEquals(new BigDecimal("7654.24445679"), result);
    }

    @Test
    public void testConvertWithCurrencySuccess() {
        // Normal case
        ConversionResult expectedResult = new ConversionResult("success", "EUR", new BigDecimal("799.992"));
        ConversionResult result = convertor.convert(correctAmount, "USD", "EUR");
        assertTrue(new ReflectionEquals(expectedResult).matches(result));
        // Same currency
        expectedResult = new ConversionResult("success", "JPY", correctAmount);
        result = convertor.convert(correctAmount, "JPY", "JPY");
        assertTrue(new ReflectionEquals(expectedResult).matches(result));
        expectedResult = new ConversionResult("success", "GBP", correctAmount);
        result = convertor.convert(correctAmount, "GBP", "gbp");
        assertTrue(new ReflectionEquals(expectedResult).matches(result));        
    }

    @Test
    public void testConvertWithCurrencyFails() {
        assertThrows(RuntimeException.class, () -> convertor.convert(incorrectAmount, "USD", "EUR"));
        assertThrows(RuntimeException.class, () -> convertor.convert(correctAmount, "11111", "EUR"));
        assertThrows(RuntimeException.class, () -> convertor.convert(correctAmount, null, "EUR"));
    }    

    @Test
    public void testConvertFails() {
        // TOTO: a loop could be added to cycle through all incorrect + correct combinations
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(correctAmount, null));
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(null, correctRate));
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(correctAmount, incorrectRate));
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(incorrectAmount, correctRate));
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(correctAmount, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(BigDecimal.ZERO, incorrectRate));
    }    

    @Test
    public void testConvertCornerCases() {
        // 0 is still considered  a correct amount
        assertEquals(BigDecimal.ZERO, convertor.convert(BigDecimal.ZERO, correctRate));
    }

    @Test
    public void testStringValidation() {
        convertor.validateAmount(correctAmount.toString());
        convertor.validateCurrency("USD");
        for (String amount: new String[]{incorrectAmount.toString(), "twenty", "", "  ", null}) {
            assertThrows(IllegalArgumentException.class, () -> convertor.validateAmount(amount));
        }
        for (String currency: new String[]{"XYZ", "987654", "", "  ", null}) {
            assertThrows(IllegalArgumentException.class, () -> convertor.validateCurrency(currency));
        }        
    }
}
