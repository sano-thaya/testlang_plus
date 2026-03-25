package com.testlang.compiler.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestOptions {
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;
    private int bodyLine = -1;

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body, int line) {
        if (this.body != null) {
            throw new RuntimeException("Line " + line + ": Duplicate body declaration in request block");
        }
        this.body = body;
        this.bodyLine = line;
    }

    public int getBodyLine() {
        return bodyLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
