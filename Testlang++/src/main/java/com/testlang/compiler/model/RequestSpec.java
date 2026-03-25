package com.testlang.compiler.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RequestSpec {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public RequestSpec(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
