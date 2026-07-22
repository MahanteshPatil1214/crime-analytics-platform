import apiClient from './apiClient';
import { PageResponse } from '../types/case';

export interface Person {
  id: string;
  biometricId?: string;
  firstName: string;
  lastName: string;
  dateOfBirth?: string;
  gender?: string;
  nationality?: string;
  addressHash?: string;
  phoneHash?: string;
  personType: 'SUSPECT' | 'VICTIM' | 'WITNESS' | 'ARRESTED' | 'CONVICTED';
  convictionCount: number;
  isKnownOffender: boolean;
  riskScore?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface PersonSearchParams {
  name?: string;
  personType?: string;
  minRisk?: number;
  maxRisk?: number;
  page?: number;
  size?: number;
}

export const personApi = {
  search: (params: PersonSearchParams = {}) =>
    apiClient.get<PageResponse<Person>>('/api/v1/persons/search', { params }).then((r) => r.data),

  getById: (id: string) =>
    apiClient.get<Person>(`/api/v1/persons/${id}`).then((r) => r.data),

  getKnownOffenders: (page = 0, size = 20) =>
    apiClient.get<PageResponse<Person>>('/api/v1/persons/offenders', { params: { page, size } }).then((r) => r.data),

  getRiskDistribution: () =>
    apiClient.get<Record<string, number>>('/api/v1/persons/stats/risk-distribution').then((r) => r.data),

  create: (data: Partial<Person>) =>
    apiClient.post<Person>('/api/v1/persons', data).then((r) => r.data),

  update: (id: string, data: Partial<Person>) =>
    apiClient.put<Person>(`/api/v1/persons/${id}`, data).then((r) => r.data),

  delete: (id: string) =>
    apiClient.delete(`/api/v1/persons/${id}`).then((r) => r.data),
};
