package com.example.api;

import com.example.backend.RuleEngine;
import com.example.data.Node;
import java.util.Map;

public class RuleAPI {
    private final RuleEngine engine = new RuleEngine();

    // Method to create a rule from a rule string
    public Node createRule(String ruleString) {
        return engine.createRule(ruleString);
    }

    // Method to combine two rules with an operator (AND or OR)
    public Node combineRules(Node rule1, Node rule2, String operator) {
        if (operator == null || (!operator.equals("AND") && !operator.equals("OR"))) {
            throw new IllegalArgumentException("Invalid operator: " + operator);
        }
        return engine.combineRules(rule1, rule2, operator);
    }

    // Method to evaluate a rule against provided data
    public boolean evaluateRule(Node node, Map<String, Object> data) {
        return engine.evaluateRule(node, data);
    }
}