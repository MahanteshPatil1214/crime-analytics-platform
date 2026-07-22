import React, { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Select, Space, Typography, Modal, Form, DatePicker, message, Popconfirm, Descriptions } from 'antd';
import { SearchOutlined, ReloadOutlined, EyeOutlined, FilterOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { caseApi } from '../../api/caseApi';
import { lookupApi } from '../../api/lookupApi';
import { CaseSearchResult, CaseMaster } from '../../types/case';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';

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
  const [categories, setCategories] = useState<{ caseCategoryId: number; lookupValue: string }[]>([]);
  const [gravityOffences, setGravityOffences] = useState<{ gravityOffenceId: number; lookupValue: string }[]>([]);
  const [courts, setCourts] = useState<{ courtId: number; courtName: string }[]>([]);
  const [units, setUnits] = useState<{ unitId: number; unitName: string }[]>([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingCase, setEditingCase] = useState<CaseSearchResult | null>(null);
  const [form] = Form.useForm();
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    lookupApi.getStatuses().then(setStatuses).catch(() => {});
    lookupApi.getCrimeHeads().then(setCrimeHeads).catch(() => {});
    lookupApi.getDistricts().then(setDistricts).catch(() => {});
    lookupApi.getCategories().then(setCategories).catch(() => {});
    lookupApi.getGravityOffences().then(setGravityOffences).catch(() => {});
    lookupApi.getCourts().then(setCourts).catch(() => {});
    lookupApi.getUnits().then(setUnits).catch(() => {});
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

  const openCreateModal = () => {
    setEditingCase(null);
    form.resetFields();
    setModalOpen(true);
  };

  const openEditModal = async (record: CaseSearchResult) => {
    try {
      const detail = await caseApi.getById(record.caseMasterId);
      const c = detail.case;
      form.setFieldsValue({
        crimeNo: c.crimeNo,
        caseNo: c.caseNo,
        crimeRegisteredDate: c.crimeRegisteredDate ? dayjs(c.crimeRegisteredDate) : null,
        policeStationId: c.policeStationId,
        caseCategoryId: c.caseCategoryId,
        gravityOffenceId: c.gravityOffenceId,
        crimeMajorHeadId: c.crimeMajorHeadId,
        crimeMinorHeadId: c.crimeMinorHeadId,
        caseStatusId: c.caseStatusId,
        courtId: c.courtId,
        incidentFromDate: c.incidentFromDate ? dayjs(c.incidentFromDate) : null,
        incidentToDate: c.incidentToDate ? dayjs(c.incidentToDate) : null,
        briefFacts: c.briefFacts,
        latitude: c.latitude,
        longitude: c.longitude,
      });
      setEditingCase(record);
      setModalOpen(true);
    } catch {
      message.error('Failed to load case details');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      const payload: Partial<CaseMaster> = {
        crimeNo: values.crimeNo,
        caseNo: values.caseNo,
        crimeRegisteredDate: values.crimeRegisteredDate?.format('YYYY-MM-DD'),
        policeStationId: values.policeStationId || null,
        caseCategoryId: values.caseCategoryId || null,
        gravityOffenceId: values.gravityOffenceId || null,
        crimeMajorHeadId: values.crimeMajorHeadId || null,
        crimeMinorHeadId: values.crimeMinorHeadId || null,
        caseStatusId: values.caseStatusId || null,
        courtId: values.courtId || null,
        incidentFromDate: values.incidentFromDate?.format('YYYY-MM-DD') || null,
        incidentToDate: values.incidentToDate?.format('YYYY-MM-DD') || null,
        briefFacts: values.briefFacts || null,
        latitude: values.latitude || null,
        longitude: values.longitude || null,
      };

      if (editingCase) {
        await caseApi.update(editingCase.caseMasterId, payload);
        message.success('Case updated successfully');
      } else {
        await caseApi.create(payload);
        message.success('Case created successfully');
      }
      setModalOpen(false);
      form.resetFields();
      fetchCases(page);
    } catch (err: any) {
      if (err?.errorFields) return;
      message.error(editingCase ? 'Failed to update case' : 'Failed to create case');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await caseApi.delete(id);
      message.success('Case deleted');
      fetchCases(page);
    } catch {
      message.error('Failed to delete case');
    }
  };

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
      title: 'Actions',
      key: 'actions',
      width: 120,
      render: (_, r) => (
        <Space size={4}>
          <Button
            type="primary"
            shape="circle"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => navigate(`/incidents/${r.caseMasterId}`)}
            style={{ boxShadow: '0 2px 6px rgba(24,144,255,0.3)' }}
          />
          <Button
            shape="circle"
            size="small"
            icon={<EditOutlined />}
            onClick={() => openEditModal(r)}
            style={{ color: '#faad14', borderColor: '#faad14' }}
          />
          <Popconfirm
            title="Delete this case?"
            description="This action cannot be undone."
            onConfirm={() => handleDelete(r.caseMasterId)}
            okText="Delete"
            cancelText="Cancel"
            okButtonProps={{ danger: true }}
          >
            <Button
              shape="circle"
              size="small"
              icon={<DeleteOutlined />}
              danger
            />
          </Popconfirm>
        </Space>
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
          <div style={{ flex: 1 }} />
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal} style={{ borderRadius: 8, background: 'linear-gradient(135deg, #1890ff, #096dd9)', boxShadow: '0 2px 8px rgba(24,144,255,0.3)' }}>
            New Case
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
            rowHoverable
          />
        </div>
      </Card>

      {/* Create / Edit Modal */}
      <Modal
        title={editingCase ? `Edit Case — ${editingCase.crimeNo}` : 'Create New Case'}
        open={modalOpen}
        onCancel={() => { setModalOpen(false); form.resetFields(); }}
        onOk={handleSubmit}
        confirmLoading={submitting}
        okText={editingCase ? 'Update' : 'Create'}
        width={800}
        destroyOnClose
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
            <Form.Item name="crimeNo" label="Crime No" rules={[{ required: true, message: 'Required' }]}>
              <Input placeholder="e.g. 001/2026" />
            </Form.Item>
            <Form.Item name="caseNo" label="Case No">
              <Input placeholder="Case Number" />
            </Form.Item>
            <Form.Item name="crimeRegisteredDate" label="Registration Date">
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="caseStatusId" label="Status">
              <Select placeholder="Select Status" allowClear>
                {statuses.map((s) => (
                  <Select.Option key={s.caseStatusId} value={s.caseStatusId}>{s.caseStatusName}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="policeStationId" label="Police Station">
              <Select placeholder="Select Station" allowClear showSearch optionFilterProp="label">
                {units.map((u) => (
                  <Select.Option key={u.unitId} value={u.unitId} label={u.unitName}>{u.unitName}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="caseCategoryId" label="Category">
              <Select placeholder="Select Category" allowClear showSearch optionFilterProp="label">
                {categories.map((c) => (
                  <Select.Option key={c.caseCategoryId} value={c.caseCategoryId} label={c.lookupValue}>{c.lookupValue}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="crimeMajorHeadId" label="Crime Head">
              <Select placeholder="Select Crime Head" allowClear showSearch optionFilterProp="label">
                {crimeHeads.map((h) => (
                  <Select.Option key={h.crimeHeadId} value={h.crimeHeadId} label={h.crimeGroupName}>{h.crimeGroupName}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="gravityOffenceId" label="Gravity of Offence">
              <Select placeholder="Select Gravity" allowClear showSearch optionFilterProp="label">
                {gravityOffences.map((g) => (
                  <Select.Option key={g.gravityOffenceId} value={g.gravityOffenceId} label={g.lookupValue}>{g.lookupValue}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="courtId" label="Court">
              <Select placeholder="Select Court" allowClear showSearch optionFilterProp="label">
                {courts.map((c) => (
                  <Select.Option key={c.courtId} value={c.courtId} label={c.courtName}>{c.courtName}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="crimeMinorHeadId" label="Crime Minor Head">
              <Input placeholder="Minor Head ID" type="number" />
            </Form.Item>
            <Form.Item name="incidentFromDate" label="Incident From">
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="incidentToDate" label="Incident To">
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item name="latitude" label="Latitude">
              <Input placeholder="Latitude" type="number" />
            </Form.Item>
            <Form.Item name="longitude" label="Longitude">
              <Input placeholder="Longitude" type="number" />
            </Form.Item>
          </div>
          <Form.Item name="briefFacts" label="Brief Facts" style={{ gridColumn: '1 / -1' }}>
            <Input.TextArea rows={3} placeholder="Brief description of the case" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
