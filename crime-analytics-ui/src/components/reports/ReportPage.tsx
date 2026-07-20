import React, { useState } from 'react';
import { Row, Col, Card, Form, Input, Select, Button, InputNumber, message, Space, Tabs } from 'antd';
import { FileTextOutlined, DownloadOutlined } from '@ant-design/icons';
import { reportApi } from '../../api/reportApi';

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
    <div style={{ padding: 24 }}>
      <Tabs
        defaultActiveKey="incident"
        items={[
          {
            key: 'incident',
            label: <span><FileTextOutlined /> Incident Report</span>,
            children: (
              <Card bordered={false} style={{ maxWidth: 700 }}>
                <Form form={incidentForm} layout="vertical" onFinish={handleIncidentReport}>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item name="firNumber" label="FIR Number" rules={[{ required: true }]}>
                        <Input placeholder="e.g. FIR-2026-001" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="title" label="Title" rules={[{ required: true }]}>
                        <Input placeholder="Incident title" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Form.Item name="description" label="Description">
                    <Input.TextArea rows={3} placeholder="Detailed description of the incident" />
                  </Form.Item>
                  <Row gutter={16}>
                    <Col span={8}>
                      <Form.Item name="severity" label="Severity" rules={[{ required: true }]}>
                        <Select placeholder="Select severity">
                          <Select.Option value="LOW">Low</Select.Option>
                          <Select.Option value="MEDIUM">Medium</Select.Option>
                          <Select.Option value="HIGH">High</Select.Option>
                          <Select.Option value="CRITICAL">Critical</Select.Option>
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item name="status" label="Status" rules={[{ required: true }]}>
                        <Select placeholder="Select status">
                          <Select.Option value="OPEN">Open</Select.Option>
                          <Select.Option value="UNDER_INVESTIGATION">Under Investigation</Select.Option>
                          <Select.Option value="CLOSED">Closed</Select.Option>
                          <Select.Option value="UNSOLVED">Unsolved</Select.Option>
                        </Select>
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item name="date" label="Date" rules={[{ required: true }]}>
                        <Input type="date" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item name="district" label="District" rules={[{ required: true }]}>
                        <Input placeholder="District name" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="address" label="Address">
                        <Input placeholder="Incident address" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" icon={<DownloadOutlined />} loading={generating} size="large">
                      Generate Incident Report PDF
                    </Button>
                  </Form.Item>
                </Form>
              </Card>
            ),
          },
          {
            key: 'profile',
            label: <span><FileTextOutlined /> Criminal Profile</span>,
            children: (
              <Card bordered={false} style={{ maxWidth: 700 }}>
                <Form form={profileForm} layout="vertical" onFinish={handleCriminalProfile}>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item name="personName" label="Person Name" rules={[{ required: true }]}>
                        <Input placeholder="Full name" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="personType" label="Person Type" rules={[{ required: true }]}>
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
                      <Form.Item name="convictionCount" label="Conviction Count" initialValue={0}>
                        <InputNumber min={0} max={100} style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="riskScore" label="Risk Score (0-10)" initialValue={0}>
                        <InputNumber min={0} max={10} step={0.1} style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Form.Item name="charges" label="Charges">
                    <Input.TextArea rows={3} placeholder="List of charges (e.g. Section 302 IPC - Murder)" />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" icon={<DownloadOutlined />} loading={generating} size="large">
                      Generate Criminal Profile PDF
                    </Button>
                  </Form.Item>
                </Form>
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
};
