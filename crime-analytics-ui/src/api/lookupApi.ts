import apiClient from './apiClient';

export interface State {
  stateId: number;
  stateName: string;
}

export interface District {
  districtId: number;
  districtName: string;
  stateId: number;
}

export interface Unit {
  unitId: number;
  unitName: string;
  districtId: number;
}

export interface CrimeHead {
  crimeHeadId: number;
  crimeGroupName: string;
}

export interface CrimeSubHead {
  crimeSubHeadId: number;
  crimeHeadName: string;
  seqId: number;
}

export interface CaseStatus {
  caseStatusId: number;
  caseStatusName: string;
}

export interface CaseCategory {
  caseCategoryId: number;
  lookupValue: string;
}

export interface GravityOffence {
  gravityOffenceId: number;
  lookupValue: string;
}

export interface Court {
  courtId: number;
  courtName: string;
}

export interface Employee {
  employeeId: number;
  firstName: string;
  kgid: string;
}

export interface Rank {
  rankId: number;
  rankName: string;
}

export interface Designation {
  designationId: number;
  designationName: string;
}

export const lookupApi = {
  getStates: () =>
    apiClient.get<State[]>('/api/v1/lookups/states').then((r) => r.data),

  getDistricts: (stateId?: number) =>
    apiClient.get<District[]>('/api/v1/lookups/districts', { params: { stateId } }).then((r) => r.data),

  getUnits: (districtId?: number) =>
    apiClient.get<Unit[]>('/api/v1/lookups/units', { params: districtId ? { districtId } : {} }).then((r) => r.data),

  getCrimeHeads: () =>
    apiClient.get<CrimeHead[]>('/api/v1/lookups/crime-heads').then((r) => r.data),

  getSubHeads: (crimeHeadId: number) =>
    apiClient.get<CrimeSubHead[]>(`/api/v1/lookups/crime-heads/${crimeHeadId}/sub-heads`).then((r) => r.data),

  getStatuses: () =>
    apiClient.get<CaseStatus[]>('/api/v1/lookups/statuses').then((r) => r.data),

  getCategories: () =>
    apiClient.get<CaseCategory[]>('/api/v1/lookups/categories').then((r) => r.data),

  getGravityOffences: () =>
    apiClient.get<GravityOffence[]>('/api/v1/lookups/gravity-offences').then((r) => r.data),

  getCourts: () =>
    apiClient.get<Court[]>('/api/v1/lookups/courts').then((r) => r.data),

  getEmployees: () =>
    apiClient.get<Employee[]>('/api/v1/lookups/employees').then((r) => r.data),

  getRanks: () =>
    apiClient.get<Rank[]>('/api/v1/lookups/ranks').then((r) => r.data),

  getDesignations: () =>
    apiClient.get<Designation[]>('/api/v1/lookups/designations').then((r) => r.data),
};
