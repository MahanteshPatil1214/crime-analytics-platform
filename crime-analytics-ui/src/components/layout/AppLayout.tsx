import React from 'react';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  AlertOutlined,
  DollarOutlined,
  FileTextOutlined,
  RobotOutlined,
  QuestionCircleOutlined,
  ApartmentOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const { Sider, Content, Header } = Layout;

const menuItems = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
  { key: '/incidents', icon: <AlertOutlined />, label: 'Incidents' },
  { key: '/financial', icon: <DollarOutlined />, label: 'Financial' },
  { key: '/reports', icon: <FileTextOutlined />, label: 'Reports' },
  { key: '/graph', icon: <ApartmentOutlined />, label: 'Criminal Network' },
  { key: '/search', icon: <SearchOutlined />, label: 'Advanced Search' },
  { key: '/chat', icon: <RobotOutlined />, label: 'AI Assistant' },
  { key: '/help', icon: <QuestionCircleOutlined />, label: 'How to Use' },
];

export const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const selectedKey = menuItems.find(m => location.pathname.startsWith(m.key))?.key || '/dashboard';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider breakpoint="lg" collapsedWidth="0" style={{ background: '#001529' }}>
        <div style={{ height: 48, margin: 12, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <AlertOutlined style={{ fontSize: 20, color: '#1890ff', marginRight: 8 }} />
          <span style={{ color: '#fff', fontWeight: 700, fontSize: 14, whiteSpace: 'nowrap' }}>Crime Analytics</span>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 24px', display: 'flex', alignItems: 'center', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>
          <h2 style={{ margin: 0, fontSize: 16, color: '#333' }}>
            {menuItems.find(m => m.key === selectedKey)?.label || 'Crime Analytics Platform'}
          </h2>
        </Header>
        <Content style={{ margin: 0, padding: 0, background: '#f0f2f5' }}>
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};
