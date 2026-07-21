import React from 'react';
import { Row, Col, Typography, Button, Tag, Space, Statistic } from 'antd';
import {
  AlertOutlined,
  DashboardOutlined,
  DollarOutlined,
  FileTextOutlined,
  RobotOutlined,
  SearchOutlined,
  ApartmentOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  DatabaseOutlined,
  ThunderboltOutlined,
  CloudServerOutlined,
  ApiOutlined,
  UserSwitchOutlined,
  BankOutlined,
  FileSearchOutlined,
  LineChartOutlined,
  LockOutlined,
  BulbOutlined,
  PlayCircleOutlined,
  ArrowRightOutlined,
  RocketOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title, Paragraph, Text } = Typography;

const features = [
  {
    icon: <DashboardOutlined style={{ fontSize: 26 }} />,
    title: 'Analytics Dashboard',
    description: 'Real-time case statistics, district-wise charts, crime head distribution, and an interactive GIS hotspot map.',
    path: '/dashboard',
    gradient: 'linear-gradient(135deg, #1890ff, #096dd9)',
    bg: 'linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%)',
    accent: '#1890ff',
  },
  {
    icon: <AlertOutlined style={{ fontSize: 26 }} />,
    title: 'Incident Management',
    description: 'Browse, search, and drill into FIR records — complainants, victims, accused, arrests, and linked transactions.',
    path: '/incidents',
    gradient: 'linear-gradient(135deg, #ff4d4f, #cf1322)',
    bg: 'linear-gradient(135deg, #fff1f0 0%, #ffccc7 100%)',
    accent: '#ff4d4f',
  },
  {
    icon: <DollarOutlined style={{ fontSize: 26 }} />,
    title: 'Financial Tracking',
    description: 'Track wire transfers, cash, crypto. Flag suspicious activity with automated risk scoring.',
    path: '/financial',
    gradient: 'linear-gradient(135deg, #faad14, #d48806)',
    bg: 'linear-gradient(135deg, #fffbe6 0%, #ffe58f 100%)',
    accent: '#faad14',
  },
  {
    icon: <FileTextOutlined style={{ fontSize: 26 }} />,
    title: 'Report Generation',
    description: 'Generate government-standard typewriter-style PDF reports for FIRs and criminal profiles.',
    path: '/reports',
    gradient: 'linear-gradient(135deg, #722ed1, #531dab)',
    bg: 'linear-gradient(135deg, #f9f0ff 0%, #efdbff 100%)',
    accent: '#722ed1',
  },
  {
    icon: <ApartmentOutlined style={{ fontSize: 26 }} />,
    title: 'Criminal Network',
    description: 'Interactive graph visualization of relationships between persons and cases using Neo4j.',
    path: '/graph',
    gradient: 'linear-gradient(135deg, #52c41a, #389e0d)',
    bg: 'linear-gradient(135deg, #f6ffed 0%, #b7eb8f 100%)',
    accent: '#52c41a',
  },
  {
    icon: <SearchOutlined style={{ fontSize: 26 }} />,
    title: 'Advanced Search',
    description: 'Full-text Elasticsearch search across cases, persons, and transactions with fuzzy matching.',
    path: '/search',
    gradient: 'linear-gradient(135deg, #eb2f96, #c41d7f)',
    bg: 'linear-gradient(135deg, #fff0f6 0%, #ffadd2 100%)',
    accent: '#eb2f96',
  },
  {
    icon: <RobotOutlined style={{ fontSize: 26 }} />,
    title: 'AI Assistant',
    description: 'Natural-language chat in English & Kannada. Query cases, get stats, generate reports.',
    path: '/chat',
    gradient: 'linear-gradient(135deg, #13c2c2, #08979c)',
    bg: 'linear-gradient(135deg, #e6fffb 0%, #b5f5ec 100%)',
    accent: '#13c2c2',
  },
];

const platformStats = [
  { title: 'FIR Cases', value: 15, icon: <FileTextOutlined />, gradient: 'linear-gradient(135deg, #1890ff, #096dd9)', shadow: '0 4px 14px rgba(24,144,255,0.3)' },
  { title: 'Accused', value: 15, icon: <TeamOutlined />, gradient: 'linear-gradient(135deg, #ff4d4f, #cf1322)', shadow: '0 4px 14px rgba(255,77,79,0.3)' },
  { title: 'Victims', value: 9, icon: <SafetyCertificateOutlined />, gradient: 'linear-gradient(135deg, #52c41a, #389e0d)', shadow: '0 4px 14px rgba(82,196,26,0.3)' },
  { title: 'Arrests', value: 12, icon: <AlertOutlined />, gradient: 'linear-gradient(135deg, #faad14, #d48806)', shadow: '0 4px 14px rgba(250,173,20,0.3)' },
  { title: 'Transactions', value: 10, icon: <DollarOutlined />, gradient: 'linear-gradient(135deg, #722ed1, #531dab)', shadow: '0 4px 14px rgba(114,46,209,0.3)' },
  { title: 'DB Tables', value: 32, icon: <DatabaseOutlined />, gradient: 'linear-gradient(135deg, #13c2c2, #08979c)', shadow: '0 4px 14px rgba(19,194,194,0.3)' },
];

const architectureNodes = [
  { icon: <CloudServerOutlined />, label: 'React UI', color: '#61dafb' },
  { icon: <ApiOutlined />, label: 'API Gateway', color: '#1890ff' },
  { icon: <SafetyCertificateOutlined />, label: 'Keycloak SSO', color: '#4d8dff' },
  { icon: <DatabaseOutlined />, label: 'Eureka Registry', color: '#6db33f' },
  { icon: <AlertOutlined />, label: 'Incident Service', color: '#ff4d4f' },
  { icon: <RobotOutlined />, label: 'AI Assistant', color: '#13c2c2' },
  { icon: <DollarOutlined />, label: 'Financial Service', color: '#faad14' },
  { icon: <FileTextOutlined />, label: 'Report Service', color: '#722ed1' },
  { icon: <ApartmentOutlined />, label: 'Graph Service', color: '#52c41a' },
  { icon: <SearchOutlined />, label: 'Search Service', color: '#eb2f96' },
  { icon: <FileSearchOutlined />, label: 'Person Service', color: '#fa8c16' },
  { icon: <BankOutlined />, label: 'Notification Service', color: '#a0d911' },
  { icon: <LineChartOutlined />, label: 'Analytics Service', color: '#2f54eb' },
];

const roles = [
  {
    icon: <LockOutlined style={{ fontSize: 32 }} />,
    title: 'Admin',
    color: '#ff4d4f',
    gradient: 'linear-gradient(135deg, #ff4d4f, #a8071a)',
    bg: 'linear-gradient(135deg, #fff1f0 0%, #ffccc7 100%)',
    description: 'Full system access. Manage users, populate databases, reindex search, and access all modules.',
    tags: ['System Config', 'Data Populate', 'Reindex'],
  },
  {
    icon: <UserSwitchOutlined style={{ fontSize: 32 }} />,
    title: 'Officer',
    color: '#1890ff',
    gradient: 'linear-gradient(135deg, #1890ff, #096dd9)',
    bg: 'linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%)',
    description: 'Investigative access. Manage cases, generate reports, explore criminal networks, and use AI.',
    tags: ['Case Management', 'Reports', 'AI Chat'],
  },
  {
    icon: <SafetyCertificateOutlined style={{ fontSize: 32 }} />,
    title: 'Viewer',
    color: '#52c41a',
    gradient: 'linear-gradient(135deg, #52c41a, #389e0d)',
    bg: 'linear-gradient(135deg, #f6ffed 0%, #b7eb8f 100%)',
    description: 'Read-only access. View dashboards, incidents, financial data, and use the AI assistant.',
    tags: ['Dashboard', 'View Cases', 'AI Chat'],
  },
];

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{ minHeight: '100vh', background: '#f5f7fa' }}>
      {/* Hero */}
      <div
        style={{
          background: 'linear-gradient(135deg, #001529 0%, #002766 35%, #00509e 70%, #096dd9 100%)',
          padding: '80px 24px 72px',
          textAlign: 'center',
          position: 'relative',
          overflow: 'hidden',
        }}
      >
        {/* Decorative mesh */}
        <div style={{ position: 'absolute', inset: 0, opacity: 0.04, backgroundImage: 'radial-gradient(circle at 1px 1px, #fff 1px, transparent 0)', backgroundSize: '32px 32px' }} />
        <div style={{ position: 'absolute', top: -100, right: -80, width: 400, height: 400, borderRadius: '50%', background: 'rgba(24,144,255,0.07)' }} />
        <div style={{ position: 'absolute', bottom: -120, left: -60, width: 320, height: 320, borderRadius: '50%', background: 'rgba(82,196,26,0.05)' }} />
        <div style={{ position: 'absolute', top: 40, left: '20%', width: 160, height: 160, borderRadius: '50%', background: 'rgba(114,46,209,0.04)' }} />

        <div style={{ position: 'relative', zIndex: 1, maxWidth: 800, margin: '0 auto' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 8, background: 'rgba(24,144,255,0.12)', border: '1px solid rgba(24,144,255,0.25)', borderRadius: 24, padding: '6px 18px', marginBottom: 24 }}>
            <SafetyCertificateOutlined style={{ color: '#1890ff', fontSize: 14 }} />
            <Text style={{ color: 'rgba(255,255,255,0.9)', fontSize: 12, fontWeight: 600, letterSpacing: 1 }}>LAW ENFORCEMENT INTELLIGENCE</Text>
          </div>

          <Title level={1} style={{ color: '#fff', margin: 0, fontSize: 44, fontWeight: 800, letterSpacing: -1, lineHeight: 1.15 }}>
            Crime Analytics<br />Platform
          </Title>

          <Paragraph style={{ color: 'rgba(255,255,255,0.65)', fontSize: 17, maxWidth: 580, margin: '20px auto 0', lineHeight: 1.75 }}>
            An integrated intelligence system — manage FIRs, track financial trails,
            map criminal networks, and leverage AI, all from a single secure dashboard.
          </Paragraph>

          <div style={{ marginTop: 36, display: 'flex', justifyContent: 'center', gap: 14, flexWrap: 'wrap' }}>
            <Button
              type="primary"
              size="large"
              icon={<PlayCircleOutlined />}
              onClick={() => navigate('/dashboard')}
              style={{
                height: 48,
                borderRadius: 10,
                fontWeight: 600,
                paddingInline: 32,
                fontSize: 15,
                background: 'linear-gradient(135deg, #1890ff, #096dd9)',
                boxShadow: '0 4px 16px rgba(24,144,255,0.4)',
                border: 'none',
              }}
            >
              Open Dashboard
            </Button>
            <Button
              size="large"
              icon={<BulbOutlined />}
              onClick={() => navigate('/help')}
              style={{
                height: 48,
                borderRadius: 10,
                fontWeight: 500,
                paddingInline: 28,
                fontSize: 15,
                color: '#fff',
                borderColor: 'rgba(255,255,255,0.3)',
                background: 'rgba(255,255,255,0.06)',
              }}
            >
              How to Use
            </Button>
          </div>
        </div>
      </div>

      <div style={{ maxWidth: 1140, margin: '0 auto', padding: '0 24px' }}>
        {/* Floating Stat Cards */}
        <div style={{ marginTop: -32, position: 'relative', zIndex: 2, marginBottom: 48 }}>
          <Row gutter={[12, 12]}>
            {platformStats.map((s) => (
              <Col xs={12} sm={8} md={4} key={s.title}>
                <div
                  style={{
                    background: s.gradient,
                    borderRadius: 14,
                    padding: '20px 14px',
                    boxShadow: s.shadow,
                    textAlign: 'center',
                    transition: 'transform 0.2s, box-shadow 0.2s',
                    cursor: 'default',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-3px)';
                    e.currentTarget.style.boxShadow = s.shadow.replace('0.3', '0.45');
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = s.shadow;
                  }}
                >
                  <div style={{ fontSize: 20, color: 'rgba(255,255,255,0.85)', marginBottom: 6 }}>{s.icon}</div>
                  <div style={{ color: '#fff', fontSize: 30, fontWeight: 800, lineHeight: 1 }}>{s.value}</div>
                  <div style={{ color: 'rgba(255,255,255,0.75)', fontSize: 11, fontWeight: 500, marginTop: 4, letterSpacing: 0.5 }}>{s.title.toUpperCase()}</div>
                </div>
              </Col>
            ))}
          </Row>
        </div>

        {/* Section Divider */}
        <div style={{ textAlign: 'center', marginBottom: 12 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 12 }}>
            <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
            <RocketOutlined style={{ color: '#1890ff', fontSize: 16 }} />
            <Text strong style={{ fontSize: 12, color: '#888', letterSpacing: 1 }}>PLATFORM MODULES</Text>
            <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
          </div>
        </div>

        {/* Module Cards */}
        <div style={{ marginBottom: 56 }}>
          <Row gutter={[16, 16]}>
            {features.map((f) => (
              <Col xs={24} sm={12} md={8} key={f.title}>
                <div
                  onClick={() => navigate(f.path)}
                  style={{
                    background: '#fff',
                    borderRadius: 14,
                    cursor: 'pointer',
                    overflow: 'hidden',
                    boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
                    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    border: '1px solid #f0f0f0',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.boxShadow = '0 8px 28px rgba(0,0,0,0.12)';
                    e.currentTarget.style.transform = 'translateY(-4px)';
                    e.currentTarget.style.borderColor = f.accent + '40';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.boxShadow = '0 1px 6px rgba(0,0,0,0.06)';
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.borderColor = '#f0f0f0';
                  }}
                >
                  <div style={{ height: 5, background: f.gradient }} />
                  <div style={{ padding: '22px 24px 24px' }}>
                    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14, marginBottom: 12 }}>
                      <div style={{
                        background: f.bg,
                        borderRadius: 12,
                        width: 52,
                        height: 52,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        flexShrink: 0,
                        color: f.accent,
                        border: `1px solid ${f.accent}20`,
                      }}>
                        {f.icon}
                      </div>
                      <div style={{ flex: 1, paddingTop: 2 }}>
                        <Text strong style={{ fontSize: 16, display: 'block', marginBottom: 6 }}>{f.title}</Text>
                        <Text type="secondary" style={{ fontSize: 13, lineHeight: 1.6 }}>{f.description}</Text>
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: f.accent, fontWeight: 500, fontSize: 13 }}>
                      <span>Open Module</span>
                      <ArrowRightOutlined style={{ fontSize: 11 }} />
                    </div>
                  </div>
                </div>
              </Col>
            ))}
          </Row>
        </div>

        {/* Architecture */}
        <div style={{ marginBottom: 56 }}>
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
              <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
              <DatabaseOutlined style={{ color: '#52c41a', fontSize: 16 }} />
              <Text strong style={{ fontSize: 12, color: '#888', letterSpacing: 1 }}>ARCHITECTURE</Text>
              <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
            </div>
            <Title level={2} style={{ margin: 0 }}>System Architecture</Title>
            <Text type="secondary" style={{ fontSize: 14 }}>
              Microservices powered by Spring Boot, Neo4j, and Elasticsearch
            </Text>
          </div>

          <div
            style={{
              background: '#fff',
              borderRadius: 14,
              boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
              border: '1px solid #f0f0f0',
              padding: 28,
            }}
          >
            <Row gutter={[10, 10]}>
              {architectureNodes.map((node) => (
                <Col xs={8} sm={6} md={4} lg={3} key={node.label}>
                  <div
                    style={{
                      textAlign: 'center',
                      padding: '12px 6px',
                      borderRadius: 10,
                      background: `${node.color}08`,
                      border: `1px solid ${node.color}18`,
                      transition: 'all 0.2s',
                    }}
                    onMouseEnter={(e) => {
                      e.currentTarget.style.borderColor = node.color + '50';
                      e.currentTarget.style.background = node.color + '14';
                    }}
                    onMouseLeave={(e) => {
                      e.currentTarget.style.borderColor = node.color + '18';
                      e.currentTarget.style.background = node.color + '08';
                    }}
                  >
                    <div style={{ fontSize: 22, color: node.color, marginBottom: 4 }}>{node.icon}</div>
                    <Text strong style={{ fontSize: 11, display: 'block', lineHeight: 1.3 }}>{node.label}</Text>
                  </div>
                </Col>
              ))}
            </Row>

            <div style={{ marginTop: 20, textAlign: 'center', padding: '14px 0 0', borderTop: '1px solid #f0f0f0' }}>
              <Space size={12} wrap>
                {[
                  { label: 'PostgreSQL', color: '#1890ff' },
                  { label: 'Neo4j Graph DB', color: '#52c41a' },
                  { label: 'Elasticsearch', color: '#faad14' },
                  { label: 'Keycloak SSO', color: '#722ed1' },
                  { label: 'Docker & K8s', color: '#13c2c2' },
                ].map((t) => (
                  <Tag key={t.label} color={t.color} style={{ borderRadius: 6, padding: '2px 10px', fontSize: 12, margin: 0 }}>{t.label}</Tag>
                ))}
              </Space>
            </div>
          </div>
        </div>

        {/* Roles */}
        <div style={{ marginBottom: 56 }}>
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
              <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
              <LockOutlined style={{ color: '#ff4d4f', fontSize: 16 }} />
              <Text strong style={{ fontSize: 12, color: '#888', letterSpacing: 1 }}>ACCESS CONTROL</Text>
              <div style={{ width: 40, height: 1, background: '#d9d9d9' }} />
            </div>
            <Title level={2} style={{ margin: 0 }}>Role-Based Access</Title>
            <Text type="secondary" style={{ fontSize: 14 }}>
              Three permission levels for secure, granular access
            </Text>
          </div>

          <Row gutter={[16, 16]}>
            {roles.map((role) => (
              <Col xs={24} md={8} key={role.title}>
                <div
                  style={{
                    background: '#fff',
                    borderRadius: 14,
                    overflow: 'hidden',
                    boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
                    border: '1px solid #f0f0f0',
                    height: '100%',
                    transition: 'all 0.3s',
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.boxShadow = '0 6px 20px rgba(0,0,0,0.1)';
                    e.currentTarget.style.transform = 'translateY(-2px)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.boxShadow = '0 1px 6px rgba(0,0,0,0.06)';
                    e.currentTarget.style.transform = 'translateY(0)';
                  }}
                >
                  <div style={{ height: 5, background: role.gradient }} />
                  <div style={{ padding: '24px 24px 20px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 14 }}>
                      <div style={{
                        background: role.bg,
                        borderRadius: 10,
                        width: 52,
                        height: 52,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: role.color,
                        border: `1px solid ${role.color}20`,
                      }}>
                        {role.icon}
                      </div>
                      <div>
                        <Text strong style={{ fontSize: 18, display: 'block' }}>{role.title}</Text>
                        <Tag color={role.color} style={{ borderRadius: 10, fontSize: 10, lineHeight: '18px', padding: '0 6px', marginTop: 2 }}>ROLE</Tag>
                      </div>
                    </div>
                    <Text type="secondary" style={{ fontSize: 13, lineHeight: 1.6, display: 'block', marginBottom: 14 }}>
                      {role.description}
                    </Text>
                    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
                      {role.tags.map((t) => (
                        <Tag key={t} color={role.color} style={{ borderRadius: 8, fontSize: 11, padding: '1px 8px', margin: 0 }}>{t}</Tag>
                      ))}
                    </div>
                  </div>
                </div>
              </Col>
            ))}
          </Row>
        </div>

        {/* Footer */}
        <div style={{
          background: 'linear-gradient(135deg, #001529 0%, #002766 100%)',
          borderRadius: '14px 14px 0 0',
          padding: '40px 24px 32px',
          textAlign: 'center',
          marginTop: 20,
        }}>
          <div style={{
            width: 48,
            height: 48,
            borderRadius: 12,
            background: 'linear-gradient(135deg, #1890ff, #096dd9)',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: 14,
          }}>
            <AlertOutlined style={{ fontSize: 22, color: '#fff' }} />
          </div>
          <Title level={4} style={{ margin: '0 0 4px', color: '#fff' }}>Crime Analytics Platform</Title>
          <Text style={{ color: 'rgba(255,255,255,0.5)', fontSize: 13, display: 'block', marginBottom: 16 }}>
            FIR Management & Intelligence System
          </Text>
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.08)', paddingTop: 16 }}>
            <Space size={10} wrap>
              {[
                { label: 'React + TypeScript', color: '#1890ff' },
                { label: 'Spring Boot', color: '#52c41a' },
                { label: 'Neo4j', color: '#722ed1' },
                { label: 'Elasticsearch', color: '#faad14' },
                { label: 'Keycloak', color: '#eb2f96' },
                { label: 'Docker', color: '#13c2c2' },
              ].map((t) => (
                <Tag key={t.label} color={t.color} style={{ borderRadius: 6, fontSize: 11, padding: '2px 10px', margin: 0, background: `${t.color}20`, borderColor: `${t.color}40`, color: '#fff' }}>{t.label}</Tag>
              ))}
            </Space>
          </div>
        </div>
      </div>
    </div>
  );
};

export { HomePage };
