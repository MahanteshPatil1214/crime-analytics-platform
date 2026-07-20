import React from 'react';
import { Card, Row, Col, Typography, Divider, Tag, Input, List, Space } from 'antd';
import {
  DashboardOutlined,
  AlertOutlined,
  DollarOutlined,
  FileTextOutlined,
  RobotOutlined,
  SearchOutlined,
  InfoCircleOutlined,
  BulbOutlined,
  GlobalOutlined,
  ApartmentOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title, Paragraph, Text } = Typography;

const features = [
  {
    icon: <DashboardOutlined style={{ fontSize: 28, color: '#1890ff' }} />,
    title: 'Dashboard',
    path: '/dashboard',
    color: '#e6f7ff',
    description: 'Get a bird\'s-eye view of all crime data at a glance.',
    howToUse: [
      'Total case counts by status (Open, Under Investigation, Charge Sheeted, Closed)',
      'District-wise bar chart showing crime distribution across Karnataka',
      'Crime Head pie chart showing types of offences (Property, Body, Economic, etc.)',
      'Financial summary with flagged suspicious transactions count',
    ],
    tips: [
      'Use the GIS Map at the bottom to see crime locations on an interactive map',
      'Click any district bar to see relative crime volume',
    ],
  },
  {
    icon: <AlertOutlined style={{ fontSize: 28, color: '#ff4d4f' }} />,
    title: 'Incidents',
    path: '/incidents',
    color: '#fff1f0',
    description: 'Browse, search, and view detailed FIR case records.',
    howToUse: [
      'View all registered FIR cases in a searchable list',
      'Filter by district, status, or crime head using the dropdown filters',
      'Click any case row to open the full Case Detail page',
      'Case Detail shows: Complainant, Victim, Accused, Arrests, Act Sections, and linked Financial Transactions',
      'Crime numbers (e.g. 104430006202600001) are displayed for each case',
    ],
    tips: [
      'Use the search bar to find cases by crime number or keywords',
      'Person IDs like A1, A2 identify accused persons in the case',
    ],
  },
  {
    icon: <DollarOutlined style={{ fontSize: 28, color: '#faad14' }} />,
    title: 'Financial',
    path: '/financial',
    color: '#fffbe6',
    description: 'Track financial transactions linked to cases and detect suspicious activity.',
    howToUse: [
      'View all financial transactions (Wire Transfer, Cash Deposit, Crypto, etc.)',
      'Filter by transaction type or search by account number',
      'Toggle "Flagged Only" to see suspicious transactions flagged by the system',
      'Risk Score indicator: CRITICAL (9+), HIGH (7+), MEDIUM (4+), LOW',
      'Bar chart shows transaction amounts by type for quick analysis',
    ],
    tips: [
      'Flagged transactions with high risk scores should be prioritized for investigation',
      'Each transaction is linked to a specific case for cross-reference',
    ],
  },
  {
    icon: <FileTextOutlined style={{ fontSize: 28, color: '#722ed1' }} />,
    title: 'Reports',
    path: '/reports',
    color: '#f9f0ff',
    description: 'Generate black-and-white typewriter-style PDF reports for cases.',
    howToUse: [
      'Incident Report tab: Enter FIR Number, Police Station, and District to generate a FIR PDF',
      'Criminal Profile tab: Enter person details to generate a suspect profile report',
      'Reports are generated in government-standard Courier typewriter format',
      'PDF downloads automatically to your browser once generated',
    ],
    tips: [
      'Use FIR numbers from the Incidents page (e.g. 104430006202600001)',
      'Reports include all case details, accused persons, and act sections',
    ],
  },
  {
    icon: <ApartmentOutlined style={{ fontSize: 28, color: '#722ed1' }} />,
    title: 'Criminal Network',
    path: '/graph',
    color: '#f9f0ff',
    description: 'Interactive graph visualization of criminal networks using Neo4j. Explore relationships between persons and cases.',
    howToUse: [
      'Click "Populate from DB" to load all cases, accused, victims, and complainants into Neo4j',
      'Use the search bar to find persons by name and center the graph on them',
      'Click any person node (red=accused, green=victim, orange=complainant) to expand their network',
      'Click the background to reset to the full graph view',
      'Filter by person type using the dropdown to show only accused, victims, or complainants',
      'Zoom in/out using the +/- buttons or mouse wheel',
    ],
    tips: [
      'Node colors: Blue=Cases, Red=Accused, Green=Victims, Orange=Complainants',
      'Edges show relationships: ACCUSED_OF, VICTIM_OF, COMPLAINT_OF, CO_OFFENDER',
      'CO_OFFENDER edges connect accused persons who appear in the same case',
    ],
  },
  {
    icon: <SearchOutlined style={{ fontSize: 28, color: '#eb2f96' }} />,
    title: 'Advanced Search',
    path: '/search',
    color: '#fff0f6',
    description: 'Full-text search across cases, persons, and financial transactions using Elasticsearch.',
    howToUse: [
      'Type any keyword to search across all data: names, crime numbers, case facts',
      'Switch between Cases, Persons, and Financial tabs to filter results',
      'Cases tab: filter by District and Status',
      'Persons tab: filter by person type (Accused, Victim, Complainant)',
      'Financial tab: toggle "Flagged Only" to see suspicious transactions',
      'Click "Reindex All" to refresh the Elasticsearch index from the database',
    ],
    tips: [
      'Search uses fuzzy matching - partial names and typos still work',
      'Act sections (e.g. IPC 302) are searchable',
      'Brief facts from FIR documents are full-text indexed',
    ],
  },
  {
    icon: <RobotOutlined style={{ fontSize: 28, color: '#13c2c2' }} />,
    title: 'AI Assistant',
    path: '/chat',
    color: '#e6fffb',
    description: 'Chat with the AI to search cases, get statistics, and explore data using natural language. Supports English and Kannada (ಕನ್ನಡ).',
    howToUse: [
      'Type natural language queries like "show me all open cases" or "list all accused"',
      'Kannada: "ಎಲ್ಲಾ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ" (show all cases), "ಎಲ್ಲಾ ಅಪರಾಧಿಗಳು" (all accused)',
      'Search for a specific case by typing its crime number directly (e.g. 104430006202600001)',
      'Ask about crime statistics: "show crime statistics" or "ಅಂಕೆ ಸಂಖ್ಯಾ"',
      'Search persons by name: "search for Ravi" or "ರವಿ ಹುಡುಕಿ"',
      'Filter by district: "cases in Bengaluru" or "ಬೆಂಗಳೂರಿನ ಪ್ರಕರಣಗಳು"',
      'Generate reports: "generate FIR report for case 1"',
      'Check financial data: "show financial transactions for case 3" or "ವ್ಯವಹಾರಗಳು"',
      'Case IDs in responses are clickable - click to navigate directly to case detail',
    ],
    tips: [
      'Supported Kannada commands: ನಮಸ್ಕಾರ, ಸಹಾಯ, ಪ್ರಕರಣ, ಅಪರಾಧಿ, ಸಂತ್ರಸ್ತ, ವ್ಯವಹಾರ, ವರದಿ',
      'Click suggestion cards on the welcome screen for quick queries',
      'Use "Clear" button to start a new conversation',
    ],
  },
];

const sampleQueries = [
  { query: 'Show me all open cases', kannada: 'ತೆರೆದ ಪ್ರಕರಣಗಳು', description: 'Lists all cases with Registered status' },
  { query: 'List all accused persons', kannada: 'ಎಲ್ಲಾ ಅಪರಾಧಿಗಳು', description: 'Shows all 15 accused with names, ages, and case links' },
  { query: 'Search for Ravi', kannada: 'ರವಿ ಹುಡುಕಿ', description: 'Finds persons matching "Ravi" across accused, victims, and complainants' },
  { query: 'Show crime statistics', kannada: 'ಅಂಕೆ ಸಂಖ್ಯಾ', description: 'Displays total cases, status breakdown, and involvement counts' },
  { query: '104430006202600001', kannada: '104430006202600001', description: 'Looks up case by crime number with full details' },
  { query: 'Show crime head distribution', kannada: 'ಅಪರಾಧ ವರ್ಗ ವಿತರಣೆ', description: 'Shows pie chart data of offence categories' },
  { query: 'Cases in Bengaluru', kannada: 'ಬೆಂಗಳೂರಿನ ಪ್ರಕರಣಗಳು', description: 'Filter cases by district' },
  { query: 'Show financial transactions for case 3', kannada: 'ಪ್ರಕರಣ 3 ವ್ಯವಹಾರ', description: 'Lists transactions linked to a specific case' },
  { query: 'Hello / Hi', kannada: 'ನಮಸ್ಕಾರ / ಹಾಯ್ / ಹಲೋ', description: 'Greeting in English or Kannada' },
  { query: 'Help', kannada: 'ಸಹಾಯ / ಹೆಲ್ಪ್', description: 'Shows all available commands' },
];

export const HelpPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: 24, maxWidth: 1100, margin: '0 auto' }}>
      <Card bordered={false} style={{ marginBottom: 24, background: 'linear-gradient(135deg, #e6f7ff 0%, #f9f0ff 100%)' }}>
        <Space align="center" size={12}>
          <InfoCircleOutlined style={{ fontSize: 32, color: '#1890ff' }} />
          <div>
            <Title level={3} style={{ margin: 0 }}>How to Use - Crime Analytics Platform</Title>
            <Text type="secondary">Quick guide to navigate and use all features effectively</Text>
          </div>
        </Space>
      </Card>

      <Card bordered={false} style={{ marginBottom: 24, background: '#e6fffb' }}>
        <Space align="start" size={12}>
          <GlobalOutlined style={{ fontSize: 28, color: '#13c2c2' }} />
          <div>
            <Title level={5} style={{ margin: 0 }}>ಕನ್ನಡ ಭಾಷೆ ಬೆಂಬಲ / Kannada Language Support</Title>
            <Paragraph style={{ margin: '8px 0 0', fontSize: 13 }}>
              AI Assistant supports both <Text strong>English</Text> and <Text strong>ಕನ್ನಡ (Kannada)</Text>. You can type queries in either language or mix both.
            </Paragraph>
            <Row gutter={[16, 8]} style={{ marginTop: 12 }}>
              <Col span={6}>
                <Tag color="blue">Greeting</Tag><br />
                <Text code style={{ fontSize: 12 }}>ನಮಸ್ಕಾರ</Text>
              </Col>
              <Col span={6}>
                <Tag color="green">Cases</Tag><br />
                <Text code style={{ fontSize: 12 }}>ಎಲ್ಲಾ ಪ್ರಕರಣಗಳನ್ನು ತೋರಿಸಿ</Text>
              </Col>
              <Col span={6}>
                <Tag color="red">Accused</Tag><br />
                <Text code style={{ fontSize: 12 }}>ಎಲ್ಲಾ ಅಪರಾಧಿಗಳು</Text>
              </Col>
              <Col span={6}>
                <Tag color="orange">Help</Tag><br />
                <Text code style={{ fontSize: 12 }}>ಸಹಾಯ</Text>
              </Col>
            </Row>
            <Row gutter={[16, 8]} style={{ marginTop: 8 }}>
              <Col span={6}>
                <Tag color="purple">Victims</Tag><br />
                <Text code style={{ fontSize: 12 }}>ಎಲ್ಲಾ ಸಂತ್ರಸ್ತರು</Text>
              </Col>
              <Col span={6}>
                <Tag color="cyan">Search</Tag><br />
                <Text code style={{ fontSize: 12 }}>ರವಿ ಹುಡುಕಿ</Text>
              </Col>
              <Col span={6}>
                <Tag color="gold">Statistics</Tag><br />
                <Text code style={{ fontSize: 12 }}>ಅಂಕೆ ಸಂಖ್ಯಾ</Text>
              </Col>
              <Col span={6}>
                <Tag color="magenta">Financial</Tag><br />
                <Text code style={{ fontSize: 12 }}>ವ್ಯವಹಾರಗಳು</Text>
              </Col>
            </Row>
          </div>
        </Space>
      </Card>

      {features.map((feature) => (
        <Card
          key={feature.title}
          bordered={false}
          style={{ marginBottom: 20, borderLeft: '4px solid #1890ff' }}
        >
          <div style={{ display: 'flex', gap: 16, cursor: 'pointer' }} onClick={() => navigate(feature.path)}>
            <div style={{ background: feature.color, borderRadius: 8, width: 56, height: 56, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              {feature.icon}
            </div>
            <div style={{ flex: 1 }}>
              <Space>
                <Title level={4} style={{ margin: 0 }}>{feature.title}</Title>
                <Tag color="blue" style={{ cursor: 'pointer' }}>Open →</Tag>
              </Space>
              <Paragraph type="secondary" style={{ margin: '4px 0 12px' }}>{feature.description}</Paragraph>

              <Text strong>How to use:</Text>
              <List
                size="small"
                dataSource={feature.howToUse}
                renderItem={(item) => (
                  <List.Item style={{ padding: '4px 0', border: 'none' }}>
                    <Text style={{ fontSize: 13 }}>• {item}</Text>
                  </List.Item>
                )}
              />

              {feature.tips && feature.tips.length > 0 && (
                <>
                  <BulbOutlined style={{ color: '#faad14', marginRight: 6 }} />
                  <Text type="secondary" italic style={{ fontSize: 13 }}>
                    {feature.tips.join(' | ')}
                  </Text>
                </>
              )}
            </div>
          </div>
        </Card>
      ))}

      <Card title={<><SearchOutlined /> Sample AI Queries</>} bordered={false} style={{ marginTop: 24 }}>
        <Paragraph type="secondary">
          Try these natural language queries in the AI Assistant chat:
        </Paragraph>
        <List
          dataSource={sampleQueries}
          renderItem={(item) => (
            <List.Item
              style={{ cursor: 'pointer', padding: '8px 12px', borderRadius: 6, marginBottom: 4 }}
              onClick={() => navigate('/chat')}
              onMouseEnter={(e) => (e.currentTarget.style.background = '#f5f5f5')}
              onMouseLeave={(e) => (e.currentTarget.style.background = 'transparent')}
            >
              <List.Item.Meta
                title={
                  <Space>
                    <Text code style={{ fontSize: 13 }}>{item.query}</Text>
                    {item.kannada !== item.query && <Text code style={{ fontSize: 13, color: '#13c2c2' }}>{item.kannada}</Text>}
                  </Space>
                }
                description={<Text type="secondary" style={{ fontSize: 12 }}>{item.description}</Text>}
              />
            </List.Item>
          )}
        />
      </Card>

      <Card bordered={false} style={{ marginTop: 24, background: '#fafafa' }}>
        <Title level={5}>System Architecture</Title>
        <Row gutter={[16, 12]}>
          <Col span={8}><Tag color="blue">Eureka Service Registry</Tag> :8761</Col>
          <Col span={8}><Tag color="purple">API Gateway</Tag> :8080</Col>
          <Col span={8}><Tag color="green">Frontend (React)</Tag> :3000</Col>
          <Col span={8}><Tag color="red">Incident Service</Tag> :8082</Col>
          <Col span={8}><Tag color="cyan">AI Assistant Service</Tag> :8087</Col>
          <Col span={8}><Tag color="orange">Report Service</Tag> :8089</Col>
          <Col span={8}><Tag color="purple">Graph Service</Tag> :8084 (Neo4j)</Col>
          <Col span={8}><Tag color="magenta">Search Service</Tag> :8085 (Elasticsearch)</Col>
        </Row>
        <Divider />
        <Text type="secondary" style={{ fontSize: 12 }}>
          Karnataka Police FIR System - 32 database tables | 15 FIR cases | 15 Complainants | 9 Victims | 15 Accused | 12 Arrests | 10 Financial Transactions | Neo4j Graph | Elasticsearch Index
        </Text>
      </Card>
    </div>
  );
};
