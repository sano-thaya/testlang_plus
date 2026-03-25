package com.testlang.compiler.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TestCase {
    private final String name;
    private final RequestSpec request;
    private final List<AssertionSpec> assertions;

    public TestCase(String name, RequestSpec request, List<AssertionSpec> assertions) {
        this.name = name;
        this.request = request;
        this.assertions = Collections.unmodifiableList(new ArrayList<>(assertions));
    }

    public String getName() {
        return name;
    }

    public RequestSpec getRequest() {
        return request;
    }

    public List<AssertionSpec> getAssertions() {
        return assertions;
    }
}
