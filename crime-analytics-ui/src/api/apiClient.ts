import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use(async (config) => {
  const { keycloak, authenticated } = useAuthStore.getState();
  if (keycloak && authenticated) {
    try {
      await keycloak.updateToken(10);
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    } catch {
      keycloak.logout({ redirectUri: window.location.origin + '/login' });
    }
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().keycloak?.logout({ redirectUri: window.location.origin + '/login' });
    }
    return Promise.reject(error);
  }
);

export default apiClient;
