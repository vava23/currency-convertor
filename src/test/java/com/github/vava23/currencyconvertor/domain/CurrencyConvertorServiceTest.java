package com.github.vava23.currencyconvertor.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CurrencyConvertorServiceTest {
    private static BigDecimal correctAmount;
    private static BigDecimal correctRate;
    private static CurrencyConvertorService convertor;

    @BeforeAll
    public static void setup() {
        convertor = new CurrencyConvertorService();
        correctAmount = new BigDecimal("999.99");
        correctRate = new BigDecimal("7.654321");        
    }

    @Test
    public void testConvertSuccess() {
        BigDecimal result = convertor.convert(correctAmount, correctRate);
        assertEquals(new BigDecimal("7654.24445679"), result);
    }

    @Test
    public void testConvertFails() {
        BigDecimal incorrectAmount = new BigDecimal("-999.99");
        BigDecimal incorrectRate = new BigDecimal("-7.654321");
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
}
