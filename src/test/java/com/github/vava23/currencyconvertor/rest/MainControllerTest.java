package com.github.vava23.currencyconvertor.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;

import com.github.vava23.currencyconvertor.domain.ConversionResult;
import com.github.vava23.currencyconvertor.domain.CurrencyConvertorService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private static CurrencyConvertorService mockCurrencyConvertor;

    @PostConstruct
    public void setup() {
        // Correct conversion
        Mockito.when(mockCurrencyConvertor.convert(new BigDecimal("100"), "EUR", "USD"))
                .thenReturn(new ConversionResult("success", "USD", new BigDecimal("120")));
        // Mock unexpected error
        Mockito.when(mockCurrencyConvertor.convert(new BigDecimal("999999"), "EUR", "USD"))
                .thenThrow(IllegalArgumentException.class);
        // Mock validation fail
        Mockito.doThrow(IllegalArgumentException.class).when(mockCurrencyConvertor).validateAmount("-1");
    }

    @Test
    public void testConvertSuccess() throws Exception {
        String expectedResult = Files.readString(Path.of(getClass().getClassLoader().getResource("json/ConversionResponse.json").toURI()));
        mvc.perform(MockMvcRequestBuilders.get("/?source_currency=EUR&target_currency=USD&amount=100"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResult));
    }

    @Test
    public void testConvertValidationError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/?source_currency=EUR&target_currency=USD&amount=-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testConvertUnexpectedError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/?source_currency=EUR&target_currency=USD&amount=999999"))
                .andExpect(status().is5xxServerError());
    }    
}
