import apiClient from './apiClient';

export const reportApi = {
  downloadFir: async (caseId: number): Promise<void> => {
    const response = await apiClient.get(`/api/v1/reports/fir/${caseId}`, {
      responseType: 'blob',
    });
    const blob = new Blob([response.data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `FIR_Report_${caseId}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  generateIncidentReport: async (values: Record<string, any>): Promise<Blob> => {
    const response = await apiClient.post('/api/v1/reports/incident', values, {
      responseType: 'blob',
    });
    return new Blob([response.data], { type: 'application/pdf' });
  },

  generateCriminalProfile: async (values: Record<string, any>): Promise<Blob> => {
    const response = await apiClient.post('/api/v1/reports/criminal-profile', values, {
      responseType: 'blob',
    });
    return new Blob([response.data], { type: 'application/pdf' });
  },
};
