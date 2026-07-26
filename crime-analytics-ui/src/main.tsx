import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { KeycloakProvider } from './auth/KeycloakProvider';
import App from './App';
import './i18n';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30000,
      retry: 2,
    },
  },
});

ReactDOM.createRoot(document.getElementById('root')!).render(
  <QueryClientProvider client={queryClient}>
    <BrowserRouter>
      <KeycloakProvider>
        <App />
      </KeycloakProvider>
    </BrowserRouter>
  </QueryClientProvider>
);
