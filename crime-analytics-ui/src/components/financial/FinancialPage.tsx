import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Table, Tag, Input, Select, Spin, Space, Button, Typography } from 'antd';
import { DollarOutlined, WarningOutlined, SearchOutlined, ReloadOutlined, AlertOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { financialApi } from '../../api/financialApi';
import { FinancialTransaction } from '../../types/financial';
import type { ColumnsType } from 'antd/es/table';

const { Text } = Typography;

const TYPE_LABELS: Record<string, string> = {
  WIRE: 'Wire Transfer',
  CASH_DEPOSIT: 'Cash Deposit',
  CASH_WITHDRAWAL: 'Cash Withdrawal',
  CHECK: 'Check',
  CRYPTO: 'Crypto',
  TRANSFER: 'Transfer',
};

const TYPE_COLORS: Record<string, string> = {
  WIRE: '#1890ff',
  CASH_DEPOSIT: '#52c41a',
  CASH_WITHDRAWAL: '#faad14',
  CHECK: '#722ed1',
  CRYPTO: '#13c2c2',
  TRANSFER: '#2f54eb',
};

const getRiskTag = (score: number | null) => {
  if (score == null) return <Tag style={{ borderRadius: 12, padding: '1px 8px' }}>Unknown</Tag>;
  if (score >= 9.0) return <Tag color="red" style={{ borderRadius: 12, padding: '1px 8px', fontWeight: 600 }}>CRITICAL ({score.toFixed(1)})</Tag>;
  if (score >= 7.0) return <Tag color="orange" style={{ borderRadius: 12, padding: '1px 8px', fontWeight: 600 }}>HIGH ({score.toFixed(1)})</Tag>;
  if (score >= 4.0) return <Tag color="gold" style={{ borderRadius: 12, padding: '1px 8px', fontWeight: 600 }}>MEDIUM ({score.toFixed(1)})</Tag>;
  return <Tag color="green" style={{ borderRadius: 12, padding: '1px 8px' }}>LOW ({score.toFixed(1)})</Tag>;
};

const statCards = [
  { key: 'total', label: 'Total Transactions', icon: <DollarOutlined />, gradient: 'linear-gradient(135deg, #1890ff, #096dd9)', shadow: '0 4px 14px rgba(24,144,255,0.25)' },
  { key: 'amount', label: 'Total Amount', icon: <DollarOutlined />, gradient: 'linear-gradient(135deg, #52c41a, #389e0d)', shadow: '0 4px 14px rgba(82,196,26,0.25)' },
  { key: 'flagged', label: 'Flagged', icon: <WarningOutlined />, gradient: 'linear-gradient(135deg, #ff4d4f, #cf1322)', shadow: '0 4px 14px rgba(255,77,79,0.25)' },
  { key: 'rate', label: 'Flagged Rate', icon: <AlertOutlined />, gradient: 'linear-gradient(135deg, #faad14, #d48806)', shadow: '0 4px 14px rgba(250,173,20,0.25)' },
];

export const FinancialPage: React.FC = () => {
  const [transactions, setTransactions] = useState<FinancialTransaction[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [stats, setStats] = useState<Record<string, number> | null>(null);
  const [flaggedOnly, setFlaggedOnly] = useState(false);
  const [typeFilter, setTypeFilter] = useState<string | undefined>(undefined);
  const [accountFilter, setAccountFilter] = useState('');

  const fetchTransactions = async (p = page) => {
    setLoading(true);
    try {
      if (flaggedOnly) {
        const data = await financialApi.getFlagged(p, 10);
        setTransactions(data.content);
        setTotal(data.totalElements);
      } else {
        const params: Record<string, any> = { page: p, size: 10 };
        if (typeFilter) params.type = typeFilter;
        if (accountFilter) params.accountId = accountFilter;
        const data = await financialApi.search(params);
        setTransactions(data.content);
        setTotal(data.totalElements);
      }
    } catch (err) {
      console.error('Failed to load transactions:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions(0);
    financialApi.getStats().then(setStats).catch(() => {});
  }, []);

  useEffect(() => {
    setPage(0);
    fetchTransactions(0);
  }, [flaggedOnly, typeFilter]);

  const handleSearch = () => { setPage(0); fetchTransactions(0); };

  const typeBreakdown = transactions.reduce((acc, t) => {
    acc[t.transactionType] = (acc[t.transactionType] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);
  const barData = Object.entries(typeBreakdown).map(([name, count]) => ({ name: TYPE_LABELS[name] || name, count }));

  const getStatValue = (key: string) => {
    if (!stats) return 0;
    if (key === 'total') return stats.totalTransactions ?? 0;
    if (key === 'amount') return stats.totalAmount ?? 0;
    if (key === 'flagged') return stats.flaggedCount ?? 0;
    if (key === 'rate') return stats.totalTransactions > 0 ? Math.round((stats.flaggedCount / stats.totalTransactions) * 100) : 0;
    return 0;
  };

  const getStatPrefix = (key: string) => {
    if (key === 'amount') return '₹';
    return undefined;
  };

  const getStatSuffix = (key: string) => {
    if (key === 'rate') return '%';
    return undefined;
  };

  const columns: ColumnsType<FinancialTransaction> = [
    {
      title: 'Ref',
      dataIndex: 'transactionRef',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12, color: '#666' }}>{v}</span>,
    },
    {
      title: 'Type',
      dataIndex: 'transactionType',
      render: (v: string) => (
        <Tag color={TYPE_COLORS[v] || 'default'} style={{ borderRadius: 12, padding: '1px 10px', fontWeight: 500 }}>
          {TYPE_LABELS[v] || v}
        </Tag>
      ),
    },
    {
      title: 'From',
      dataIndex: 'senderAccountId',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12, color: '#666' }}>{v}</span>,
    },
    {
      title: 'To',
      dataIndex: 'recipientAccountId',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12, color: '#666' }}>{v}</span>,
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      render: (v: number, r) => (
        <span style={{ fontWeight: 600, color: r.isFlagged ? '#ff4d4f' : '#1a1a2e', fontSize: 14 }}>
          {r.currency} {v.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
        </span>
      ),
      sorter: (a, b) => a.amount - b.amount,
    },
    {
      title: 'Date',
      dataIndex: 'transactionDate',
      render: (v: string) => new Date(v).toLocaleDateString('en-IN'),
    },
    {
      title: 'Risk',
      dataIndex: 'riskScore',
      render: getRiskTag,
      sorter: (a, b) => (a.riskScore ?? 0) - (b.riskScore ?? 0),
    },
    {
      title: 'Case ID',
      dataIndex: 'relatedCaseId',
      render: (v: number | null) => v != null ? <span style={{ fontFamily: 'monospace', color: '#1890ff' }}>{v}</span> : '—',
    },
    {
      title: 'Flag',
      dataIndex: 'isFlagged',
      render: (v: boolean, r) => v
        ? <Tag color="red" style={{ borderRadius: 12, padding: '1px 8px' }} title={r.flagReason || ''}>Flagged</Tag>
        : <Tag style={{ borderRadius: 12, padding: '1px 8px' }}>Clean</Tag>,
    },
  ];

  return (
    <div style={{ padding: 24, background: '#f5f7fa', minHeight: '100vh' }}>
      {/* Stat Cards */}
      <Row gutter={[14, 14]} style={{ marginBottom: 20 }}>
        {statCards.map((card) => (
          <Col xs={12} sm={6} key={card.key}>
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
                e.currentTarget.style.boxShadow = card.shadow.replace('0.25', '0.4');
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
              <div style={{ color: '#fff', fontSize: 26, fontWeight: 700, lineHeight: 1 }}>
                {getStatPrefix(card.key)}{typeof getStatValue(card.key) === 'number' && card.key === 'amount'
                  ? (getStatValue(card.key) as number).toLocaleString('en-IN', { minimumFractionDigits: 2 })
                  : getStatValue(card.key)}{getStatSuffix(card.key)}
              </div>
            </div>
          </Col>
        ))}
      </Row>

      {/* Chart */}
      <Card
        variant="borderless"
        title={<span style={{ fontWeight: 600 }}>Transaction Type Breakdown</span>}
        style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)', marginBottom: 14 }}
        styles={{ body: { padding: '12px 16px' } }}
      >
        {barData.length > 0 ? (
          <ResponsiveContainer width="100%" height={210}>
            <BarChart data={barData} barCategoryGap="20%">
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="name" tick={{ fontSize: 12, fill: '#888' }} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11, fill: '#888' }} />
              <Tooltip
                contentStyle={{ borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.12)', border: 'none' }}
              />
              <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                {barData.map((_, i) => <Cell key={i} fill={['#1890ff', '#52c41a', '#faad14', '#722ed1', '#13c2c2', '#2f54eb'][i % 6]} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        ) : <Spin />}
      </Card>

      {/* Transactions Table */}
      <Card
        variant="borderless"
        title={<span style={{ fontWeight: 600 }}>Transactions</span>}
        style={{ borderRadius: 12, boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
        styles={{ body: { padding: 0 } }}
      >
        {/* Filter Bar */}
        <div
          style={{
            padding: '14px 20px',
            background: 'linear-gradient(135deg, #fafbfc 0%, #f0f2f5 100%)',
            borderBottom: '1px solid #f0f0f0',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            flexWrap: 'wrap',
          }}
        >
          <Input placeholder="Account ID" prefix={<SearchOutlined style={{ color: '#bbb' }} />} value={accountFilter} onChange={(e) => setAccountFilter(e.target.value)} onPressEnter={handleSearch} style={{ width: 160, borderRadius: 8 }} allowClear />
          <Select placeholder="Type" allowClear value={typeFilter} onChange={(v) => setTypeFilter(v)} style={{ width: 160 }}>
            <Select.Option value="WIRE">Wire Transfer</Select.Option>
            <Select.Option value="CASH_DEPOSIT">Cash Deposit</Select.Option>
            <Select.Option value="CASH_WITHDRAWAL">Cash Withdrawal</Select.Option>
            <Select.Option value="CRYPTO">Crypto</Select.Option>
            <Select.Option value="TRANSFER">Transfer</Select.Option>
          </Select>
          <Button type={flaggedOnly ? 'primary' : 'default'} danger={flaggedOnly} onClick={() => setFlaggedOnly(!flaggedOnly)} style={{ borderRadius: 8 }}>
            {flaggedOnly ? 'Flagged Only' : 'Show All'}
          </Button>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch} style={{ borderRadius: 8 }}>Search</Button>
          <Button icon={<ReloadOutlined />} onClick={() => { setAccountFilter(''); setTypeFilter(undefined); setFlaggedOnly(false); setPage(0); fetchTransactions(0); }} style={{ borderRadius: 8 }}>Reset</Button>
        </div>
        <div style={{ padding: '0 4px' }}>
          <Table
            rowKey="id"
            columns={columns}
            dataSource={transactions}
            loading={loading}
            pagination={{
              current: page + 1,
              pageSize: 10,
              total,
              showSizeChanger: false,
              showTotal: (t) => <Text type="secondary" style={{ fontSize: 13 }}>{t} transactions</Text>,
              onChange: (p) => { setPage(p - 1); fetchTransactions(p - 1); },
            }}
            rowHoverStyle={{ background: '#e6f7ff' }}
          />
        </div>
      </Card>
    </div>
  );
};
