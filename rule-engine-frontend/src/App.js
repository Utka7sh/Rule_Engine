import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
    const [ruleName, setRuleName] = useState('');
    const [ruleString, setRuleString] = useState('');
    const [rules, setRules] = useState([]);
    const [ruleId, setRuleId] = useState('');
    const [ruleData, setRuleData] = useState('{}');
    const [evaluationResult, setEvaluationResult] = useState('');
    const [validationResult, setValidationResult] = useState('');

    useEffect(() => {
        fetchRules();
    }, []);

    const fetchRules = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/rules');
            setRules(response.data);
        } catch (error) {
            console.error('Error fetching rules:', error);
        }
    };

    const validateRule = async () => {
        try {
            const response = await axios.post('http://localhost:8080/api/validate-rule', { ruleString });
            setValidationResult(response.data.message);
        } catch (error) {
            setValidationResult('Error: ' + (error.response?.data?.message || 'An error occurred'));
        }
    };

    const createRule = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8080/api/rules', { ruleName, ruleString });
            alert('Rule created successfully!');
            setRuleName('');
            setRuleString('');
            fetchRules();
        } catch (error) {
            console.error('Error creating rule:', error);
            alert('Error creating rule: ' + (error.response?.data?.message || error.message));
        }
    };

    const evaluateRule = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(`http://localhost:8080/api/rules/${ruleId}/evaluate`, JSON.parse(ruleData));
            if (response.data) {
                setEvaluationResult('Rule matches the data!');
            } else {
                setEvaluationResult('Rule does not match the data.');
            }
        } catch (error) {
            console.error('Error evaluating rule:', error);
            setEvaluationResult('Error evaluating rule.');
        }
    };

    return (
        <div className="app-container">
            {/* Rule Validation Section */}
            <div className="validation-area">
                <h1>Rule Engine Validator</h1>
                <div className="search-bar">
                    <input
                        type="text"
                        value={ruleString}
                        onChange={(e) => setRuleString(e.target.value)}
                        placeholder="Enter your rule string"
                    />
                    <button onClick={validateRule}>Validate Rule</button>
                </div>
                <div className="validation-result">
                    <strong>Validation Result:</strong> {validationResult}
                </div>
            </div>

            {/* Create New Rule Section */}
            <div className="create-rule-area">
                <h2>Create New Rule</h2>
                <form onSubmit={createRule}>
                    <div>
                        <label>Rule Name:</label>
                        <input
                            type="text"
                            value={ruleName}
                            onChange={(e) => setRuleName(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label>Rule String:</label>
                        <textarea
                            value={ruleString}
                            onChange={(e) => setRuleString(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit">Create Rule</button>
                </form>
                <div className="existing-rules">
                    <h3>Existing Rules</h3>
                    <ul>
                        {rules.map((rule) => (
                            <li key={rule.id}>
                                ID: {rule.id} - {rule.ruleName}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>

            {/* Evaluate Rule Section */}
            <div className="evaluate-rule-area">
                <h2>Evaluate Rule</h2>
                <form onSubmit={evaluateRule}>
                    <div>
                        <label>Rule ID:</label>
                        <input
                            type="number"
                            value={ruleId}
                            onChange={(e) => setRuleId(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label>Rule Data (JSON format):</label>
                        <textarea
                            value={ruleData}
                            onChange={(e) => setRuleData(e.target.value)}
                            placeholder='{"age": 30, "department": "Sales", "salary": 60000}'
                            required
                        />
                    </div>
                    <button type="submit">Evaluate Rule</button>
                </form>
                <div className="evaluation-result">
                    <br/>
                    <strong>Evaluation Result:</strong> {evaluationResult}
                </div>
            </div>
        </div>
    );
}

export default App;
