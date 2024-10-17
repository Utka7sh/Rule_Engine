package com.example.backend;

import com.example.data.Node;
import java.util.Stack;
import java.util.Map;

public class RuleEngine {

    public Node createRule(String ruleString) {
        ruleString = ruleString.trim();
        if (ruleString.startsWith("(") && ruleString.endsWith(")")) {
            ruleString = ruleString.substring(1, ruleString.length() - 1);
        }

        return parseExpression(ruleString);
    }

    private Node parseExpression(String expression) {
        Stack<Node> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        int index = 0;
        while (index < expression.length()) {
            char currentChar = expression.charAt(index);

            if (currentChar == '(') {
                int closingIndex = findClosingParenthesis(expression, index);
                String subExpression = expression.substring(index + 1, closingIndex);
                operandStack.push(parseExpression(subExpression));
                index = closingIndex + 1;
            } else if (expression.startsWith("AND", index)) {
                operatorStack.push("AND");
                index += 3;
            } else if (expression.startsWith("OR", index)) {
                operatorStack.push("OR");
                index += 2;
            } else {
                int nextIndex = findNextOperatorIndex(expression, index);
                String condition = expression.substring(index, nextIndex).trim();

                if (isValidCondition(condition)) {
                    operandStack.push(new Node("operand", condition));
                } else {
                    throw new IllegalArgumentException("Unsupported rule format: " + condition);
                }
                index = nextIndex;
            }
        }

        // Build the expression tree based on operators
        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            if (operandStack.size() < 2) {
                throw new IllegalArgumentException("Invalid rule format. Missing operands.");
            }
            Node right = operandStack.pop();
            Node left = operandStack.pop();
            Node combined = new Node("operator", left, right, operator);
            operandStack.push(combined);
        }

        return operandStack.isEmpty() ? null : operandStack.pop();
    }

    private int findClosingParenthesis(String expression, int openIndex) {
        int balance = 1;
        for (int i = openIndex + 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') balance++;
            if (expression.charAt(i) == ')') balance--;
            if (balance == 0) return i;
        }
        throw new IllegalArgumentException("Mismatched parentheses in rule: " + expression);
    }

    private int findNextOperatorIndex(String expression, int startIndex) {
        int andIndex = expression.indexOf("AND", startIndex);
        int orIndex = expression.indexOf("OR", startIndex);

        if (andIndex == -1 && orIndex == -1) {
            return expression.length();
        } else if (andIndex == -1) {
            return orIndex;
        } else if (orIndex == -1) {
            return andIndex;
        } else {
            return Math.min(andIndex, orIndex);
        }
    }

    private boolean isValidCondition(String condition) {
        return condition.matches("age (>|>=|<|<=|=|!=) \\d+") ||
                condition.matches("department (>|>=|<|<=|=|!=) '.+'") ||
                condition.matches("salary (>|>=|<|<=|=|!=) \\d+") ||
                condition.matches("experience (>|>=|<|<=|=|!=) \\d+");
    }

    public boolean evaluateRule(Node node, Map<String, Object> data) {
        if (node.getType().equals("operand")) {
            String[] parts = node.getValue().split(" ");
            String attribute = parts[0];
            String operator = parts[1];
            String value = parts[2].replace("'", "");

            if (data.containsKey(attribute)) {
                Object attributeValue = data.get(attribute);

                if (attributeValue instanceof Integer) {
                    int numericValue = Integer.parseInt(value);
                    int dataValue = (int) attributeValue;

                    switch (operator) {
                        case ">": return dataValue > numericValue;
                        case "<": return dataValue < numericValue;
                        case ">=": return dataValue >= numericValue;
                        case "<=": return dataValue <= numericValue;
                        case "=": return dataValue == numericValue;
                        case "!=": return dataValue != numericValue;
                        default: throw new IllegalArgumentException("Unsupported operator: " + operator);
                    }
                } else if (attributeValue instanceof String) {
                    String stringValue = (String) attributeValue;
                    switch (operator) {
                        case "=": return stringValue.equals(value);
                        case "!=": return !stringValue.equals(value);
                        default: throw new IllegalArgumentException("Unsupported operator for strings: " + operator);
                    }
                } else {
                    System.out.println("Attribute type not supported: " + attribute);
                    return false;
                }
            } else {
                System.out.println("Attribute " + attribute + " not found in data.");
                return false;
            }
        } else if (node.getType().equals("operator")) {
            boolean leftResult = evaluateRule(node.getLeft(), data);
            boolean rightResult = evaluateRule(node.getRight(), data);
            return node.getOperator().equals("AND") ? leftResult && rightResult : leftResult || rightResult;
        }

        return false;
    }

    public Node combineRules(Node rule1, Node rule2, String operator) {
        if (rule1 == null || rule2 == null || operator == null) {
            throw new IllegalArgumentException("Rules and operator cannot be null");
        }
        return new Node("operator", rule1, rule2, operator);
    }
}
