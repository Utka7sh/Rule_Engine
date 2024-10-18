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
    const [visibleRules, setVisibleRules] = useState({});

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

    const toggleRuleVisibility = (id) => {
        setVisibleRules((prevVisibleRules) => ({
            ...prevVisibleRules,
            [id]: !prevVisibleRules[id], // Toggle visibility
        }));
    };

    const deleteRule = async (id) => {
        console.log(`Attempting to delete rule with ID: ${id}`); // Add this line for debugging
        try {
            await axios.delete(`http://localhost:8080/api/rules/${id}`);
            alert('Rule deleted successfully!');
            fetchRules(); // Fetch rules again after deletion
        } catch (error) {
            console.error('Error deleting rule:', error); // Log error details for further debugging
            alert('Error deleting rule: ' + (error.response?.data?.message || error.message));
        }
    };

    return (
        <div className="app-container">
            {/* Main Title */}
            <h1 className="main-title">Rule Engine Validator</h1>

            {/* Validation Area */}
            <div className="validation-area">
                <h2>Validate Rule</h2>
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

            {/* Create New Rule Area */}
            <div className="create-rule-area">
                <h2>Create New Rule</h2>
                <form onSubmit={createRule}>
                    <label>Rule Name:</label>
                    <input
                        type="text"
                        value={ruleName}
                        onChange={(e) => setRuleName(e.target.value)}
                        required
                    />
                    <label>Rule String:</label>
                    <textarea
                        value={ruleString}
                        onChange={(e) => setRuleString(e.target.value)}
                        required
                    />
                    <button type="submit">Create Rule</button>
                </form>
                <h2>Existing Rules</h2>
                <ul>
                    {rules.map((rule) => (
                        <li key={rule.id}>
                            ID: {rule.id} - {rule.name} {/* Update to rule.name instead of rule.ruleName */}
                            <button onClick={() => toggleRuleVisibility(rule.id)} style={{marginLeft: '10px'}}>
                                {visibleRules[rule.id] ? 'Hide Rule' : 'Show Rule'}
                            </button>
                            <button
                                onClick={() => deleteRule(rule.id)}
                                style={{marginLeft: '10px', backgroundColor: 'red', color: 'white'}}
                            >
                                Delete Rule
                            </button>
                            {visibleRules[rule.id] && (
                                <div style={{marginTop: '10px', paddingLeft: '20px'}}>
                                    <strong>Rule String:</strong> {rule.ruleString}
                                </div>
                            )}
                        </li>
                    ))}
                </ul>
            </div>

            {/* Evaluate Rule Area */}
            <div className="evaluate-rule-area">
                <h2>Evaluate Rule</h2>
                <form onSubmit={evaluateRule}>
                    <label>Rule ID:</label>
                    <input
                        type="number"
                        value={ruleId}
                        onChange={(e) => setRuleId(e.target.value)}
                        required
                    />
                    <label>Rule Data (JSON format):</label>
                    <textarea
                        value={ruleData}
                        onChange={(e) => setRuleData(e.target.value)}
                        placeholder='{"age": 30, "department": "Sales", "salary": 60000}'
                        required
                    />
                    <button type="submit">Evaluate Rule</button>
                </form>
                <div className="validation-result">
                    <strong>Evaluation Result:</strong> {evaluationResult}
                </div>
            </div>
        </div>
    );
}

export default App;
