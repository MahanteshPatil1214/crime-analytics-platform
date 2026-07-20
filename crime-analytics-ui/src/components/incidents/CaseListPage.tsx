import React, { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Select, Space } from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { caseApi } from '../../api/caseApi';
import { lookupApi } from '../../api/lookupApi';
import { CaseSearchResult } from '../../types/case';
import type { ColumnsType } from 'antd/es/table';

const statusColors: Record<string, string> = {
  'OPEN': 'processing',
  'UNDER INVESTIGATION': 'warning',
  'CLOSED': 'success',
  'CHARGE SHEETED': 'blue',
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
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontWeight: 600 }}>{v}</span>,
    },
    { title: 'Case No', dataIndex: 'caseNo' },
    { title: 'Registered Date', dataIndex: 'crimeRegisteredDate' },
    {
      title: 'Status',
      dataIndex: 'statusName',
      render: (v: string) => {
        if (!v) return <Tag>—</Tag>;
        return <Tag color={statusColors[v.toUpperCase()] || 'default'}>{v}</Tag>;
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
        <Button type="link" icon={<EyeOutlined />} onClick={() => navigate(`/incidents/${r.caseMasterId}`)} />
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card
        title="FIR Cases"
        bordered={false}
        extra={
          <Space>
            <Input
              placeholder="Crime No"
              prefix={<SearchOutlined />}
              value={crimeNoFilter}
              onChange={(e) => setCrimeNoFilter(e.target.value)}
              onPressEnter={() => { setPage(0); fetchCases(0); }}
              style={{ width: 160 }}
            />
            <Select
              placeholder="Status"
              allowClear value={statusFilter}
              onChange={(v) => setStatusFilter(v)}
              style={{ width: 180 }}
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
              style={{ width: 200 }}
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
              style={{ width: 180 }}
            >
              {districts.map((d) => (
                <Select.Option key={d.districtId} value={d.districtId} label={d.districtName}>{d.districtName}</Select.Option>
              ))}
            </Select>
            <Button type="primary" icon={<SearchOutlined />} onClick={() => { setPage(0); fetchCases(0); }}>Search</Button>
            <Button icon={<ReloadOutlined />} onClick={() => {
              setCrimeNoFilter(''); setStatusFilter(undefined); setCrimeHeadFilter(undefined); setDistrictFilter(undefined); setPage(0); fetchCases(0);
            }}>Reset</Button>
          </Space>
        }
      >
        <Table
          rowKey="caseMasterId"
          columns={columns}
          dataSource={cases}
          loading={loading}
          pagination={{
            current: page + 1,
            pageSize: 12,
            total,
            onChange: (p) => { setPage(p - 1); fetchCases(p - 1); },
          }}
        />
      </Card>
    </div>
  );
};
