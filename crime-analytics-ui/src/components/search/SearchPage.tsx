import React, { useState, useEffect, useCallback } from 'react';
import { Card, Input, Button, Space, Select, Table, Tag, message, Drawer, Descriptions, Empty, Row, Col, Tooltip, Divider, DatePicker } from 'antd';
import { SearchOutlined, FilterOutlined, EyeOutlined, CloseOutlined, DownloadOutlined } from '@ant-design/icons';
import { caseApi } from '../../api/caseApi';
import { lookupApi, District, CaseStatus, CrimeHead } from '../../api/lookupApi';
import type { CaseSearchResult } from '../../types/case';
import dayjs from 'dayjs';

const statusColors: Record<string, string> = {
  'REGISTERED': 'red',
  'UNDER INVESTIGATION': 'gold',
  'CHARGE SHEETED': 'purple',
  'CLOSED': 'green',
  'CONVICTED': 'volcano',
};

const fmtDate = (v: string) => v ? dayjs(v).format('DD MMM YYYY') : '-';

const renderNames = (names: string[] | undefined, color: string) => {
  if (!names || names.length === 0) {
    return <span style={{ color: '#bbb' }}>—</span>;
  }
  return names.map(n => <Tag key={n} color={color} style={{ marginBottom: 2 }}>{n}</Tag>);
};

const renderActs = (sections: string[] | undefined) => {
  if (!sections || sections.length === 0) {
    return <span style={{ color: '#bbb' }}>—</span>;
  }
  return sections.slice(0, 3).map(a => <Tag key={a} style={{ fontSize: 11 }}>{a}</Tag>);
};

function exportCSV(results: CaseSearchResult[]) {
  const headers = ['Crime No', 'Date', 'District', 'Police Station', 'Crime Head', 'Status', 'Accused', 'Victims', 'Complainants', 'Acts', 'Facts'];
  const rows = results.map(r => [
    r.crimeNo,
    fmtDate(r.crimeRegisteredDate),
    r.districtName || '',
    r.policeStationName || '',
    r.crimeHeadName || '',
    r.statusName || '',
    (r.accusedNames || []).join('; '),
    (r.victimNames || []).join('; '),
    (r.complainantNames || []).join('; '),
    (r.actSections || []).join('; '),
    (r.briefFacts || '').replace(/"/g, '""'),
  ].map(v => `"${v}"`).join(','));

  const csv = '\uFEFF' + [headers.join(','), ...rows].join('\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `crime_search_${dayjs().format('YYYYMMDD_HHmmss')}.csv`;
  a.click();
  URL.revokeObjectURL(url);
}

export const SearchPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<CaseSearchResult[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);

  const [crimeNo, setCrimeNo] = useState('');
  const [briefFacts, setBriefFacts] = useState('');
  const [selectedDistrict, setSelectedDistrict] = useState<number | undefined>();
  const [selectedStatus, setSelectedStatus] = useState<number | undefined>();
  const [selectedCrimeHead, setSelectedCrimeHead] = useState<number | undefined>();
  const [dateRange, setDateRange] = useState<[dayjs.Dayjs | null, dayjs.Dayjs | null] | null>(null);

  const [districts, setDistricts] = useState<District[]>([]);
  const [statuses, setStatuses] = useState<CaseStatus[]>([]);
  const [crimeHeads, setCrimeHeads] = useState<CrimeHead[]>([]);

  const [drawerCase, setDrawerCase] = useState<CaseSearchResult | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);

  useEffect(() => {
    Promise.all([
      lookupApi.getDistricts(),
      lookupApi.getStatuses(),
      lookupApi.getCrimeHeads(),
    ]).then(([d, s, c]) => {
      setDistricts(d);
      setStatuses(s);
      setCrimeHeads(c);
    }).catch(() => message.warning('Failed to load filters'));
  }, []);

  const doSearch = useCallback(async (p = 0) => {
    setLoading(true);
    try {
      const params: Record<string, any> = {};
      if (crimeNo) params.crimeNo = crimeNo;
      if (briefFacts) params.briefFacts = briefFacts;
      if (selectedDistrict !== undefined) params.district = districts.find(d => d.districtId === selectedDistrict)?.districtName;
      if (selectedStatus !== undefined) params.statusId = selectedStatus;
      if (selectedCrimeHead !== undefined) params.crimeHeadId = selectedCrimeHead;
      if (dateRange?.[0]) params.startDate = dateRange[0].format('YYYY-MM-DD');
      if (dateRange?.[1]) params.endDate = dateRange[1].format('YYYY-MM-DD');

      const res = await caseApi.search(params, p, pageSize);
      setResults(res.content);
      setTotal(res.totalElements);
      setPage(res.number);
    } catch {
      message.error('Search failed');
    } finally {
      setLoading(false);
    }
  }, [crimeNo, briefFacts, selectedDistrict, selectedStatus, selectedCrimeHead, dateRange, pageSize, districts]);

  useEffect(() => {
    doSearch(0);
  }, []);

  const clearFilters = () => {
    setCrimeNo('');
    setBriefFacts('');
    setSelectedDistrict(undefined);
    setSelectedStatus(undefined);
    setSelectedCrimeHead(undefined);
    setDateRange(null);
    doSearch(0);
  };

  const openDrawer = (c: CaseSearchResult) => {
    setDrawerCase(c);
    setDrawerVisible(true);
  };

  const columns = [
    {
      title: 'Crime No', dataIndex: 'crimeNo', key: 'crimeNo', width: 130,
      render: (v: string) => <Tag color="blue" style={{ fontFamily: 'monospace' }}>{v}</Tag>,
    },
    {
      title: 'Date', dataIndex: 'crimeRegisteredDate', key: 'date', width: 110,
      render: (v: string) => fmtDate(v),
    },
    {
      title: 'District', dataIndex: 'districtName', key: 'district', width: 110,
      render: (v: string) => v || <span style={{ color: '#bbb' }}>—</span>,
    },
    {
      title: 'Police Station', dataIndex: 'policeStationName', key: 'station', width: 130,
      render: (v: string) => v || <span style={{ color: '#bbb' }}>—</span>,
    },
    {
      title: 'Crime Head', dataIndex: 'crimeHeadName', key: 'crimeHead', width: 150,
      render: (v: string) => v ? <Tag color="geekblue">{v}</Tag> : <span style={{ color: '#bbb' }}>—</span>,
    },
    {
      title: 'Status', dataIndex: 'statusName', key: 'status', width: 130,
      render: (v: string) => {
        if (!v) return <Tag>—</Tag>;
        const color = statusColors[v.toUpperCase().replace(/\s+/g, ' ').trim()] || 'default';
        return <Tag color={color} style={{ fontWeight: 600 }}>{v}</Tag>;
      },
    },
    {
      title: 'Accused', key: 'accused', width: 150, ellipsis: true,
      render: (_: any, r: CaseSearchResult) => renderNames(r.accusedNames, 'red'),
    },
    {
      title: 'Victims', key: 'victims', width: 130, ellipsis: true,
      render: (_: any, r: CaseSearchResult) => renderNames(r.victimNames, 'green'),
    },
    {
      title: 'Acts', key: 'acts', width: 110,
      render: (_: any, r: CaseSearchResult) => renderActs(r.actSections),
    },
    {
      title: 'Facts', dataIndex: 'briefFacts', key: 'facts', ellipsis: true,
      render: (v: string) => {
        if (!v) return <span style={{ color: '#bbb' }}>—</span>;
        return <Tooltip title={v}><span>{v.substring(0, 80)}...</span></Tooltip>;
      },
    },
    {
      title: 'Action', key: 'action', width: 64,
      render: (_: any, r: CaseSearchResult) => (
        <Tooltip title="Quick Preview">
          <Button type="default" size="small" icon={<EyeOutlined />} onClick={() => openDrawer(r)}
            style={{ borderRadius: 4 }} />
        </Tooltip>
      ),
    },
  ];

  const filtersActive = crimeNo || briefFacts || selectedDistrict !== undefined || selectedStatus !== undefined || selectedCrimeHead !== undefined || dateRange?.[0];

  return (
    <div style={{ padding: 16 }}>
      <Card style={{ marginBottom: 16 }} size="small">
        <Row gutter={[12, 12]}>
          <Col xs={24} sm={12} md={6} lg={4}>
            <Input placeholder="Crime No" value={crimeNo} onChange={e => setCrimeNo(e.target.value)}
              prefix={<SearchOutlined />} allowClear size="middle" />
          </Col>
          <Col xs={24} sm={12} md={6} lg={5}>
            <Input placeholder="Brief Facts (keyword)" value={briefFacts} onChange={e => setBriefFacts(e.target.value)}
              prefix={<SearchOutlined />} allowClear size="middle" />
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Select placeholder="District" value={selectedDistrict} onChange={v => setSelectedDistrict(v)}
              allowClear style={{ width: '100%' }} showSearch optionFilterProp="children" size="middle">
              {districts.map(d => <Select.Option key={d.districtId} value={d.districtId}>{d.districtName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Select placeholder="Status" value={selectedStatus} onChange={v => setSelectedStatus(v)}
              allowClear style={{ width: '100%' }} size="middle">
              {statuses.map(s => <Select.Option key={s.caseStatusId} value={s.caseStatusId}>{s.caseStatusName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={24} sm={8} md={6} lg={4}>
            <Select placeholder="Crime Head" value={selectedCrimeHead} onChange={v => setSelectedCrimeHead(v)}
              allowClear style={{ width: '100%' }} showSearch optionFilterProp="children" size="middle">
              {crimeHeads.map(c => <Select.Option key={c.crimeHeadId} value={c.crimeHeadId}>{c.crimeGroupName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={5}>
            <DatePicker.RangePicker
              value={dateRange as any}
              onChange={dates => setDateRange(dates as any)}
              style={{ width: '100%' }}
              size="middle"
              placeholder={['Start Date', 'End Date']}
            />
          </Col>
        </Row>
        <Divider style={{ margin: '10px 0' }} />
        <Space wrap>
          <Button type="primary" icon={<SearchOutlined />} onClick={() => doSearch(0)} loading={loading}
            size="middle">Search</Button>
          {filtersActive && (
            <Button type="link" onClick={clearFilters} style={{ color: '#999' }}
              icon={<CloseOutlined />}>Clear Filters</Button>
          )}
        </Space>
      </Card>

      <Card bodyStyle={{ padding: 0 }} size="small"
        title={
          <span style={{ fontSize: 14, color: '#666' }}>
            <FilterOutlined style={{ marginRight: 6 }} />
            {total} case{total !== 1 ? 's' : ''} found
          </span>
        }
        extra={
          results.length > 0 && (
            <Button icon={<DownloadOutlined />} size="small" onClick={() => exportCSV(results)}>
              Export CSV
            </Button>
          )
        }
      >
        <Table
          dataSource={results}
          columns={columns}
          rowKey="caseMasterId"
          loading={loading}
          size="small"
          pagination={{
            current: page + 1,
            pageSize,
            total,
            showSizeChanger: true,
            showTotal: (t) => `${t} cases total`,
            onChange: (p, ps) => { setPage(p - 1); setPageSize(ps); doSearch(p - 1); },
          }}
          locale={{ emptyText: <Empty description="No cases match your filters" /> }}
        />
      </Card>

      <Drawer
        title={drawerCase ? `Case: ${drawerCase.crimeNo}` : ''}
        placement="right"
        width={520}
        open={drawerVisible}
        onClose={() => setDrawerVisible(false)}
        extra={<Button type="text" icon={<CloseOutlined />} onClick={() => setDrawerVisible(false)} />}
      >
        {drawerCase && (
          <>
            <Descriptions column={1} size="small" bordered style={{ marginBottom: 16 }}>
              <Descriptions.Item label="Crime No">
                <Tag color="blue">{drawerCase.crimeNo}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="Case No">{drawerCase.caseNo || '-'}</Descriptions.Item>
              <Descriptions.Item label="Registered Date">
                {fmtDate(drawerCase.crimeRegisteredDate)}
              </Descriptions.Item>
              <Descriptions.Item label="District">{drawerCase.districtName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Police Station">{drawerCase.policeStationName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Crime Head">
                {drawerCase.crimeHeadName ? <Tag color="geekblue">{drawerCase.crimeHeadName}</Tag> : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Status">
                <Tag color={statusColors[drawerCase.statusName?.toUpperCase().replace(/\s+/g, ' ').trim()] || 'default'}>
                  {drawerCase.statusName || '-'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>

            <Divider orientation="left" orientationMargin={0}>Brief Facts</Divider>
            <p style={{ fontSize: 13, lineHeight: 1.6, whiteSpace: 'pre-wrap', background: '#fafafa', padding: 12, borderRadius: 6 }}>
              {drawerCase.briefFacts || 'No facts recorded'}
            </p>

            {drawerCase.accusedNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Accused</Divider>
                <div>{drawerCase.accusedNames.map(a => <Tag key={a} color="red">{a}</Tag>)}</div>
              </>
            )}

            {drawerCase.victimNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Victims</Divider>
                <div>{drawerCase.victimNames.map(v => <Tag key={v} color="green">{v}</Tag>)}</div>
              </>
            )}

            {drawerCase.complainantNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Complainants</Divider>
                <div>{drawerCase.complainantNames.map(c => <Tag key={c} color="orange">{c}</Tag>)}</div>
              </>
            )}

            {drawerCase.actSections?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Act Sections</Divider>
                <div>{drawerCase.actSections.map(a => <Tag key={a} style={{ fontFamily: 'monospace' }}>{a}</Tag>)}</div>
              </>
            )}
          </>
        )}
      </Drawer>
    </div>
  );
};
