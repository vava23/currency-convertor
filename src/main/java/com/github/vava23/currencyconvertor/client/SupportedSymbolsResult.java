package com.github.vava23.currencyconvertor.client;

import java.util.Map;
import java.io.Serializable;

/**
 * Supported Symbols response data
 */
public class SupportedSymbolsResult implements Serializable {
    private boolean success;
    private Map<String, String> symbols;

    public boolean isSuccess() {
        return success;
    }
    
    public Map<String, String> getSymbols() {
        return symbols;
    }
}
