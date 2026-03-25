package com.testlang.compiler.model;

public final class Variable {
    public enum Type {
        STRING,
        INTEGER
    }

    private final String name;
    private final Type type;
    private final Object value;

    public Variable(String name, Type type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
