import { create } from 'zustand';

interface AuthState {
  keycloak: { token: string; refreshToken: string; realm: string } | null;
  setAuth: (keycloak: AuthState['keycloak']) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  keycloak: null,
  setAuth: (keycloak) => set({ keycloak }),
  logout: () => {
    localStorage.removeItem('keycloak-token');
    set({ keycloak: null });
  },
}));
