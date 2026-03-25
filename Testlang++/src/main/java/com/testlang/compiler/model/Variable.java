package com.testlang.compiler.model;

public final class Variable {
    public enum Type {
        STRING,
        INTEGER
    }

    private final String name;
    private final Type type;
    private final Object value;
    private final int line;

    public Variable(String name, Type type, Object value) {
        this(name, type, value, -1);
    }

    public Variable(String name, Type type, Object value, int line) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.line = line;
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

    public int getLine() {
        return line;
    }
}
