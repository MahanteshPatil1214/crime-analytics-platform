import { create } from 'zustand';
import Keycloak from 'keycloak-js';

interface AuthState {
  keycloak: Keycloak | null;
  authenticated: boolean;
  userRoles: string[];
  initialized: boolean;
  setKeycloak: (kc: Keycloak) => void;
  setAuthenticated: (kc: Keycloak) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  keycloak: null,
  authenticated: false,
  userRoles: [],
  initialized: false,
  setKeycloak: (kc) => set({ keycloak: kc }),
  setAuthenticated: (kc) => set({
    authenticated: true,
    userRoles: kc.tokenParsed?.realm_access?.roles || [],
    initialized: true,
  }),
  logout: () => {
    const state = useAuthStore.getState();
    state.keycloak?.logout({ redirectUri: window.location.origin + '/login' });
  },
}));
