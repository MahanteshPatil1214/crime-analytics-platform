import React, { useState, useEffect, useCallback } from 'react';
import { Card, Input, Button, Space, Select, Table, Tag, message, Drawer, Descriptions, Empty, Row, Col, Tooltip, Divider, DatePicker } from 'antd';
import { SearchOutlined, FilterOutlined, EyeOutlined, CloseOutlined, DownloadOutlined, CopyOutlined } from '@ant-design/icons';
import { caseApi } from '../../api/caseApi';
import { lookupApi, District, CaseStatus, CrimeHead } from '../../api/lookupApi';
import type { CaseSearchResult } from '../../types/case';
import dayjs from 'dayjs';

const statusStyles: Record<string, React.CSSProperties> = {
  'REGISTERED': { background: '#fff1f0', color: '#cf1322', border: '1px solid #ffa39e' },
  'UNDER INVESTIGATION': { background: '#fffbe6', color: '#ad8b00', border: '1px solid #ffe58f' },
  'CHARGE SHEETED': { background: '#f9f0ff', color: '#722ed1', border: '1px solid #d3adf7' },
  'CLOSED': { background: '#f6ffed', color: '#389e0d', border: '1px solid #b7eb8f' },
  'CONVICTED': { background: '#fff2e8', color: '#d4380d', border: '1px solid #ffbb96' },
};

const statusStyle = (name: string | undefined): React.CSSProperties => {
  const raw = name?.toUpperCase().replace(/\s+/g, ' ') ?? '';
  const key = Object.keys(statusStyles).find(k => raw.includes(k)) || raw;
  return statusStyles[key] || { background: '#fafafa', color: '#666', border: '1px solid #d9d9d9' };
};

const fmtDate = (v: string) => v ? dayjs(v).format('DD MMM YYYY') : '-';

const fmtCrimeNo = (v: string) => {
  if (!v) return '-';
  const short = v.length > 18 ? v.substring(0, 10) + '...' + v.slice(-4) : v;
  return <Tooltip title={v}><span style={{ fontFamily: 'monospace', fontSize: 12, color: '#1677ff', cursor: 'pointer' }}>{short}</span></Tooltip>;
};

const renderPeople = (names: string[] | undefined, dotColor: string, label: string) => {
  if (!names || names.length === 0) {
    return <span style={{ color: '#bbb', fontSize: 12 }}>—</span>;
  }
  return (
    <span style={{ fontSize: 12, lineHeight: 1.8 }}>
      {names.map((n, i) => (
        <span key={n}>
          {i > 0 && <span style={{ color: '#ccc' }}>, </span>}
          <span style={{ color: dotColor, fontWeight: 600 }}>{n}</span>
        </span>
      ))}
    </span>
  );
};

const renderActs = (sections: string[] | undefined) => {
  if (!sections || sections.length === 0) {
    return <span style={{ color: '#bbb', fontSize: 12 }}>—</span>;
  }
  const first = sections[0];
  const rest = sections.length - 1;
  return (
    <span>
      <span style={{ fontFamily: 'monospace', fontSize: 12, background: '#f5f5f5', padding: '1px 6px', borderRadius: 3, color: '#555' }}>{first}</span>
      {rest > 0 && <Tag style={{ fontSize: 10, marginLeft: 4, lineHeight: '16px', padding: '0 4px', border: 'none', background: '#f0f0f0', color: '#888' }}>+{rest}</Tag>}
    </span>
  );
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

  const cellStyle: React.CSSProperties = { padding: '8px 12px', fontSize: 13, lineHeight: 1.5 };

  const columns = [
    {
      title: 'Crime No', dataIndex: 'crimeNo', key: 'crimeNo', width: 150,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (v: string) => fmtCrimeNo(v),
    },
    {
      title: 'Date', dataIndex: 'crimeRegisteredDate', key: 'date', width: 110,
      onCell: () => ({ style: { ...cellStyle, textAlign: 'right' as const } }),
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12, color: '#888' }}>{fmtDate(v)}</span>,
    },
    {
      title: 'Location', key: 'location', width: 180,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (_: any, r: CaseSearchResult) => (
        <div style={{ lineHeight: 1.4 }}>
          <div style={{ fontSize: 13, color: '#333' }}>{r.districtName || <span style={{ color: '#bbb' }}>—</span>}</div>
          <div style={{ fontSize: 11, color: '#999' }}>{r.policeStationName || ''}</div>
        </div>
      ),
    },
    {
      title: 'Crime Head', dataIndex: 'crimeHeadName', key: 'crimeHead', width: 150,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (v: string) => v
        ? <span style={{ color: '#333', fontSize: 13 }}>{v}</span>
        : <span style={{ color: '#bbb' }}>—</span>,
    },
    {
      title: 'Status', dataIndex: 'statusName', key: 'status', width: 130,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (v: string) => {
        if (!v) return <span style={{ color: '#bbb' }}>—</span>;
        return <span style={{ ...statusStyle(v), padding: '2px 10px', borderRadius: 10, fontSize: 12, fontWeight: 600, display: 'inline-block' }}>{v}</span>;
      },
    },
    {
      title: 'People', key: 'people', width: 200,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (_: any, r: CaseSearchResult) => (
        <div style={{ lineHeight: 1.7 }}>
          {r.accusedNames?.length > 0 && (
            <div><span style={{ color: '#cf1322', fontSize: 11, marginRight: 4 }}>A:</span>{renderPeople(r.accusedNames, '#cf1322', 'A')}</div>
          )}
          {r.victimNames?.length > 0 && (
            <div><span style={{ color: '#389e0d', fontSize: 11, marginRight: 4 }}>V:</span>{renderPeople(r.victimNames, '#389e0d', 'V')}</div>
          )}
          {(!r.accusedNames?.length && !r.victimNames?.length) && <span style={{ color: '#bbb', fontSize: 12 }}>—</span>}
        </div>
      ),
    },
    {
      title: 'Acts', key: 'acts', width: 110,
      onCell: () => ({ style: { ...cellStyle } }),
      render: (_: any, r: CaseSearchResult) => renderActs(r.actSections),
    },
    {
      title: '', key: 'action', width: 48,
      onCell: () => ({ style: { ...cellStyle, textAlign: 'center' as const } }),
      render: (_: any, r: CaseSearchResult) => (
        <Tooltip title={r.briefFacts ? `Preview: ${r.briefFacts.substring(0, 120)}...` : 'View Details'}>
          <Button type="text" size="small" icon={<EyeOutlined />} onClick={() => openDrawer(r)}
            style={{ color: '#999' }} />
        </Tooltip>
      ),
    },
  ];

  const filtersActive = crimeNo || briefFacts || selectedDistrict !== undefined || selectedStatus !== undefined || selectedCrimeHead !== undefined || dateRange?.[0];

  return (
    <div style={{ padding: 16 }}>
      <Card style={{ marginBottom: 16 }} size="small" styles={{ body: { padding: 16 } }}>
        <Row gutter={[8, 8]}>
          <Col xs={24} sm={12} md={6} lg={4}>
            <Input placeholder="Crime No" value={crimeNo} onChange={e => setCrimeNo(e.target.value)}
              prefix={<SearchOutlined />} allowClear size="small" />
          </Col>
          <Col xs={24} sm={12} md={6} lg={4}>
            <Input placeholder="Brief Facts" value={briefFacts} onChange={e => setBriefFacts(e.target.value)}
              prefix={<SearchOutlined />} allowClear size="small" />
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Select placeholder="District" value={selectedDistrict} onChange={v => setSelectedDistrict(v)}
              allowClear style={{ width: '100%' }} showSearch optionFilterProp="children" size="small">
              {districts.map(d => <Select.Option key={d.districtId} value={d.districtId}>{d.districtName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={12} sm={8} md={4} lg={3}>
            <Select placeholder="Status" value={selectedStatus} onChange={v => setSelectedStatus(v)}
              allowClear style={{ width: '100%' }} size="small">
              {statuses.map(s => <Select.Option key={s.caseStatusId} value={s.caseStatusId}>{s.caseStatusName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={24} sm={8} md={5} lg={4}>
            <Select placeholder="Crime Head" value={selectedCrimeHead} onChange={v => setSelectedCrimeHead(v)}
              allowClear style={{ width: '100%' }} showSearch optionFilterProp="children" size="small">
              {crimeHeads.map(c => <Select.Option key={c.crimeHeadId} value={c.crimeHeadId}>{c.crimeGroupName}</Select.Option>)}
            </Select>
          </Col>
          <Col xs={24} sm={12} md={8} lg={6}>
            <DatePicker.RangePicker
              value={dateRange as any}
              onChange={dates => setDateRange(dates as any)}
              style={{ width: '100%' }}
              size="small"
              placeholder={['Start Date', 'End Date']}
            />
          </Col>
        </Row>
        <div style={{ marginTop: 10, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Button type="primary" icon={<SearchOutlined />} onClick={() => doSearch(0)} loading={loading}
            size="small">Search</Button>
          {filtersActive && (
            <Button type="link" onClick={clearFilters} size="small"
              style={{ color: '#999', padding: 0 }}>Clear Filters</Button>
          )}
        </div>
      </Card>

      <Card
        size="small"
        styles={{ body: { padding: 0 } }}
        title={<span style={{ fontSize: 13, color: '#666' }}><FilterOutlined style={{ marginRight: 6 }} />{total} case{total !== 1 ? 's' : ''} found</span>}
        extra={
          results.length > 0 ? (
            <Button icon={<DownloadOutlined />} size="small" onClick={() => exportCSV(results)} type="text" style={{ color: '#666' }}>
              Export CSV
            </Button>
          ) : null
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
            showTotal: (t) => `${t} cases`,
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
                <span style={{ fontFamily: 'monospace' }}>{drawerCase.crimeNo}</span>
              </Descriptions.Item>
              <Descriptions.Item label="Case No">{drawerCase.caseNo || '-'}</Descriptions.Item>
              <Descriptions.Item label="Registered Date">
                {fmtDate(drawerCase.crimeRegisteredDate)}
              </Descriptions.Item>
              <Descriptions.Item label="District">{drawerCase.districtName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Police Station">{drawerCase.policeStationName || '-'}</Descriptions.Item>
              <Descriptions.Item label="Crime Head">
                <span style={{ color: '#333' }}>{drawerCase.crimeHeadName || '-'}</span>
              </Descriptions.Item>
              <Descriptions.Item label="Status">
                <span style={{ ...statusStyle(drawerCase.statusName), padding: '2px 10px', borderRadius: 10, fontSize: 12, fontWeight: 600, display: 'inline-block' }}>
                  {drawerCase.statusName || '-'}
                </span>
              </Descriptions.Item>
            </Descriptions>

            <Divider orientation="left" orientationMargin={0}>Brief Facts</Divider>
            <p style={{ fontSize: 13, lineHeight: 1.6, whiteSpace: 'pre-wrap', background: '#fafafa', padding: 12, borderRadius: 6 }}>
              {drawerCase.briefFacts || 'No facts recorded'}
            </p>

            {drawerCase.accusedNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Accused</Divider>
                <div style={{ fontSize: 13, color: '#cf1322', lineHeight: 1.8 }}>{drawerCase.accusedNames.join(', ')}</div>
              </>
            )}

            {drawerCase.victimNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Victims</Divider>
                <div style={{ fontSize: 13, color: '#389e0d', lineHeight: 1.8 }}>{drawerCase.victimNames.join(', ')}</div>
              </>
            )}

            {drawerCase.complainantNames?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Complainants</Divider>
                <div style={{ fontSize: 13, color: '#d46b08', lineHeight: 1.8 }}>{drawerCase.complainantNames.join(', ')}</div>
              </>
            )}

            {drawerCase.actSections?.length > 0 && (
              <>
                <Divider orientation="left" orientationMargin={0}>Act Sections</Divider>
                <div>{drawerCase.actSections.map(a => <Tag key={a} style={{ fontFamily: 'monospace', background: '#f5f5f5', border: 'none', color: '#555' }}>{a}</Tag>)}</div>
              </>
            )}
          </>
        )}
      </Drawer>
    </div>
  );
};
