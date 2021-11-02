package com.github.vava23.currencyconvertor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.vava23.currencyconvertor.rest.MainController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CurrencyConvertorApplicationTests {
	@Autowired
	MainController mainController;


	/** 
	 * Smoke test
	 */
	@Test
	void contextLoads() {
		assertNotNull(mainController);
	}
}
