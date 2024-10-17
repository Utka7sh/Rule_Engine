package com.example.service;

import com.example.data.Rule;
import com.example.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public Optional<Rule> getRuleById(int id) {
        return ruleRepository.findById(id);
    }

    public Rule saveRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public void deleteRule(int id) {
        ruleRepository.deleteById(id);
    }
}
