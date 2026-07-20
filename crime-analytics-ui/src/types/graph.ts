export interface GraphNode {
  id: string;
  label: string;
  type: 'Person' | 'Account' | 'Incident';
  riskScore?: number;
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  label: string;
  weight: number;
}

export interface GraphData {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface HotspotData {
  lat: number;
  lng: number;
  density: number;
  crimeType: string;
}

export interface BeatBoundary {
  beatNumber: string;
  name: string;
  coordinates: number[][][];
  crimeCount: number;
}
