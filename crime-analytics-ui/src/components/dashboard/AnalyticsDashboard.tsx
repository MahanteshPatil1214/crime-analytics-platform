import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Spin, Typography } from 'antd';
import {
  AlertOutlined,
  FileTextOutlined,
  WarningOutlined,
  CheckCircleOutlined,
  DollarOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, PieChart, Pie } from 'recharts';
import { GisMap } from '../map/GisMap';
import { caseApi } from '../../api/caseApi';
import { lookupApi } from '../../api/lookupApi';
import { financialApi } from '../../api/financialApi';

const { Title, Text } = Typography;
const PIE_COLORS = ['#ff4d4f', '#faad14', '#1890ff', '#722ed1', '#52c41a'];

const statCards = [
  { key: 'totalCases', label: 'Total Cases', icon: <FileTextOutlined />, gradient: 'linear-gradient(135deg, #1890ff, #096dd9)', shadow: '0 4px 14px rgba(24,144,255,0.25)' },
  { key: 'openCases', label: 'Open Cases', icon: <AlertOutlined />, gradient: 'linear-gradient(135deg, #faad14, #d48806)', shadow: '0 4px 14px rgba(250,173,20,0.25)' },
  { key: 'underInvestigation', label: 'Under Investigation', icon: <SearchOutlined />, gradient: 'linear-gradient(135deg, #722ed1, #531dab)', shadow: '0 4px 14px rgba(114,46,209,0.25)' },
  { key: 'chargeSheeted', label: 'Charge Sheeted', icon: <WarningOutlined />, gradient: 'linear-gradient(135deg, #ff4d4f, #cf1322)', shadow: '0 4px 14px rgba(255,77,79,0.25)' },
  { key: 'closed', label: 'Closed', icon: <CheckCircleOutlined />, gradient: 'linear-gradient(135deg, #52c41a, #389e0d)', shadow: '0 4px 14px rgba(82,196,26,0.25)' },
  { key: 'flagged', label: 'Flagged Txns', icon: <DollarOutlined />, gradient: 'linear-gradient(135deg, #ff4d4f, #a8071a)', shadow: '0 4px 14px rgba(255,77,79,0.3)' },
];

export const AnalyticsDashboard: React.FC = () => {
  const [stats, setStats] = useState<Record<string, number>>({});
  const [totalFlagged, setTotalFlagged] = useState(0);
  const [totalAmount, setTotalAmount] = useState(0);
  const [districtData, setDistrictData] = useState<{ name: string; count: number }[]>([]);
  const [crimeHeadData, setCrimeHeadData] = useState<{ name: string; count: number }[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        const [caseStats, districtStats, crimeHeadStats, finStats] = await Promise.all([
          caseApi.getStats(),
          caseApi.getDistrictStats().catch(() => []),
          caseApi.getCrimeHeadStats().catch(() => []),
          financialApi.getStats().catch(() => ({ flaggedCount: 0, totalAmount: 0 })),
        ]);

        setStats(caseStats);
        setTotalFlagged(finStats.flaggedCount);
        setTotalAmount(finStats.totalAmount);

        if (districtStats.length > 0) {
          const units = await lookupApi.getUnits().catch(() => []);
          const unitMap: Record<number, string> = {};
          units.forEach((u) => { unitMap[u.unitId] = u.unitName; });
          setDistrictData(
            districtStats
              .map((d) => ({ name: unitMap[d.unitId] || String(d.unitId), count: d.count }))
              .sort((a, b) => b.count - a.count)
          );
        }

        if (crimeHeadStats.length > 0) {
          const heads = await lookupApi.getCrimeHeads().catch(() => []);
          const headMap: Record<number, string> = {};
          heads.forEach((h) => { headMap[h.crimeHeadId] = h.crimeGroupName; });
          setCrimeHeadData(
            crimeHeadStats.map((c) => ({ name: headMap[c.crimeHeadId] || String(c.crimeHeadId), count: c.count }))
          );
        }
      } catch (err) {
        console.error('Failed to load dashboard stats:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  const getStatValue = (key: string) => {
    if (key === 'flagged') return totalFlagged;
    return stats[key] ?? 0;
  };

  return (
    <div style={{ padding: 24, background: '#f5f7fa', minHeight: '100vh' }}>
      <Spin spinning={loading}>
        {/* Stat Cards */}
        <Row gutter={[14, 14]} style={{ marginBottom: 20 }}>
          {statCards.map((card) => (
            <Col xs={12} sm={8} md={4} key={card.key}>
              <div
                style={{
                  background: card.gradient,
                  borderRadius: 12,
                  padding: '18px 16px',
                  boxShadow: card.shadow,
                  transition: 'transform 0.2s, box-shadow 0.2s',
                  cursor: 'default',
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-2px)';
                  e.currentTarget.style.boxShadow = card.shadow.replace('0.25', '0.4').replace('0.3', '0.45');
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)';
                  e.currentTarget.style.boxShadow = card.shadow;
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                  <div style={{ fontSize: 18, color: 'rgba(255,255,255,0.85)' }}>{card.icon}</div>
                  <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 12, fontWeight: 500 }}>{card.label}</Text>
                </div>
                <div style={{ color: '#fff', fontSize: 28, fontWeight: 700, lineHeight: 1 }}>
                  {getStatValue(card.key)}
                </div>
              </div>
            </Col>
          ))}
        </Row>

        {/* Map + Pie + Financial */}
        <Row gutter={[14, 14]} style={{ marginBottom: 14 }}>
          <Col xs={24} lg={16}>
            <Card
              variant="borderless"
              title={<span style={{ fontWeight: 600 }}>Crime Hotspot Map</span>}
              style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)', minHeight: 460 }}
              styles={{ body: { padding: '12px 16px' } }}
            >
              <GisMap />
            </Card>
          </Col>
          <Col xs={24} lg={8}>
            <Card
              variant="borderless"
              title={<span style={{ fontWeight: 600 }}>Crime Head Distribution</span>}
              style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)', marginBottom: 14 }}
              styles={{ body: { padding: '8px 16px' } }}
            >
              {crimeHeadData.length > 0 ? (
                <ResponsiveContainer width="100%" height={180}>
                  <PieChart>
                    <Pie
                      data={crimeHeadData}
                      cx="50%"
                      cy="50%"
                      outerRadius={70}
                      dataKey="count"
                      nameKey="name"
                      label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                      labelLine={{ strokeWidth: 1 }}
                    >
                      {crimeHeadData.map((_, i) => <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />)}
                    </Pie>
                    <Tooltip
                      contentStyle={{ borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.12)', border: 'none' }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              ) : <Spin />}
            </Card>
            <Card
              variant="borderless"
              title={<span style={{ fontWeight: 600 }}>Financial Summary</span>}
              style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
              styles={{ body: { padding: '16px' } }}
            >
              <div style={{ marginBottom: 16 }}>
                <Text type="secondary" style={{ fontSize: 12 }}>Total Transaction Volume</Text>
                <div style={{ fontSize: 24, fontWeight: 700, color: '#52c41a' }}>
                  ₹{totalAmount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                </div>
              </div>
              <div>
                <Text type="secondary" style={{ fontSize: 12 }}>Flagged Transactions</Text>
                <div style={{ fontSize: 24, fontWeight: 700, color: '#ff4d4f' }}>{totalFlagged}</div>
              </div>
            </Card>
          </Col>
        </Row>

        {/* District Bar Chart */}
        <Card
          variant="borderless"
          title={<span style={{ fontWeight: 600 }}>Cases by District / Unit</span>}
          style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
          styles={{ body: { padding: '12px 16px' } }}
        >
          {districtData.length > 0 ? (
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={districtData} barCategoryGap="20%">
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="name" tick={{ fontSize: 11, fill: '#888' }} />
                <YAxis allowDecimals={false} tick={{ fontSize: 11, fill: '#888' }} />
                <Tooltip
                  contentStyle={{ borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.12)', border: 'none' }}
                />
                <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                  {districtData.map((_, i) => <Cell key={i} fill={['#1890ff', '#52c41a', '#faad14', '#ff4d4f', '#722ed1', '#13c2c2'][i % 6]} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          ) : <Spin />}
        </Card>
      </Spin>
    </div>
  );
};
