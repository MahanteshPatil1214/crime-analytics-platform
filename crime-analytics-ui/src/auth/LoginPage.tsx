import React, { useEffect } from 'react';
import { Button, Card, Typography } from 'antd';
import { SecurityScanOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';

const { Title, Text } = Typography;

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const keycloak = useAuthStore((s) => s.keycloak);
  const authenticated = useAuthStore((s) => s.authenticated);
  const initialized = useAuthStore((s) => s.initialized);

  useEffect(() => {
    if (authenticated) navigate('/dashboard', { replace: true });
  }, [authenticated]);

  const handleLogin = () => {
    keycloak?.login({ redirectUri: window.location.origin + '/dashboard' });
  };

  if (!initialized) return null;

  return (
    <div style={{
      display: 'flex', justifyContent: 'center', alignItems: 'center',
      minHeight: '100vh', background: 'linear-gradient(135deg, #001529 0%, #003366 100%)',
    }}>
      <Card style={{ width: 400, textAlign: 'center', borderRadius: 12, boxShadow: '0 8px 32px rgba(0,0,0,0.2)' }}>
        <SecurityScanOutlined style={{ fontSize: 48, color: '#1890ff', marginBottom: 16 }} />
        <Title level={3} style={{ marginBottom: 4 }}>Crime Analytics</Title>
        <Text type="secondary" style={{ display: 'block', marginBottom: 32 }}>
          Secure platform for law enforcement personnel
        </Text>
        <Button type="primary" size="large" block onClick={handleLogin}>
          Sign in with Keycloak
        </Button>
      </Card>
    </div>
  );
};
