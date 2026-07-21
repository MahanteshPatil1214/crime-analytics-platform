import React, { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Select, Space, Typography } from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined, FilterOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { caseApi } from '../../api/caseApi';
import { lookupApi } from '../../api/lookupApi';
import { CaseSearchResult } from '../../types/case';
import type { ColumnsType } from 'antd/es/table';

const { Text } = Typography;

const statusStyles: Record<string, { color: string; bg: string; border: string }> = {
  'OPEN': { color: '#1890ff', bg: '#e6f7ff', border: '#91d5ff' },
  'UNDER INVESTIGATION': { color: '#faad14', bg: '#fffbe6', border: '#ffe58f' },
  'CLOSED': { color: '#52c41a', bg: '#f6ffed', border: '#b7eb8f' },
  'CHARGE SHEETED': { color: '#ff4d4f', bg: '#fff1f0', border: '#ffa39e' },
};

export const CaseListPage: React.FC = () => {
  const navigate = useNavigate();
  const [cases, setCases] = useState<CaseSearchResult[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [crimeNoFilter, setCrimeNoFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState<number | undefined>(undefined);
  const [crimeHeadFilter, setCrimeHeadFilter] = useState<number | undefined>(undefined);
  const [districtFilter, setDistrictFilter] = useState<number | undefined>(undefined);
  const [statuses, setStatuses] = useState<{ caseStatusId: number; caseStatusName: string }[]>([]);
  const [crimeHeads, setCrimeHeads] = useState<{ crimeHeadId: number; crimeGroupName: string }[]>([]);
  const [districts, setDistricts] = useState<{ districtId: number; districtName: string }[]>([]);

  useEffect(() => {
    lookupApi.getStatuses().then(setStatuses).catch(() => {});
    lookupApi.getCrimeHeads().then(setCrimeHeads).catch(() => {});
    lookupApi.getDistricts().then(setDistricts).catch(() => {});
  }, []);

  const fetchCases = async (p = page) => {
    setLoading(true);
    try {
      const params: Record<string, any> = {};
      if (crimeNoFilter) params.crimeNo = crimeNoFilter;
      if (statusFilter) params.statusId = statusFilter;
      if (crimeHeadFilter) params.crimeHeadId = crimeHeadFilter;
      if (districtFilter) params.district = districts.find(d => d.districtId === districtFilter)?.districtName;
      const data = await caseApi.search(params, p, 12);
      setCases(data.content);
      setTotal(data.totalElements);
    } catch (err) {
      console.error('Failed to load cases:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCases(0); }, []);

  const columns: ColumnsType<CaseSearchResult> = [
    {
      title: 'Crime No',
      dataIndex: 'crimeNo',
      render: (v: string) => (
        <span style={{ fontFamily: 'monospace', fontWeight: 600, color: '#1890ff', fontSize: 13 }}>{v}</span>
      ),
    },
    { title: 'Case No', dataIndex: 'caseNo' },
    { title: 'Registered Date', dataIndex: 'crimeRegisteredDate' },
    {
      title: 'Status',
      dataIndex: 'statusName',
      render: (v: string) => {
        if (!v) return <Tag>—</Tag>;
        const s = statusStyles[v.toUpperCase()] || { color: '#888', bg: '#fafafa', border: '#d9d9d9' };
        return (
          <Tag
            style={{
              color: s.color,
              background: s.bg,
              border: `1px solid ${s.border}`,
              borderRadius: 12,
              padding: '1px 10px',
              fontSize: 12,
              fontWeight: 500,
            }}
          >
            {v}
          </Tag>
        );
      },
    },
    {
      title: 'Crime Head',
      dataIndex: 'crimeHeadName',
      render: (v: string) => v || '—',
    },
    { title: 'District', dataIndex: 'districtName', render: (v: string) => v || '—' },
    {
      title: '',
      key: 'view',
      width: 60,
      render: (_, r) => (
        <Button
          type="primary"
          shape="circle"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => navigate(`/incidents/${r.caseMasterId}`)}
          style={{ boxShadow: '0 2px 6px rgba(24,144,255,0.3)' }}
        />
      ),
    },
  ];

  return (
    <div style={{ padding: 24, background: '#f5f7fa', minHeight: '100vh' }}>
      <Card
        variant="borderless"
        style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
        styles={{ body: { padding: 0 } }}
      >
        {/* Filter Bar */}
        <div
          style={{
            padding: '16px 20px',
            background: 'linear-gradient(135deg, #fafbfc 0%, #f0f2f5 100%)',
            borderBottom: '1px solid #f0f0f0',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            flexWrap: 'wrap',
          }}
        >
          <FilterOutlined style={{ color: '#888', fontSize: 14 }} />
          <Input
            placeholder="Crime No"
            prefix={<SearchOutlined style={{ color: '#bbb' }} />}
            value={crimeNoFilter}
            onChange={(e) => setCrimeNoFilter(e.target.value)}
            onPressEnter={() => { setPage(0); fetchCases(0); }}
            style={{ width: 160, borderRadius: 8 }}
            allowClear
          />
          <Select
            placeholder="Status"
            allowClear value={statusFilter}
            onChange={(v) => setStatusFilter(v)}
            style={{ width: 180, borderRadius: 8 }}
          >
            {statuses.map((s) => (
              <Select.Option key={s.caseStatusId} value={s.caseStatusId}>{s.caseStatusName}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="Crime Head"
            allowClear showSearch optionFilterProp="label"
            value={crimeHeadFilter}
            onChange={(v) => setCrimeHeadFilter(v)}
            style={{ width: 200, borderRadius: 8 }}
          >
            {crimeHeads.map((h) => (
              <Select.Option key={h.crimeHeadId} value={h.crimeHeadId} label={h.crimeGroupName}>{h.crimeGroupName}</Select.Option>
            ))}
          </Select>
          <Select
            placeholder="District"
            allowClear showSearch optionFilterProp="label"
            value={districtFilter}
            onChange={(v) => setDistrictFilter(v)}
            style={{ width: 180, borderRadius: 8 }}
          >
            {districts.map((d) => (
              <Select.Option key={d.districtId} value={d.districtId} label={d.districtName}>{d.districtName}</Select.Option>
            ))}
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={() => { setPage(0); fetchCases(0); }} style={{ borderRadius: 8 }}>
            Search
          </Button>
          <Button icon={<ReloadOutlined />} onClick={() => {
            setCrimeNoFilter(''); setStatusFilter(undefined); setCrimeHeadFilter(undefined); setDistrictFilter(undefined); setPage(0); fetchCases(0);
          }} style={{ borderRadius: 8 }}>
            Reset
          </Button>
        </div>

        {/* Table */}
        <div style={{ padding: '0 4px' }}>
          <Table
            rowKey="caseMasterId"
            columns={columns}
            dataSource={cases}
            loading={loading}
            pagination={{
              current: page + 1,
              pageSize: 12,
              total,
              showSizeChanger: false,
              showTotal: (t) => <Text type="secondary" style={{ fontSize: 13 }}>{t} cases found</Text>,
              onChange: (p) => { setPage(p - 1); fetchCases(p - 1); },
            }}
            rowHoverStyle={{ background: '#e6f7ff' }}
          />
        </div>
      </Card>
    </div>
  );
};
