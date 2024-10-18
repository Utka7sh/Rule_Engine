package com.example.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.api.RuleAPI;
import com.example.data.Rule;
import com.example.data.Node;
import com.example.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    private final RuleAPI ruleAPI = new RuleAPI();

    // Endpoint to validate a rule string
    @PostMapping("/validate-rule")
    public ResponseEntity<?> validateRule(@RequestBody Map<String, String> request) {
        String ruleString = request.get("ruleString");
        try {
            Node ruleNode = ruleAPI.createRule(ruleString);
            if (ruleNode != null) {
                return ResponseEntity.ok().body(Map.of("message", "Rule is valid"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Rule is invalid"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // Endpoint to create a new rule
    @PostMapping("/rules")
    public ResponseEntity<?> createRule(@RequestBody Map<String, String> request) {
        String ruleName = request.get("ruleName");
        String ruleString = request.get("ruleString");

        try {
            Node ruleNode = ruleAPI.createRule(ruleString);
            if (ruleNode != null) {
                Rule newRule = new Rule(ruleName, ruleString);
                ruleService.saveRule(newRule);
                return ResponseEntity.ok().body(Map.of("message", "Rule created successfully", "id", newRule.getId()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid rule format"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/rules/{id}")
    public ResponseEntity<?> deleteRule(@PathVariable int id) {
        Optional<Rule> rule = ruleService.getRuleById(id);
        if (rule.isPresent()) {
            ruleService.deleteRule(id);
            return ResponseEntity.ok().body(Map.of("message", "Rule deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Rule not found"));
        }
    }

    // Endpoint to get all rules
    @GetMapping("/rules")
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    // Endpoint to evaluate a rule
    @PostMapping("/rules/{id}/evaluate")
    public ResponseEntity<?> evaluateRule(@PathVariable int id, @RequestBody Map<String, Object> data) {
        Optional<Rule> optionalRule = ruleService.getRuleById(id);

        if (optionalRule.isPresent()) {
            Rule rule = optionalRule.get();
            Node ruleNode = ruleAPI.createRule(rule.getRuleString());
            boolean result = ruleAPI.evaluateRule(ruleNode, data);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(404).body(Map.of("message", "Rule not found"));
        }
    }
}