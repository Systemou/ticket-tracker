import React, { useEffect, useState } from 'react';
import { Card, Row, Col, Button, Alert } from 'reactstrap';

export const Debug = () => {
  const [categories, setCategories] = useState([]);
  const [priorities, setPriorities] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      // Fetch categories
      const categoriesResponse = await fetch('/api/ticket-categories', {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      });

      if (categoriesResponse.ok) {
        const categoriesData = await categoriesResponse.json();
        setCategories(categoriesData);
      } else {
        setError(`Categories API error: ${categoriesResponse.status} ${categoriesResponse.statusText}`);
      }

      // Fetch priorities
      const prioritiesResponse = await fetch('/api/ticket-priorities', {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      });

      if (prioritiesResponse.ok) {
        const prioritiesData = await prioritiesResponse.json();
        setPriorities(prioritiesData);
      } else {
        setError(prev => prev + ` | Priorities API error: ${prioritiesResponse.status} ${prioritiesResponse.statusText}`);
      }

      // Fetch users
      const usersResponse = await fetch('/api/users', {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
      });

      if (usersResponse.ok) {
        const usersData = await usersResponse.json();
        setUsers(usersData);
      } else {
        setError(prev => prev + ` | Users API error: ${usersResponse.status} ${usersResponse.statusText}`);
      }
    } catch (networkError) {
      setError(`Network error: ${networkError.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div className="debug-container p-4">
      <Card className="border-0 shadow-sm">
        <div className="p-4">
          <h3 className="mb-4">Debug Information</h3>

          {error && (
            <Alert color="warning" className="mb-4">
              <strong>API Errors:</strong> {error}
              <br />
              <small>This might be due to authentication requirements. Try logging in first.</small>
            </Alert>
          )}

          <Row>
            <Col md={4}>
              <h5>Categories ({categories.length})</h5>
              {loading ? (
                <p>Loading...</p>
              ) : categories.length > 0 ? (
                <ul>
                  {categories.map(cat => (
                    <li key={cat.id}>
                      {cat.id}: {cat.name}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-muted">No categories found</p>
              )}
            </Col>

            <Col md={4}>
              <h5>Priorities ({priorities.length})</h5>
              {loading ? (
                <p>Loading...</p>
              ) : priorities.length > 0 ? (
                <ul>
                  {priorities.map(pri => (
                    <li key={pri.id}>
                      {pri.id}: {pri.name}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-muted">No priorities found</p>
              )}
            </Col>

            <Col md={4}>
              <h5>Users ({users.length})</h5>
              {loading ? (
                <p>Loading...</p>
              ) : users.length > 0 ? (
                <ul>
                  {users.map(user => (
                    <li key={user.id}>
                      {user.id}: {user.login}
                    </li>
                  ))}
                </ul>
              ) : (
                <p className="text-muted">No users found</p>
              )}
            </Col>
          </Row>

          <div className="mt-4">
            <Button color="primary" onClick={fetchData} className="me-2">
              Refresh Data
            </Button>
            <Button color="secondary" onClick={() => window.open('/ticket/new', '_blank')}>
              Test Ticket Creation
            </Button>
          </div>

          <div className="mt-4">
            <h6>Quick Fix Instructions:</h6>
            <ol>
              <li>Make sure you&apos;re logged in to the application</li>
              <li>Try accessing the ticket creation form directly</li>
              <li>If dropdowns are still empty, check the browser console for errors</li>
              <li>The data should be automatically loaded when you&apos;re authenticated</li>
            </ol>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default Debug;
