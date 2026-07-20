import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Table, Tag, Input, Select, Spin, Space, Button } from 'antd';
import { DollarOutlined, WarningOutlined, SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { financialApi } from '../../api/financialApi';
import { FinancialTransaction } from '../../types/financial';
import type { ColumnsType } from 'antd/es/table';

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
  if (score == null) return <Tag>Unknown</Tag>;
  if (score >= 9.0) return <Tag color="red">CRITICAL ({score.toFixed(1)})</Tag>;
  if (score >= 7.0) return <Tag color="orange">HIGH ({score.toFixed(1)})</Tag>;
  if (score >= 4.0) return <Tag color="gold">MEDIUM ({score.toFixed(1)})</Tag>;
  return <Tag color="green">LOW ({score.toFixed(1)})</Tag>;
};

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

  const columns: ColumnsType<FinancialTransaction> = [
    {
      title: 'Ref',
      dataIndex: 'transactionRef',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{v}</span>,
    },
    {
      title: 'Type',
      dataIndex: 'transactionType',
      render: (v: string) => <Tag color={TYPE_COLORS[v] || 'default'}>{TYPE_LABELS[v] || v}</Tag>,
    },
    {
      title: 'From',
      dataIndex: 'senderAccountId',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{v}</span>,
    },
    {
      title: 'To',
      dataIndex: 'recipientAccountId',
      render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{v}</span>,
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      render: (v: number, r) => (
        <span style={{ fontWeight: 600, color: r.isFlagged ? '#ff4d4f' : '#333' }}>
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
      render: (v: number | null) => v != null ? <span style={{ fontFamily: 'monospace' }}>{v}</span> : '—',
    },
    {
      title: 'Flag',
      dataIndex: 'isFlagged',
      render: (v: boolean, r) => v
        ? <Tag color="red" title={r.flagReason || ''}>Flagged</Tag>
        : <Tag>Clean</Tag>,
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Total Transactions" value={stats?.totalTransactions ?? 0} prefix={<DollarOutlined />} valueStyle={{ color: '#1890ff' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Total Amount" value={stats?.totalAmount ?? 0} prefix="₹" precision={2} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Flagged Transactions" value={stats?.flaggedCount ?? 0} prefix={<WarningOutlined />} valueStyle={{ color: '#ff4d4f' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Flagged Rate" value={stats && stats.totalTransactions > 0 ? Math.round((stats.flaggedCount / stats.totalTransactions) * 100) : 0} suffix="%" valueStyle={{ color: '#faad14' }} />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col span={24}>
          <Card bordered={false} title="Transaction Type Breakdown">
            {barData.length > 0 ? (
              <ResponsiveContainer width="100%" height={200}>
                <BarChart data={barData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="name" tick={{ fontSize: 12 }} />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Bar dataKey="count" radius={[4, 4, 0, 0]}>
                    {barData.map((_, i) => <Cell key={i} fill={['#1890ff', '#52c41a', '#faad14', '#722ed1', '#13c2c2', '#2f54eb'][i % 6]} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : <Spin />}
          </Card>
        </Col>
      </Row>

      <Card
        title="Transactions"
        bordered={false}
        extra={
          <Space>
            <Input placeholder="Account ID" prefix={<SearchOutlined />} value={accountFilter} onChange={(e) => setAccountFilter(e.target.value)} onPressEnter={handleSearch} style={{ width: 160 }} />
            <Select placeholder="Type" allowClear value={typeFilter} onChange={(v) => setTypeFilter(v)} style={{ width: 160 }}>
              <Select.Option value="WIRE">Wire Transfer</Select.Option>
              <Select.Option value="CASH_DEPOSIT">Cash Deposit</Select.Option>
              <Select.Option value="CASH_WITHDRAWAL">Cash Withdrawal</Select.Option>
              <Select.Option value="CRYPTO">Crypto</Select.Option>
              <Select.Option value="TRANSFER">Transfer</Select.Option>
            </Select>
            <Button type={flaggedOnly ? 'primary' : 'default'} danger={flaggedOnly} onClick={() => setFlaggedOnly(!flaggedOnly)}>
              {flaggedOnly ? 'Flagged Only' : 'Show All'}
            </Button>
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>Search</Button>
            <Button icon={<ReloadOutlined />} onClick={() => { setAccountFilter(''); setTypeFilter(undefined); setFlaggedOnly(false); setPage(0); fetchTransactions(0); }}>Reset</Button>
          </Space>
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={transactions}
          loading={loading}
          pagination={{
            current: page + 1,
            pageSize: 10,
            total,
            onChange: (p) => { setPage(p - 1); fetchTransactions(p - 1); },
          }}
        />
      </Card>
    </div>
  );
};
