package com.testlang.compiler.model;

public final class AssertionSpec {
    public enum Type {
        STATUS_EQUALS,
        HEADER_EQUALS,
        HEADER_CONTAINS,
        BODY_CONTAINS
    }

    private final Type type;
    private final String key;
    private final String textValue;
    private final Integer numberValue;

    private AssertionSpec(Type type, String key, String textValue, Integer numberValue) {
        this.type = type;
        this.key = key;
        this.textValue = textValue;
        this.numberValue = numberValue;
    }

    public static AssertionSpec statusEquals(int status) {
        return new AssertionSpec(Type.STATUS_EQUALS, null, null, status);
    }

    public static AssertionSpec headerEquals(String key, String value) {
        return new AssertionSpec(Type.HEADER_EQUALS, key, value, null);
    }

    public static AssertionSpec headerContains(String key, String value) {
        return new AssertionSpec(Type.HEADER_CONTAINS, key, value, null);
    }

    public static AssertionSpec bodyContains(String value) {
        return new AssertionSpec(Type.BODY_CONTAINS, null, value, null);
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getTextValue() {
        return textValue;
    }

    public Integer getNumberValue() {
        return numberValue;
    }
}
