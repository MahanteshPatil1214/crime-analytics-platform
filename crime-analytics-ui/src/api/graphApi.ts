import apiClient from './apiClient';
import { GraphData, HotspotData, BeatBoundary } from '../types/graph';

export const graphApi = {
  getNetwork: (seedPersonId: string, depth = 2, minWeight = 0.05) =>
    apiClient.get<GraphData>('/api/v1/graph/network', {
      params: { seedPersonId, depth, minWeight },
    }).then((r) => r.data),

  getHotspots: (nibrsCode: string, days = 30, minDensity = 0.01) =>
    apiClient.get<HotspotData[]>('/api/v1/analytics/hotspots', {
      params: { nibrsCode, days, minDensity },
    }).then((r) => r.data),

  getBeatStats: () =>
    apiClient.get<BeatBoundary[]>('/api/v1/incidents/beats/stats').then((r) => r.data),
};
