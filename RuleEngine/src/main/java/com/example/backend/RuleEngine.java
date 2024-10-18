package com.example.backend;

import com.example.data.Node;

import java.util.Stack;
import java.util.Map;

public class RuleEngine {

    public Node createRule(String ruleString) {
        ruleString = ruleString.trim();
        System.out.println("Parsing Rule: " + ruleString); // Debugging

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
                // Find the matching closing parenthesis for this subexpression
                int closingIndex = findClosingParenthesis(expression, index);
                String subExpression = expression.substring(index + 1, closingIndex);

                // Recursively parse the sub-expression inside parentheses
                operandStack.push(parseExpression(subExpression.trim()));
                index = closingIndex + 1;  // Move index past the closing parenthesis
            } else if (expression.startsWith("AND", index)) {
                // Handle AND operator
                while (!operatorStack.isEmpty() && operatorStack.peek().equals("OR")) {
                    // Process OR operators before AND to handle precedence
                    String operator = operatorStack.pop();
                    Node right = operandStack.pop();
                    Node left = operandStack.pop();
                    operandStack.push(new Node("operator", left, right, operator));
                }
                operatorStack.push("AND");
                index += 3;  // Move index past "AND"
            } else if (expression.startsWith("OR", index)) {
                // Handle OR operator
                operatorStack.push("OR");
                index += 2;  // Move index past "OR"
            } else {
                // Extract the condition by finding the next operator or end of the expression
                int nextIndex = findNextOperatorIndex(expression, index);
                String condition = expression.substring(index, nextIndex).trim();

                // Remove any trailing parentheses from conditions if necessary
                while (condition.endsWith(")")) {
                    condition = condition.substring(0, condition.length() - 1).trim();
                }

                System.out.println("Processing Condition: " + condition);  // Debugging

                if (isValidCondition(condition)) {
                    operandStack.push(new Node("operand", condition));
                } else {
                    throw new IllegalArgumentException("Unsupported rule format: " + condition);
                }
                index = nextIndex;  // Move to next part of the expression
            }
        }

        // Process remaining operators in the stack
        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            if (operandStack.size() < 2) {
                throw new IllegalArgumentException("Invalid rule format. Missing operands.");
            }
            Node right = operandStack.pop();
            Node left = operandStack.pop();
            operandStack.push(new Node("operator", left, right, operator));
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

    // Enhanced regex to properly handle string conditions and numeric conditions
    // Enhanced regex to properly handle string conditions and numeric conditions
    private boolean isValidCondition(String condition) {
        // Check if parentheses are balanced within a condition
        if (condition.contains("(") || condition.contains(")")) {
            int openParenCount = condition.length() - condition.replace("(", "").length();
            int closeParenCount = condition.length() - condition.replace(")", "").length();
            if (openParenCount != closeParenCount) {
                System.out.println("Condition has unbalanced parentheses: " + condition); // Debugging
                return false;
            }
        }

        boolean matches = condition.matches("age (>|>=|<|<=|=|!=) \\d+") ||
                condition.matches("department (=|!=) '.*'") ||  // Matches only supported string operators (e.g., department = 'Sales')
                condition.matches("salary (>|>=|<|<=|=|!=) \\d+") ||
                condition.matches("experience (>|>=|<|<=|=|!=) \\d+");

        System.out.println("Condition Validity: " + matches); // Debugging
        return matches;
    }


    public boolean evaluateRule(Node node, Map<String, Object> data) {
        if (node.getType().equals("operand")) {
            String[] parts = node.getValue().split(" ");
            String attribute = parts[0];
            String operator = parts[1];
            String value = parts[2].replace("'", "");  // Handle string values like 'Sales'

            System.out.println("Evaluating: " + attribute + " " + operator + " " + value);  // Debugging

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