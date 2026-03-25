package com.testlang.compiler.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Program {
    private final Config config;
    private final List<Variable> variables;
    private final List<TestCase> tests;

    public Program(Config config, List<Variable> variables, List<TestCase> tests) {
        this.config = config;
        this.variables = Collections.unmodifiableList(new ArrayList<>(variables));
        this.tests = Collections.unmodifiableList(new ArrayList<>(tests));
    }

    public Config getConfig() {
        return config;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<TestCase> getTests() {
        return tests;
    }
}
