import apiClient from './apiClient';

export interface ChatResponse {
  response: string;
  intent: string;
  responseType: string;
  data?: any;
  sessionId: string;
  timestamp: string;
}

export interface Suggestion {
  text: string;
  icon: string;
  category: string;
}

export const chatApi = {
  sendMessage: (sessionId: string, message: string) =>
    apiClient.post<ChatResponse>('/api/v1/chat/message', {
      sessionId,
      message,
    }).then((r) => r.data),

  getSuggestions: () =>
    apiClient.get<{ suggestions: Suggestion[] }>('/api/v1/chat/suggestions').then((r) => r.data.suggestions),
};
