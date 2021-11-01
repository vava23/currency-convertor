package com.github.vava23.currencyconvertor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyConvertorApplication {
	private static final Logger log = LoggerFactory.getLogger(CurrencyConvertorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConvertorApplication.class, args);
		String host = "local host";
		try {
			host = InetAddress.getLocalHost().getHostAddress();			
		} catch (UnknownHostException e) {
		}
		log.info("Currency Convertor started on {}", host);
	}
}
