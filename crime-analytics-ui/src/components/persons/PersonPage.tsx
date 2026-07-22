import React, { useState, useEffect, useCallback } from 'react';
import {
  Card,
  Table,
  Tag,
  Button,
  Input,
  Select,
  Space,
  Typography,
  Modal,
  Form,
  DatePicker,
  Switch,
  Row,
  Col,
  Statistic,
  message,
  Popconfirm,
  Empty,
  Spin,
} from 'antd';
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  WarningOutlined,
  UserOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { personApi, Person } from '../../api/personApi';
import { PageResponse } from '../../types/case';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;

const personTypeColors: Record<string, string> = {
  SUSPECT: 'red',
  VICTIM: 'orange',
  WITNESS: 'blue',
  ARRESTED: 'purple',
  CONVICTED: 'magenta',
};

const getRiskLevel = (score?: number): { label: string; color: string } => {
  if (score === undefined || score === null) return { label: 'N/A', color: '#d9d9d9' };
  if (score >= 9) return { label: 'CRITICAL', color: '#ff4d4f' };
  if (score >= 7) return { label: 'HIGH', color: '#fa8c16' };
  if (score >= 4) return { label: 'MEDIUM', color: '#faad14' };
  return { label: 'LOW', color: '#52c41a' };
};

const statCardStyle = (gradient: string): React.CSSProperties => ({
  background: gradient,
  borderRadius: 12,
  boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
  transition: 'transform 0.2s',
  cursor: 'default',
});

const cardStyle: React.CSSProperties = {
  borderRadius: 12,
  boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
};

const PersonPage: React.FC = () => {
  const [persons, setPersons] = useState<Person[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(10);

  const [name, setName] = useState('');
  const [personTypeFilter, setPersonTypeFilter] = useState<string | undefined>(undefined);

  const [modalVisible, setModalVisible] = useState(false);
  const [editingPerson, setEditingPerson] = useState<Person | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();

  const [stats, setStats] = useState({
    total: 0,
    suspects: 0,
    knownOffenders: 0,
    avgRisk: 0,
  });

  const fetchPersons = useCallback(async (page: number = 1) => {
    setLoading(true);
    try {
      const params: Record<string, any> = {
        page: page - 1,
        size: pageSize,
      };
      if (name) params.name = name;
      if (personTypeFilter) params.personType = personTypeFilter;

      const response: PageResponse<Person> = await personApi.search(params);
      setPersons(response.content);
      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setCurrentPage(page);

      const allPersons = response.content;
      const suspects = allPersons.filter((p) => p.personType === 'SUSPECT').length;
      const knownOffenders = allPersons.filter((p) => p.isKnownOffender).length;
      const riskScores = allPersons.filter((p) => p.riskScore !== undefined && p.riskScore !== null);
      const avgRisk =
        riskScores.length > 0
          ? riskScores.reduce((sum, p) => sum + (p.riskScore || 0), 0) / riskScores.length
          : 0;

      setStats({
        total: response.totalElements,
        suspects,
        knownOffenders,
        avgRisk: Math.round(avgRisk * 10) / 10,
      });
    } catch {
      message.error('Failed to load persons');
    } finally {
      setLoading(false);
    }
  }, [name, personTypeFilter, pageSize]);

  useEffect(() => {
    fetchPersons(1);
  }, []);

  const handleSearch = () => {
    fetchPersons(1);
  };

  const handleReset = () => {
    setName('');
    setPersonTypeFilter(undefined);
    setTimeout(() => fetchPersons(1), 0);
  };

  const handleTableChange = (pagination: any) => {
    fetchPersons(pagination.current);
  };

  const handleCreate = () => {
    setEditingPerson(null);
    form.resetFields();
    form.setFieldsValue({
      isKnownOffender: false,
      convictionCount: 0,
      riskScore: 0,
      personType: 'SUSPECT',
    });
    setModalVisible(true);
  };

  const handleEdit = (person: Person) => {
    setEditingPerson(person);
    form.setFieldsValue({
      firstName: person.firstName,
      lastName: person.lastName,
      personType: person.personType,
      gender: person.gender,
      dateOfBirth: person.dateOfBirth ? dayjs(person.dateOfBirth) : null,
      nationality: person.nationality,
      isKnownOffender: person.isKnownOffender,
      convictionCount: person.convictionCount,
      riskScore: person.riskScore,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: string) => {
    try {
      await personApi.delete(id);
      message.success('Person deleted');
      fetchPersons(currentPage);
    } catch {
      message.error('Failed to delete person');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);

      const payload: Partial<Person> = {
        firstName: values.firstName,
        lastName: values.lastName,
        personType: values.personType,
        gender: values.gender,
        dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : undefined,
        nationality: values.nationality,
        isKnownOffender: values.isKnownOffender,
        convictionCount: values.convictionCount || 0,
        riskScore: values.riskScore,
      };

      if (editingPerson) {
        await personApi.update(editingPerson.id, payload);
        message.success('Person updated');
      } else {
        await personApi.create(payload);
        message.success('Person created');
      }

      setModalVisible(false);
      form.resetFields();
      fetchPersons(editingPerson ? currentPage : 1);
    } catch {
      message.error('Operation failed');
    } finally {
      setSubmitting(false);
    }
  };

  const columns = [
    {
      title: 'Name',
      key: 'name',
      render: (_: any, record: Person) => (
        <Text strong>{record.firstName} {record.lastName}</Text>
      ),
    },
    {
      title: 'Type',
      dataIndex: 'personType',
      key: 'personType',
      render: (type: string) => <Tag color={personTypeColors[type]}>{type}</Tag>,
    },
    {
      title: 'Gender',
      dataIndex: 'gender',
      key: 'gender',
      render: (g: string) => g || '-',
    },
    {
      title: 'DOB',
      dataIndex: 'dateOfBirth',
      key: 'dateOfBirth',
      render: (d: string) => d || '-',
    },
    {
      title: 'Nationality',
      dataIndex: 'nationality',
      key: 'nationality',
      render: (n: string) => n || '-',
    },
    {
      title: 'Risk Score',
      dataIndex: 'riskScore',
      key: 'riskScore',
      render: (score: number) => {
        const risk = getRiskLevel(score);
        return <Tag color={risk.color}>{score !== undefined && score !== null ? `${score} - ${risk.label}` : 'N/A'}</Tag>;
      },
    },
    {
      title: 'Known Offender',
      dataIndex: 'isKnownOffender',
      key: 'isKnownOffender',
      render: (v: boolean) => <Tag color={v ? 'red' : 'green'}>{v ? 'Yes' : 'No'}</Tag>,
    },
    {
      title: 'Convictions',
      dataIndex: 'convictionCount',
      key: 'convictionCount',
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Person) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
            style={{ borderRadius: 8 }}
          >
            Edit
          </Button>
          <Popconfirm
            title="Delete this person?"
            onConfirm={() => handleDelete(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="link" danger icon={<DeleteOutlined />} style={{ borderRadius: 8 }}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24, background: '#f5f7fa', minHeight: '100vh' }}>
      <Title level={3} style={{ marginBottom: 24 }}>Person Management</Title>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card
            style={statCardStyle('linear-gradient(135deg, #667eea 0%, #764ba2 100%)')}
            bordered={false}
            bodyStyle={{ padding: '20px 24px' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(-2px)'; }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(0)'; }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.8)' }}>Total Persons</span>}
              value={stats.total}
              valueStyle={{ color: '#fff', fontSize: 28 }}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            style={statCardStyle('linear-gradient(135deg, #f093fb 0%, #f5576c 100%)')}
            bordered={false}
            bodyStyle={{ padding: '20px 24px' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(-2px)'; }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(0)'; }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.8)' }}>Suspects</span>}
              value={stats.suspects}
              valueStyle={{ color: '#fff', fontSize: 28 }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            style={statCardStyle('linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)')}
            bordered={false}
            bodyStyle={{ padding: '20px 24px' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(-2px)'; }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(0)'; }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.8)' }}>Known Offenders</span>}
              value={stats.knownOffenders}
              valueStyle={{ color: '#fff', fontSize: 28 }}
              prefix={<SafetyOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            style={statCardStyle('linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)')}
            bordered={false}
            bodyStyle={{ padding: '20px 24px' }}
            onMouseEnter={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(-2px)'; }}
            onMouseLeave={(e) => { (e.currentTarget as HTMLElement).style.transform = 'translateY(0)'; }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.8)' }}>Avg Risk Score</span>}
              value={stats.avgRisk}
              valueStyle={{ color: '#fff', fontSize: 28 }}
              prefix={<WarningOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Card
        style={{
          ...cardStyle,
          marginBottom: 16,
          background: 'linear-gradient(135deg, #fafbfc 0%, #f0f2f5 100%)',
        }}
        bordered={false}
      >
        <Space wrap>
          <Input
            placeholder="Search by name"
            prefix={<SearchOutlined />}
            value={name}
            onChange={(e) => setName(e.target.value)}
            onPressEnter={handleSearch}
            style={{ width: 220, borderRadius: 8 }}
            allowClear
          />
          <Select
            placeholder="Person Type"
            value={personTypeFilter}
            onChange={setPersonTypeFilter}
            style={{ width: 160 }}
            allowClear
          >
            <Option value="SUSPECT">Suspect</Option>
            <Option value="VICTIM">Victim</Option>
            <Option value="WITNESS">Witness</Option>
            <Option value="ARRESTED">Arrested</Option>
            <Option value="CONVICTED">Convicted</Option>
          </Select>
          <Button
            type="primary"
            icon={<SearchOutlined />}
            onClick={handleSearch}
            style={{ borderRadius: 8 }}
          >
            Search
          </Button>
          <Button
            icon={<ReloadOutlined />}
            onClick={handleReset}
            style={{ borderRadius: 8 }}
          >
            Reset
          </Button>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
            style={{ borderRadius: 8, marginLeft: 'auto' }}
          >
            New Person
          </Button>
        </Space>
      </Card>

      <Card style={cardStyle} bordered={false}>
        <Spin spinning={loading}>
          <Table
            columns={columns}
            dataSource={persons}
            rowKey="id"
            pagination={{
              current: currentPage,
              pageSize,
              total: totalElements,
              showSizeChanger: false,
              showTotal: (total) => `${total} persons`,
            }}
            onChange={handleTableChange}
            rowHoverable
            locale={{ emptyText: <Empty description="No persons found" /> }}
          />
        </Spin>
      </Card>

      <Modal
        title={editingPerson ? 'Edit Person' : 'New Person'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        confirmLoading={submitting}
        width={640}
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="firstName"
                label="First Name"
                rules={[{ required: true, message: 'First name is required' }]}
              >
                <Input placeholder="Enter first name" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="lastName"
                label="Last Name"
                rules={[{ required: true, message: 'Last name is required' }]}
              >
                <Input placeholder="Enter last name" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="personType"
                label="Person Type"
                rules={[{ required: true, message: 'Person type is required' }]}
              >
                <Select placeholder="Select type">
                  <Option value="SUSPECT">Suspect</Option>
                  <Option value="VICTIM">Victim</Option>
                  <Option value="WITNESS">Witness</Option>
                  <Option value="ARRESTED">Arrested</Option>
                  <Option value="CONVICTED">Convicted</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="gender" label="Gender">
                <Select placeholder="Select gender" allowClear>
                  <Option value="Male">Male</Option>
                  <Option value="Female">Female</Option>
                  <Option value="Other">Other</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="dateOfBirth" label="Date of Birth">
                <DatePicker style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="nationality" label="Nationality">
                <Input placeholder="Enter nationality" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="isKnownOffender" label="Known Offender" valuePropName="checked">
                <Switch />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="convictionCount" label="Conviction Count">
                <Input type="number" min={0} placeholder="0" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="riskScore" label="Risk Score">
                <Input type="number" min={0} max={10} step={0.1} placeholder="0-10" />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default PersonPage;
