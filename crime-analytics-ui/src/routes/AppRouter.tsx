import { Routes, Route } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { AnalyticsDashboard } from '../components/dashboard/AnalyticsDashboard';
import { CaseListPage } from '../components/incidents/CaseListPage';
import { CaseDetailPage } from '../components/incidents/CaseDetailPage';
import { FinancialPage } from '../components/financial/FinancialPage';
import { ReportPage } from '../components/reports/ReportPage';
import { ChatPage } from '../components/chat/ChatPage';
import { HelpPage } from '../components/help/HelpPage';
import { GraphPage } from '../components/graph/GraphPage';
import { SearchPage } from '../components/search/SearchPage';

export const AppRouter = () => {
  return (
    <AppLayout>
      <Routes>
        <Route path="/" element={<AnalyticsDashboard />} />
        <Route path="/dashboard" element={<AnalyticsDashboard />} />
        <Route path="/incidents" element={<CaseListPage />} />
        <Route path="/incidents/:id" element={<CaseDetailPage />} />
        <Route path="/financial" element={<FinancialPage />} />
        <Route path="/reports" element={<ReportPage />} />
        <Route path="/graph" element={<GraphPage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/help" element={<HelpPage />} />
      </Routes>
    </AppLayout>
  );
};
