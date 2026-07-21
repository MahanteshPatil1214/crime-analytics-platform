import React, { useEffect, useState } from 'react';
import Keycloak from 'keycloak-js';
import { Spin } from 'antd';
import { useAuthStore } from '../stores/authStore';

const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8081',
  realm: 'crime-platform',
  clientId: 'crime-web',
};

export const KeycloakProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [loading, setLoading] = useState(true);
  const setKeycloak = useAuthStore((s) => s.setKeycloak);
  const setAuthenticated = useAuthStore((s) => s.setAuthenticated);
  const authenticated = useAuthStore((s) => s.authenticated);

  useEffect(() => {
    const kc = new Keycloak(keycloakConfig);

    kc.init({
      onLoad: 'check-sso',
      silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
      pkceMethod: 'S256',
    }).then((auth) => {
      setKeycloak(kc);
      if (auth) {
        setAuthenticated(kc);
        kc.onTokenExpired = () => kc.updateToken(30).catch(() => kc.logout());
      } else {
        useAuthStore.setState({ initialized: true });
      }
    }).catch(() => {
      useAuthStore.setState({ initialized: true });
    }).finally(() => setLoading(false));

    return () => { kc?.clearToken(); };
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" tip="Initializing..." fullscreen />
      </div>
    );
  }

  return <>{children}</>;
};
