import api.RuleAPI;
import data.Node;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineTest {
    private final RuleAPI api = new RuleAPI();

    @Test
    public void testCreateAndEvaluateRule() {
        Node rule = api.createRule("age > 30");
        Map<String, Object> data = new HashMap<>();
        data.put("age", 35);
        assertTrue(api.evaluateRule(rule, data));
    }

    @Test
    public void testCombinedRuleEvaluation() {
        Node rule1 = api.createRule("age > 30");
        Node rule2 = api.createRule("department = 'Sales'");
        Node combinedRule = api.combineRules(rule1, rule2);

        Map<String, Object> data = new HashMap<>();
        data.put("age", 35);
        data.put("department", "Sales");

        assertTrue(api.evaluateRule(combinedRule, data));
    }
}
