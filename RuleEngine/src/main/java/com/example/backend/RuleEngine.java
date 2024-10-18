package com.example.backend;

import com.example.data.Node;
import com.example.data.Condition; // Import the Condition class

import java.util.Stack;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // Tokenize the expression (handles operators, parentheses, etc.)
        String[] tokens = expression.trim().split("\\s*(AND|OR|\\(|\\))\\s*");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            System.out.println("Processing token: " + token);

            if (token.equals("AND") || token.equals("OR")) {
                operatorStack.push(token);  // Logical operators
            } else if (token.equals("(") || token.equals(")")) {
                // Parentheses handling logic
                continue;  // Skipping for now, adjust if needed
            } else {
                // Process the condition
                Node operandNode = createOperandNode(token.trim());
                operandStack.push(operandNode);
            }
        }

        // Process remaining operators
        while (!operatorStack.isEmpty()) {
            String operator = operatorStack.pop();
            Node right = operandStack.pop();
            Node left = operandStack.pop();
            Node operatorNode = new Node("operator", left, right, operator);
            operandStack.push(operatorNode);
        }

        return operandStack.isEmpty() ? null : operandStack.pop();
    }

    private Node createOperandNode(String expression) {
        // Updated regex to handle numeric and string comparisons
        Pattern pattern = Pattern.compile("(\\w+)\\s*(>|<|>=|<=|=)\\s*([0-9]+(\\.[0-9]+)?|'[^']*'|\\w+)");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.matches()) {
            String field = matcher.group(1);  // Attribute (e.g., "age")
            String operator = matcher.group(2);  // Operator (e.g., ">")
            String value = matcher.group(3);  // Value (e.g., "30")
            return new Node("operand", new Condition(field, operator, value));
        }
        throw new IllegalArgumentException("Invalid expression: " + expression);
    }

    public boolean evaluateRule(Node node, Map<String, Object> data) {
        if (node.getType().equals("operand")) {
            // Extract the Condition object from the operand node using getters
            Condition condition = (Condition) node.getValue();
            String attribute = condition.getField();
            String operator = condition.getOperator();
            String value = condition.getValue().replace("'", "");  // Handle string values like 'Sales'

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
            return node.getValue().equals("AND") ? leftResult && rightResult : leftResult || rightResult;
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
