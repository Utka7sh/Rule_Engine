package com.example.data;

// Import the Condition class
import com.example.data.Condition;

public class Node {
    private String type;    // "operator" or "operand"
    private Node left;
    private Node right;
    private Object value;   // For operands (Condition object) or operators (String)

    // Constructor for operator nodes
    public Node(String type, Node left, Node right, String operator) {
        if (type == null || left == null || right == null || operator == null) {
            throw new IllegalArgumentException("Type, left, right, and operator cannot be null");
        }
        this.type = type;
        this.left = left;
        this.right = right;
        this.value = operator; // For operator nodes, value stores the operator
    }

    // Constructor for operand nodes
    public Node(String type, Condition condition) {
        if (type == null || condition == null) {
            throw new IllegalArgumentException("Type and condition cannot be null");
        }
        this.type = type;
        this.value = condition; // For operand nodes, value stores the Condition object
        this.left = null;  // No children for operand nodes
        this.right = null;
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

    public Object getValue() {
        return value;
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
