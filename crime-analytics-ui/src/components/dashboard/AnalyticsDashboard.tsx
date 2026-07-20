import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Spin } from 'antd';
import { AlertOutlined, FileTextOutlined, WarningOutlined, CheckCircleOutlined, DollarOutlined, SearchOutlined } from '@ant-design/icons';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell, PieChart, Pie } from 'recharts';
import { GisMap } from '../map/GisMap';
import { caseApi } from '../../api/caseApi';
import { lookupApi } from '../../api/lookupApi';
import { financialApi } from '../../api/financialApi';

const PIE_COLORS = ['#ff4d4f', '#faad14', '#1890ff', '#722ed1', '#52c41a'];

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

  return (
    <div style={{ padding: 24, background: '#f0f2f5', minHeight: '100vh' }}>
      <Spin spinning={loading}>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Total Cases" value={stats.totalCases ?? 0} prefix={<FileTextOutlined />} valueStyle={{ color: '#1890ff', fontSize: 20 }} />
            </Card>
          </Col>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Open Cases" value={stats.openCases ?? 0} prefix={<AlertOutlined />} valueStyle={{ color: '#faad14', fontSize: 20 }} />
            </Card>
          </Col>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Under Investigation" value={stats.underInvestigation ?? 0} prefix={<SearchOutlined />} valueStyle={{ color: '#722ed1', fontSize: 20 }} />
            </Card>
          </Col>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Charge Sheeted" value={stats.chargeSheeted ?? 0} prefix={<WarningOutlined />} valueStyle={{ color: '#ff4d4f', fontSize: 20 }} />
            </Card>
          </Col>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Closed" value={stats.closed ?? 0} prefix={<CheckCircleOutlined />} valueStyle={{ color: '#52c41a', fontSize: 20 }} />
            </Card>
          </Col>
          <Col span={4}>
            <Card bordered={false}>
              <Statistic title="Flagged Txns" value={totalFlagged} prefix={<DollarOutlined />} valueStyle={{ color: '#ff4d4f', fontSize: 20 }} />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={16}>
            <Card title="Crime Hotspot Map" bordered={false} style={{ minHeight: 460 }}>
              <GisMap />
            </Card>
          </Col>
          <Col span={8}>
            <Card title="Crime Head Distribution" bordered={false} style={{ minHeight: 220, marginBottom: 16 }}>
              {crimeHeadData.length > 0 ? (
                <ResponsiveContainer width="100%" height={180}>
                  <PieChart>
                    <Pie data={crimeHeadData} cx="50%" cy="50%" outerRadius={70} dataKey="count" nameKey="name" label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}>
                      {crimeHeadData.map((_, i) => <Cell key={i} fill={PIE_COLORS[i % PIE_COLORS.length]} />)}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              ) : <Spin />}
            </Card>
            <Card title="Financial Summary" bordered={false} style={{ minHeight: 220 }}>
              <Row gutter={[16, 16]}>
                <Col span={24}>
                  <Statistic title="Total Transaction Volume" value={totalAmount} prefix="₹" precision={2} valueStyle={{ fontSize: 22, color: '#52c41a' }} />
                </Col>
                <Col span={24}>
                  <Statistic title="Flagged Transactions" value={totalFlagged} valueStyle={{ fontSize: 22, color: '#ff4d4f' }} />
                </Col>
              </Row>
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]}>
          <Col span={24}>
            <Card title="Cases by District/Unit" bordered={false}>
              {districtData.length > 0 ? (
                <ResponsiveContainer width="100%" height={250}>
                  <BarChart data={districtData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                      {districtData.map((_, i) => <Cell key={i} fill={['#1890ff', '#52c41a', '#faad14', '#ff4d4f', '#722ed1', '#13c2c2'][i % 6]} />)}
                    </Bar>
                  </BarChart>
                </ResponsiveContainer>
              ) : <Spin />}
            </Card>
          </Col>
        </Row>
      </Spin>
    </div>
  );
};
