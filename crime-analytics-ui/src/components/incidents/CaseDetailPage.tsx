import React, { useEffect, useState } from 'react';
import { Row, Col, Card, Table, Tag, Descriptions, Spin, Empty, Statistic, Button, message } from 'antd';
import { ArrowLeftOutlined, UserOutlined, DollarOutlined, WarningOutlined, SafetyOutlined, FilePdfOutlined, DownloadOutlined, UploadOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { caseApi } from '../../api/caseApi';
import { lookupApi, Unit, Court as CourtType, CaseCategory, CrimeHead, Employee, GravityOffence } from '../../api/lookupApi';
import { evidenceApi, Evidence } from '../../api/evidenceApi';
import { financialApi } from '../../api/financialApi';
import { reportApi } from '../../api/reportApi';
import { CaseDetail, Involvement, ActSection, Arrest } from '../../types/case';
import { FinancialTransaction } from '../../types/financial';

const INVOLVEMENT_COLORS: Record<string, string> = {
  COMPLAINANT: '#1890ff',
  VICTIM: '#faad14',
  ACCUSED: '#ff4d4f',
};

export const CaseDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [detail, setDetail] = useState<CaseDetail | null>(null);
  const [involvements, setInvolvements] = useState<Involvement[]>([]);
  const [transactions, setTransactions] = useState<FinancialTransaction[]>([]);
  const [evidenceList, setEvidenceList] = useState<Evidence[]>([]);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);

  const [statusMap, setStatusMap] = useState<Record<number, string>>({});
  const [unitMap, setUnitMap] = useState<Record<number, string>>({});
  const [courtMap, setCourtMap] = useState<Record<number, string>>({});
  const [categoryMap, setCategoryMap] = useState<Record<number, string>>({});
  const [crimeHeadMap, setCrimeHeadMap] = useState<Record<number, string>>({});
  const [employeeMap, setEmployeeMap] = useState<Record<number, string>>({});
  const [gravityMap, setGravityMap] = useState<Record<number, string>>({});

  const GENDER_MAP: Record<number, string> = { 1: 'Male', 2: 'Female', 3: 'Transgender' };

  useEffect(() => {
    Promise.all([
      lookupApi.getStatuses().then((sts) => {
        const m: Record<number, string> = {};
        sts.forEach((s) => { m[s.caseStatusId] = s.caseStatusName; });
        setStatusMap(m);
      }).catch(() => {}),
      lookupApi.getUnits().then((us) => {
        const m: Record<number, string> = {};
        us.forEach((u) => { m[u.unitId] = u.unitName; });
        setUnitMap(m);
      }).catch(() => {}),
      lookupApi.getCourts().then((cs) => {
        const m: Record<number, string> = {};
        cs.forEach((c) => { m[c.courtId] = c.courtName; });
        setCourtMap(m);
      }).catch(() => {}),
      lookupApi.getCategories().then((cs) => {
        const m: Record<number, string> = {};
        cs.forEach((c) => { m[c.caseCategoryId] = c.lookupValue; });
        setCategoryMap(m);
      }).catch(() => {}),
      lookupApi.getCrimeHeads().then((ch) => {
        const m: Record<number, string> = {};
        ch.forEach((c) => { m[c.crimeHeadId] = c.crimeGroupName; });
        setCrimeHeadMap(m);
      }).catch(() => {}),
      lookupApi.getEmployees().then((es) => {
        const m: Record<number, string> = {};
        es.forEach((e) => { m[e.employeeId] = e.firstName; });
        setEmployeeMap(m);
      }).catch(() => {}),
      lookupApi.getGravityOffences().then((gs) => {
        const m: Record<number, string> = {};
        gs.forEach((g) => { m[g.gravityOffenceId] = g.lookupValue; });
        setGravityMap(m);
      }).catch(() => {}),
    ]);
  }, []);

  useEffect(() => {
    if (!id) return;
    const caseId = Number(id);
    const load = async () => {
      try {
        const [det, invs, txns, evs] = await Promise.all([
          caseApi.getById(caseId),
          caseApi.getInvolvements(caseId).catch(() => []),
          financialApi.getByCase(caseId).catch(() => []),
          evidenceApi.list(caseId).catch(() => []),
        ]);
        setDetail(det);
        setInvolvements(Array.isArray(invs) ? invs : []);
        setTransactions(Array.isArray(txns) ? txns : []);
        setEvidenceList(Array.isArray(evs) ? evs : []);
      } catch (err) {
        console.error('Failed to load case:', err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  if (loading) return <div style={{ padding: 24 }}><Spin size="large" /></div>;
  if (!detail) return <div style={{ padding: 24 }}><Empty description="Case not found" /></div>;

  const { case: c, actSections, arrests } = detail;
  const totalPersons = involvements.length;
  const accusedCount = involvements.filter((i) => i.type === 'ACCUSED').length;

  const statusName = c.caseStatusId != null ? (statusMap[c.caseStatusId] || '—') : '—';
  const unitName = c.policeStationId != null ? (unitMap[c.policeStationId] || '—') : '—';
  const courtName = c.courtId != null ? (courtMap[c.courtId] || '—') : '—';
  const categoryName = c.caseCategoryId != null ? (categoryMap[c.caseCategoryId] || '—') : '—';
  const crimeHeadName = c.crimeMajorHeadId != null ? (crimeHeadMap[c.crimeMajorHeadId] || '—') : '—';

  const handleDownloadFir = async () => {
    if (!c.caseMasterId) return;
    setGenerating(true);
    try {
      await reportApi.downloadFir(c.caseMasterId);
      message.success('FIR report downloaded');
    } catch (err) {
      console.error('Failed to generate report:', err);
      message.error('Failed to generate FIR report');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)}>Back</Button>
        <Button
          type="primary"
          icon={<FilePdfOutlined />}
          loading={generating}
          onClick={handleDownloadFir}
          style={{ background: '#8B0000', borderColor: '#8B0000' }}
        >
          Download FIR Report
        </Button>
      </div>

      <Card bordered={false} style={{ marginBottom: 16 }}>
        <Descriptions bordered column={2} title={`Crime No: ${c.crimeNo}`}>
          <Descriptions.Item label="Case No">{c.caseNo || '—'}</Descriptions.Item>
          <Descriptions.Item label="Status">
            <Tag color="blue">{statusName}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Registered Date">{c.crimeRegisteredDate || '—'}</Descriptions.Item>
          <Descriptions.Item label="Police Station">{unitName}</Descriptions.Item>
          <Descriptions.Item label="Court">{courtName}</Descriptions.Item>
          <Descriptions.Item label="Category">{categoryName}</Descriptions.Item>
          <Descriptions.Item label="Crime Head">{crimeHeadName}</Descriptions.Item>
          <Descriptions.Item label="Gravity">{c.gravityOffenceId != null ? (gravityMap[c.gravityOffenceId] || '—') : '—'}</Descriptions.Item>
          <Descriptions.Item label="Incident From">{c.incidentFromDate || '—'}</Descriptions.Item>
          <Descriptions.Item label="Incident To">{c.incidentToDate || '—'}</Descriptions.Item>
          {c.latitude != null && c.longitude != null && (
            <Descriptions.Item label="Coordinates" span={2}>{c.latitude}, {c.longitude}</Descriptions.Item>
          )}
          {c.briefFacts && <Descriptions.Item label="Brief Facts" span={2}>{c.briefFacts}</Descriptions.Item>}
        </Descriptions>
      </Card>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Total Persons" value={totalPersons} prefix={<UserOutlined />} valueStyle={{ color: '#1890ff' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Accused" value={accusedCount} prefix={<WarningOutlined />} valueStyle={{ color: '#ff4d4f' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Financial Transactions" value={transactions.length} prefix={<DollarOutlined />} valueStyle={{ color: '#722ed1' }} />
          </Card>
        </Col>
        <Col span={6}>
          <Card bordered={false}>
            <Statistic title="Act Sections" value={actSections.length} prefix={<SafetyOutlined />} valueStyle={{ color: '#52c41a' }} />
          </Card>
        </Col>
      </Row>

      {detail.complainants && detail.complainants.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title={<><UserOutlined /> Complainants</>} bordered={false}>
              <Table
                rowKey="complainantId"
                dataSource={detail.complainants}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Name', dataIndex: 'complainantName' },
                  { title: 'Age', dataIndex: 'ageYear', render: (v: number | null) => v ?? '—' },
                  { title: 'Gender', dataIndex: 'genderId', render: (v: number | null) => v != null ? (GENDER_MAP[v] || '—') : '—' },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      {detail.victims && detail.victims.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title="Victims" bordered={false}>
              <Table
                rowKey="victimMasterId"
                dataSource={detail.victims}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Name', dataIndex: 'victimName' },
                  { title: 'Age', dataIndex: 'ageYear', render: (v: number | null) => v ?? '—' },
                  { title: 'Gender', dataIndex: 'genderId', render: (v: number | null) => v != null ? (GENDER_MAP[v] || '—') : '—' },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      {detail.accused && detail.accused.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title={<><WarningOutlined /> Accused</>} bordered={false}>
              <Table
                rowKey="accusedMasterId"
                dataSource={detail.accused}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Name', dataIndex: 'accusedName' },
                  { title: 'Age', dataIndex: 'ageYear', render: (v: number | null) => v ?? '—' },
                  { title: 'Gender', dataIndex: 'genderId', render: (v: number | null) => v != null ? (GENDER_MAP[v] || '—') : '—' },
                  { title: 'Person ID', dataIndex: 'personId', render: (v: string | null) => v || '—' },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      {involvements.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title={<><UserOutlined /> All Involvements (Combined)</>} bordered={false}>
              <Table
                rowKey={(_, i) => String(i)}
                dataSource={involvements}
                pagination={false}
                size="small"
                columns={[
                  {
                    title: 'Type',
                    dataIndex: 'type',
                    render: (v: string) => <Tag color={INVOLVEMENT_COLORS[v] || 'default'}>{v}</Tag>,
                  },
                  { title: 'Name', dataIndex: 'name' },
                  { title: 'Age', dataIndex: 'age', render: (v: number | null) => v ?? '—' },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      {actSections.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title={<><SafetyOutlined /> Act Sections</>} bordered={false}>
              <Table
                rowKey={(_, i) => String(i)}
                dataSource={actSections}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Act Code', dataIndex: 'actCode', render: (v: string) => <span style={{ fontFamily: 'monospace' }}>{v}</span> },
                  { title: 'Section Code', dataIndex: 'sectionCode', render: (v: string) => <span style={{ fontFamily: 'monospace' }}>{v}</span> },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      {arrests.length > 0 && (
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={24}>
            <Card title="Arrests" bordered={false}>
              <Table
                rowKey="arrestSurrenderId"
                dataSource={arrests}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Date', dataIndex: 'arrestSurrenderDate', render: (v: string | null) => v || '—' },
                  { title: 'IO', dataIndex: 'ioId', render: (v: number | null) => v != null ? (employeeMap[v] || '—') : '—' },
                  { title: 'Court', dataIndex: 'courtId', render: (v: number | null) => v != null ? (courtMap[v] || '—') : '—' },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col span={24}>
          <Card title="Evidence" bordered={false}
            extra={
              <label style={{ cursor: 'pointer', color: '#1890ff', fontSize: 13 }}>
                <input type="file" hidden onChange={async (e) => {
                  const file = e.target.files?.[0];
                  if (!file || !id) return;
                  setUploading(true);
                  try {
                    const ev = await evidenceApi.upload(Number(id), file);
                    setEvidenceList(prev => [...prev, ev]);
                    message.success(`${file.name} uploaded`);
                  } catch { message.error('Upload failed'); }
                  finally { setUploading(false); e.target.value = ''; }
                }} />
                {uploading ? 'Uploading...' : '+ Upload File'}
              </label>
            }
          >
            {evidenceList.length === 0 ? (
              <Empty description="No evidence uploaded" image={Empty.PRESENTED_IMAGE_SIMPLE} />
            ) : (
              <Table
                rowKey="evidenceId"
                dataSource={evidenceList}
                pagination={false}
                size="small"
                columns={[
                  { title: 'File', dataIndex: 'originalName', render: (v: string) => <span style={{ fontWeight: 500 }}>{v}</span> },
                  { title: 'Type', dataIndex: 'fileType', render: (v: string) => v || '—' },
                  { title: 'Size', dataIndex: 'fileSize', render: (v: number) => {
                    if (!v) return '—';
                    const kb = v / 1024;
                    return kb > 1024 ? `${(kb / 1024).toFixed(1)} MB` : `${kb.toFixed(0)} KB`;
                  }},
                  { title: 'Description', dataIndex: 'description', render: (v: string) => v || '—' },
                  { title: 'Uploaded', dataIndex: 'uploadDate', render: (v: string) => v ? new Date(v).toLocaleDateString('en-IN') : '—' },
                  {
                    title: 'Action', key: 'action', width: 120,
                    render: (_: any, r: Evidence) => (
                      <>
                        <Button type="link" size="small" icon={<DownloadOutlined />}
                          href={evidenceApi.download(Number(id), r.evidenceId)} target="_blank">Download</Button>
                        <Button type="link" size="small" danger
                          onClick={async () => {
                            try {
                              await evidenceApi.delete(Number(id), r.evidenceId);
                              setEvidenceList(prev => prev.filter(e => e.evidenceId !== r.evidenceId));
                              message.success('Deleted');
                            } catch { message.error('Delete failed'); }
                          }}>Delete</Button>
                      </>
                    ),
                  },
                ]}
              />
            )}
          </Card>
        </Col>
      </Row>

      {transactions.length > 0 && (
        <Row gutter={[16, 16]}>
          <Col span={24}>
            <Card title={<><DollarOutlined /> Financial Transactions</>} bordered={false}>
              <Table
                rowKey="id"
                dataSource={transactions}
                pagination={false}
                size="small"
                columns={[
                  { title: 'Ref', dataIndex: 'transactionRef', render: (v: string) => <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{v}</span> },
                  { title: 'Type', dataIndex: 'transactionType', render: (v: string) => <Tag>{v}</Tag> },
                  { title: 'Amount', dataIndex: 'amount', render: (v: number, r) => <span style={{ fontWeight: 600, color: r.isFlagged ? '#ff4d4f' : '#333' }}>{r.currency} {Number(v).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span> },
                  { title: 'Date', dataIndex: 'transactionDate', render: (v: string) => new Date(v).toLocaleDateString('en-IN') },
                  { title: 'Flag', dataIndex: 'isFlagged', render: (v: boolean, r) => v ? <Tag color="red" title={r.flagReason || ''}>Flagged</Tag> : <Tag>Clean</Tag> },
                ]}
              />
            </Card>
          </Col>
        </Row>
      )}
    </div>
  );
};
