import React, { useState } from 'react';
import { Card, Form, Input, Select, Button, InputNumber, message, Tabs, Typography, Row, Col } from 'antd';
import { FileTextOutlined, DownloadOutlined, UserOutlined, AuditOutlined } from '@ant-design/icons';
import { reportApi } from '../../api/reportApi';

const { Title, Text } = Typography;

const downloadBlob = (blob: Blob, filename: string) => {
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
};

export const ReportPage: React.FC = () => {
  const [incidentForm] = Form.useForm();
  const [profileForm] = Form.useForm();
  const [generating, setGenerating] = useState(false);

  const handleIncidentReport = async (values: any) => {
    setGenerating(true);
    try {
      const blob = await reportApi.generateIncidentReport(values);
      downloadBlob(blob, `Incident_Report_${values.firNumber}.pdf`);
      message.success('Incident report generated successfully');
    } catch (err) {
      message.error('Failed to generate report');
    } finally {
      setGenerating(false);
    }
  };

  const handleCriminalProfile = async (values: any) => {
    setGenerating(true);
    try {
      const blob = await reportApi.generateCriminalProfile(values);
      downloadBlob(blob, `Criminal_Profile_${values.personName.replace(/\s+/g, '_')}.pdf`);
      message.success('Criminal profile generated successfully');
    } catch (err) {
      message.error('Failed to generate profile');
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div style={{ padding: 24, background: '#f5f7fa', minHeight: '100vh' }}>
      <Tabs
        defaultActiveKey="incident"
        style={{ background: 'transparent' }}
        items={[
          {
            key: 'incident',
            label: <span style={{ fontWeight: 500 }}><FileTextOutlined /> Incident Report</span>,
            children: (
              <div style={{ maxWidth: 760 }}>
                {/* Header Banner */}
                <div
                  style={{
                    background: 'linear-gradient(135deg, #1890ff, #096dd9)',
                    borderRadius: '12px 12px 0 0',
                    padding: '24px 28px',
                    marginBottom: 0,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <div style={{
                      width: 44,
                      height: 44,
                      borderRadius: 10,
                      background: 'rgba(255,255,255,0.2)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}>
                      <FileTextOutlined style={{ fontSize: 22, color: '#fff' }} />
                    </div>
                    <div>
                      <Title level={4} style={{ margin: 0, color: '#fff' }}>Generate Incident Report</Title>
                      <Text style={{ color: 'rgba(255,255,255,0.75)', fontSize: 13 }}>
                        Fill in the FIR details to generate a formatted PDF report
                      </Text>
                    </div>
                  </div>
                </div>
                <Card
                  variant="borderless"
                  style={{ borderRadius: '0 0 12px 12px', boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
                  styles={{ body: { padding: '24px 28px' } }}
                >
                  <Form form={incidentForm} layout="vertical" onFinish={handleIncidentReport}>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item name="firNumber" label={<Text strong>FIR Number</Text>} rules={[{ required: true }]}>
                          <Input placeholder="e.g. FIR-2026-001" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item name="title" label={<Text strong>Title</Text>} rules={[{ required: true }]}>
                          <Input placeholder="Incident title" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                    </Row>
                    <Form.Item name="description" label={<Text strong>Description</Text>}>
                      <Input.TextArea rows={3} placeholder="Detailed description of the incident" style={{ borderRadius: 8 }} />
                    </Form.Item>
                    <Row gutter={16}>
                      <Col span={8}>
                        <Form.Item name="severity" label={<Text strong>Severity</Text>} rules={[{ required: true }]}>
                          <Select placeholder="Select severity" style={{ borderRadius: 8 }}>
                            <Select.Option value="LOW">Low</Select.Option>
                            <Select.Option value="MEDIUM">Medium</Select.Option>
                            <Select.Option value="HIGH">High</Select.Option>
                            <Select.Option value="CRITICAL">Critical</Select.Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item name="status" label={<Text strong>Status</Text>} rules={[{ required: true }]}>
                          <Select placeholder="Select status">
                            <Select.Option value="OPEN">Open</Select.Option>
                            <Select.Option value="UNDER_INVESTIGATION">Under Investigation</Select.Option>
                            <Select.Option value="CLOSED">Closed</Select.Option>
                            <Select.Option value="UNSOLVED">Unsolved</Select.Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item name="date" label={<Text strong>Date</Text>} rules={[{ required: true }]}>
                          <Input type="date" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                    </Row>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item name="district" label={<Text strong>District</Text>} rules={[{ required: true }]}>
                          <Input placeholder="District name" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item name="address" label={<Text strong>Address</Text>}>
                          <Input placeholder="Incident address" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                    </Row>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        icon={<DownloadOutlined />}
                        loading={generating}
                        size="large"
                        style={{
                          borderRadius: 8,
                          height: 44,
                          fontWeight: 600,
                          background: 'linear-gradient(135deg, #1890ff, #096dd9)',
                          boxShadow: '0 4px 12px rgba(24,144,255,0.3)',
                        }}
                      >
                        Generate Incident Report PDF
                      </Button>
                    </Form.Item>
                  </Form>
                </Card>
              </div>
            ),
          },
          {
            key: 'profile',
            label: <span style={{ fontWeight: 500 }}><UserOutlined /> Criminal Profile</span>,
            children: (
              <div style={{ maxWidth: 760 }}>
                {/* Header Banner */}
                <div
                  style={{
                    background: 'linear-gradient(135deg, #ff4d4f, #a8071a)',
                    borderRadius: '12px 12px 0 0',
                    padding: '24px 28px',
                    marginBottom: 0,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <div style={{
                      width: 44,
                      height: 44,
                      borderRadius: 10,
                      background: 'rgba(255,255,255,0.2)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}>
                      <AuditOutlined style={{ fontSize: 22, color: '#fff' }} />
                    </div>
                    <div>
                      <Title level={4} style={{ margin: 0, color: '#fff' }}>Generate Criminal Profile</Title>
                      <Text style={{ color: 'rgba(255,255,255,0.75)', fontSize: 13 }}>
                        Enter person details to generate a suspect profile report
                      </Text>
                    </div>
                  </div>
                </div>
                <Card
                  variant="borderless"
                  style={{ borderRadius: '0 0 12px 12px', boxShadow: '0 1px 6px rgba(0,0,0,0.06)' }}
                  styles={{ body: { padding: '24px 28px' } }}
                >
                  <Form form={profileForm} layout="vertical" onFinish={handleCriminalProfile}>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item name="personName" label={<Text strong>Person Name</Text>} rules={[{ required: true }]}>
                          <Input placeholder="Full name" style={{ borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item name="personType" label={<Text strong>Person Type</Text>} rules={[{ required: true }]}>
                          <Select placeholder="Select type">
                            <Select.Option value="SUSPECT">Suspect</Select.Option>
                            <Select.Option value="VICTIM">Victim</Select.Option>
                            <Select.Option value="WITNESS">Witness</Select.Option>
                            <Select.Option value="ARRESTED">Arrested</Select.Option>
                            <Select.Option value="CONVICTED">Convicted</Select.Option>
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item name="convictionCount" label={<Text strong>Conviction Count</Text>} initialValue={0}>
                          <InputNumber min={0} max={100} style={{ width: '100%', borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item name="riskScore" label={<Text strong>Risk Score (0-10)</Text>} initialValue={0}>
                          <InputNumber min={0} max={10} step={0.1} style={{ width: '100%', borderRadius: 8 }} />
                        </Form.Item>
                      </Col>
                    </Row>
                    <Form.Item name="charges" label={<Text strong>Charges</Text>}>
                      <Input.TextArea rows={3} placeholder="List of charges (e.g. Section 302 IPC - Murder)" style={{ borderRadius: 8 }} />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        icon={<DownloadOutlined />}
                        loading={generating}
                        size="large"
                        style={{
                          borderRadius: 8,
                          height: 44,
                          fontWeight: 600,
                          background: 'linear-gradient(135deg, #ff4d4f, #a8071a)',
                          boxShadow: '0 4px 12px rgba(255,77,79,0.3)',
                        }}
                      >
                        Generate Criminal Profile PDF
                      </Button>
                    </Form.Item>
                  </Form>
                </Card>
              </div>
            ),
          },
        ]}
      />
    </div>
  );
};
