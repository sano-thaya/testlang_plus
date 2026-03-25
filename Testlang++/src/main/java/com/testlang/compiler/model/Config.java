package com.testlang.compiler.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Config {
    private final String baseUrl;
    private final Map<String, String> defaultHeaders;

    public Config(String baseUrl, Map<String, String> defaultHeaders) {
        this.baseUrl = baseUrl;
        this.defaultHeaders = Collections.unmodifiableMap(new LinkedHashMap<>(defaultHeaders));
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }
}
