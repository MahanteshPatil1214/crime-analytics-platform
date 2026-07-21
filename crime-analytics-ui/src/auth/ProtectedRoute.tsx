import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';

interface Props {
  children: React.ReactNode;
  roles?: string[];
}

export const ProtectedRoute: React.FC<Props> = ({ children, roles }) => {
  const authenticated = useAuthStore((s) => s.authenticated);
  const userRoles = useAuthStore((s) => s.userRoles);
  const initialized = useAuthStore((s) => s.initialized);

  if (!initialized) return null;

  if (!authenticated) {
    return <Navigate to="/login" replace />;
  }

  if (roles && !roles.some((r) => userRoles.includes(r))) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};
