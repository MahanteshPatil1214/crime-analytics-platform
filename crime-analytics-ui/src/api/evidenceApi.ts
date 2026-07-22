import apiClient from './apiClient';

export interface Evidence {
  evidenceId: number;
  caseMasterId: number;
  fileName: string;
  originalName: string;
  fileType: string;
  fileSize: number;
  description: string;
  uploadedBy: number;
  uploadDate: string;
}

export const evidenceApi = {
  list: (caseId: number) =>
    apiClient.get<Evidence[]>(`/api/v1/cases/${caseId}/evidence`).then(r => r.data),

  upload: (caseId: number, file: File, description?: string, uploadedBy?: number) => {
    const form = new FormData();
    form.append('file', file);
    if (description) form.append('description', description);
    if (uploadedBy) form.append('uploadedBy', String(uploadedBy));
    return apiClient.post<Evidence>(`/api/v1/cases/${caseId}/evidence`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }).then(r => r.data);
  },

  download: async (caseId: number, evidenceId: number, fileName?: string) => {
    const response = await apiClient.get(`/api/v1/cases/${caseId}/evidence/${evidenceId}/download`, {
      responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', fileName || 'evidence');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  delete: (caseId: number, evidenceId: number) =>
    apiClient.delete(`/api/v1/cases/${caseId}/evidence/${evidenceId}`).then(r => r.data),
};
