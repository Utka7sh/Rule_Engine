package com.example.data;

public class Node {
    private String type;    // "operator" or "operand"
    private Node left;
    private Node right;
    private String value;   // For operands (e.g., "age > 30")
    private String operator; // For operator nodes (e.g., "AND" or "OR")

    // Constructor for operator nodes
    public Node(String type, Node left, Node right, String operator) {
        if (type == null || left == null || right == null || operator == null) {
            throw new IllegalArgumentException("Type, left, right, and operator cannot be null");
        }
        this.type = type;
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.value = null; // For operator nodes, value is not used
    }

    // Constructor for operand nodes
    public Node(String type, String value) {
        if (type == null || value == null) {
            throw new IllegalArgumentException("Type and value cannot be null");
        }
        this.type = type;
        this.value = value;
        this.left = null;  // No children for operand nodes
        this.right = null;
        this.operator = null; // No operator for operand nodes
    }

    // Getters
    public String getType() {
        return type;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public String getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    // Setters
    public void setLeft(Node left) {
        if (left == null) {
            throw new IllegalArgumentException("Left node cannot be null");
        }
        this.left = left;
    }

    public void setRight(Node right) {
        if (right == null) {
            throw new IllegalArgumentException("Right node cannot be null");
        }
        this.right = right;
    }
}
