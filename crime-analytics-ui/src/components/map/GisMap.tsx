import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import { caseApi } from '../../api/caseApi';
import { CaseSearchResult } from '../../types/case';

interface MapPoint {
  lat: number;
  lng: number;
  name: string;
  crimeNo: string;
  status: string;
  crimeHead: string;
  briefFacts: string;
}

const getMarkerColor = (idx: number, total: number): string => {
  const ratio = total > 1 ? idx / (total - 1) : 0;
  if (ratio >= 0.8) return '#ff0000';
  if (ratio >= 0.6) return '#ff6600';
  if (ratio >= 0.4) return '#ffcc00';
  if (ratio >= 0.2) return '#00cc00';
  return '#0066ff';
};

export const GisMap: React.FC = () => {
  const [points, setPoints] = useState<MapPoint[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const allCases = await caseApi.search({}, 0, 100);
        const cases: CaseSearchResult[] = allCases.content;

        const pts: MapPoint[] = [];
        for (const c of cases) {
          if (c.latitude != null && c.longitude != null) {
            pts.push({
              lat: c.latitude,
              lng: c.longitude,
              name: c.crimeNo || `Case #${c.caseMasterId}`,
              crimeNo: c.crimeNo,
              status: c.statusName || 'Unknown',
              crimeHead: c.crimeHeadName || 'Unknown',
              briefFacts: c.briefFacts || '',
            });
          }
        }
        setPoints(pts);
      } catch (err) {
        console.error('Failed to load map data:', err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const center: [number, number] = points.length > 0
    ? [points.reduce((s, p) => s + p.lat, 0) / points.length, points.reduce((s, p) => s + p.lng, 0) / points.length]
    : [15.4, 76.5];

  const zoom = points.length > 0 ? (points.length === 1 ? 12 : 7) : 6;

  return (
    <div style={{ width: '100%', height: '600px', borderRadius: '8px', overflow: 'hidden' }}>
      <MapContainer
        center={center}
        zoom={zoom}
        style={{ width: '100%', height: '100%' }}
        scrollWheelZoom={true}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/">CARTO</a>'
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
        />
        {points.map((p, idx) => (
          <CircleMarker
            key={idx}
            center={[p.lat, p.lng]}
            radius={8}
            fillColor={getMarkerColor(idx, points.length)}
            fillOpacity={0.8}
            color={getMarkerColor(idx, points.length)}
            weight={2}
          >
            <Popup>
              <div style={{ fontSize: 13, maxWidth: 280 }}>
                <strong>{p.crimeNo}</strong><br />
                <span style={{ color: '#666' }}>Status:</span> {p.status}<br />
                <span style={{ color: '#666' }}>Crime Head:</span> {p.crimeHead}<br />
                {p.briefFacts && <><br /><em>{p.briefFacts.length > 120 ? p.briefFacts.substring(0, 120) + '...' : p.briefFacts}</em></>}
                <br /><span style={{ color: '#999', fontSize: 11 }}>{p.lat.toFixed(4)}, {p.lng.toFixed(4)}</span>
              </div>
            </Popup>
          </CircleMarker>
        ))}
      </MapContainer>
      {loading && (
        <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%,-50%)', color: '#999', fontSize: 14 }}>
          Loading map...
        </div>
      )}
    </div>
  );
};
