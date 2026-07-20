import { create } from 'zustand';
import { ChatMessage } from '../types/chat';

interface ChatState {
  messages: ChatMessage[];
  isOpen: boolean;
  sessionId: string;
  isTyping: boolean;
  addMessage: (msg: ChatMessage) => void;
  toggleChat: () => void;
  clearMessages: () => void;
  setTyping: (typing: boolean) => void;
}

export const useChatStore = create<ChatState>((set) => ({
  messages: [],
  isOpen: false,
  sessionId: crypto.randomUUID(),
  isTyping: false,
  addMessage: (msg) => set((state) => ({ messages: [...state.messages, msg] })),
  toggleChat: () => set((state) => ({ isOpen: !state.isOpen })),
  clearMessages: () => set({ messages: [] }),
  setTyping: (typing) => set({ isTyping: typing }),
}));
