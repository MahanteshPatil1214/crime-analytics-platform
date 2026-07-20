export interface FinancialTransaction {
  id: string;
  transactionRef: string;
  senderAccountId: string;
  recipientAccountId: string;
  amount: number;
  currency: string;
  transactionDate: string;
  transactionType: 'WIRE' | 'CASH_DEPOSIT' | 'CASH_WITHDRAWAL' | 'CHECK' | 'CRYPTO' | 'TRANSFER';
  isFlagged: boolean;
  flagReason: string | null;
  riskScore: number | null;
  relatedCaseId: number | null;
}

export interface FinancialSearchParams {
  accountId?: string;
  type?: string;
  flagged?: boolean;
  minRisk?: number;
  maxRisk?: number;
}

export interface FinancialStats {
  totalTransactions: number;
  totalAmount: number;
  flaggedCount: number;
}
