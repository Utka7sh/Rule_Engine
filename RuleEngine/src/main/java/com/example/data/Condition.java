package com.example.data;

public class Condition {
    private String field;
    private String operator;
    private String value;

    public Condition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    // Getter methods
    public String getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    // Optionally, you can add setter methods if you need to modify the values in the future.
}
