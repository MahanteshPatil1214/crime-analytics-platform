export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: string;
  evidence?: EvidenceTrail;
}

export interface EvidenceTrail {
  references: EvidenceReference[];
  disclaimer: string;
  overallConfidence: number;
}

export interface EvidenceReference {
  claimText: string;
  sourceType: string;
  sourceId: string;
  sourceQuery: string;
  confidence: number;
}
