package com.example.data;

import javax.persistence.*;

@Entity
@Table(name = "rules")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "rule_name", nullable = false)
    private String name;

    @Column(name = "rule_string", nullable = false)
    private String ruleString;

    @Transient
    private Node node; // Not stored in the database, calculated during runtime

    // Default constructor for JPA
    public Rule() {}

    // Constructor
    public Rule(String name, String ruleString) {
        this.name = name;
        this.ruleString = ruleString;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRuleString() {
        return ruleString;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRuleString(String ruleString) {
        this.ruleString = ruleString;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
