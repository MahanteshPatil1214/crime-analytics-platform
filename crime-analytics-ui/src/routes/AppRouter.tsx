import { Routes, Route } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { LoginPage } from '../auth/LoginPage';
import { ProtectedRoute } from '../auth/ProtectedRoute';
import { AnalyticsDashboard } from '../components/dashboard/AnalyticsDashboard';
import { CaseListPage } from '../components/incidents/CaseListPage';
import { CaseDetailPage } from '../components/incidents/CaseDetailPage';
import { FinancialPage } from '../components/financial/FinancialPage';
import { ReportPage } from '../components/reports/ReportPage';
import { ChatPage } from '../components/chat/ChatPage';
import { HelpPage } from '../components/help/HelpPage';
import { GraphPage } from '../components/graph/GraphPage';
import { SearchPage } from '../components/search/SearchPage';
import { HomePage } from '../components/home/HomePage';

export const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/help" element={<HelpPage />} />
      <Route path="/*" element={
        <ProtectedRoute>
          <AppLayout>
            <Routes>
              <Route path="/dashboard" element={<AnalyticsDashboard />} />
              <Route path="/incidents" element={<CaseListPage />} />
              <Route path="/incidents/:id" element={<CaseDetailPage />} />
              <Route path="/financial" element={<FinancialPage />} />
              <Route path="/reports" element={<ReportPage />} />
              <Route path="/graph" element={<GraphPage />} />
              <Route path="/search" element={<SearchPage />} />
              <Route path="/chat" element={<ChatPage />} />
            </Routes>
          </AppLayout>
        </ProtectedRoute>
      } />
    </Routes>
  );
};
