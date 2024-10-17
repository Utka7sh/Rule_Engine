import React, { useState, useEffect } from 'react';
import axios from 'axios';

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
        <div style={{ padding: '20px' }}>
            <h1>Rule Engine Validator</h1>
            <div style={{ marginBottom: '20px' }}>
                <input
                    type="text"
                    value={ruleString}
                    onChange={(e) => setRuleString(e.target.value)}
                    placeholder="Enter your rule string"
                    style={{ width: '300px', marginRight: '10px' }}
                />
                <button onClick={validateRule}>Validate Rule</button>
                <div style={{ marginTop: '10px' }}>
                    <strong>Validation Result:</strong> {validationResult}
                </div>
            </div>
            <div style={{ marginBottom: '20px' }}>
                <h2>Create New Rule</h2>
                <form onSubmit={createRule}>
                    <div>
                        <label>Rule Name:</label>
                        <input
                            type="text"
                            value={ruleName}
                            onChange={(e) => setRuleName(e.target.value)}
                            style={{ marginLeft: '10px', marginBottom: '10px', width: '300px' }}
                            required
                        />
                    </div>
                    <div>
                        <label>Rule String:</label>
                        <textarea
                            value={ruleString}
                            onChange={(e) => setRuleString(e.target.value)}
                            style={{ marginLeft: '10px', width: '300px', height: '100px' }}
                            required
                        />
                    </div>
                    <button type="submit">Create Rule</button>
                </form>
            </div>
            <div style={{ marginBottom: '20px' }}>
                <h2>Existing Rules</h2>
                <ul>
                    {rules.map((rule) => (
                        <li key={rule.id}>
                            ID: {rule.id} - {rule.ruleName}
                        </li>
                    ))}
                </ul>
            </div>
            <div>
                <h2>Evaluate Rule</h2>
                <form onSubmit={evaluateRule}>
                    <div>
                        <label>Rule ID:</label>
                        <input
                            type="number"
                            value={ruleId}
                            onChange={(e) => setRuleId(e.target.value)}
                            style={{ marginLeft: '10px', marginBottom: '10px', width: '300px' }}
                            required
                        />
                    </div>
                    <div>
                        <label>Rule Data (JSON format):</label>
                        <textarea
                            value={ruleData}
                            onChange={(e) => setRuleData(e.target.value)}
                            style={{ marginLeft: '10px', width: '300px', height: '100px' }}
                            placeholder='{"age": 30, "department": "Sales", "salary": 60000}'
                            required
                        />
                    </div>
                    <button type="submit">Evaluate Rule</button>
                </form>
                <div style={{ marginTop: '10px' }}>
                    <strong>Evaluation Result:</strong> {evaluationResult}
                </div>
            </div>
        </div>
    );
}

export default App;
