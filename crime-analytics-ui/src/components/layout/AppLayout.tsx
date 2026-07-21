import React from 'react';
import { Layout, Menu, Button, Tag } from 'antd';
import {
  HomeOutlined,
  DashboardOutlined,
  AlertOutlined,
  DollarOutlined,
  FileTextOutlined,
  RobotOutlined,
  QuestionCircleOutlined,
  ApartmentOutlined,
  SearchOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';

const { Sider, Content, Header } = Layout;

const menuItems = [
  { key: '/', icon: <HomeOutlined />, label: 'Home', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/incidents', icon: <AlertOutlined />, label: 'Incidents', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/financial', icon: <DollarOutlined />, label: 'Financial', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/reports', icon: <FileTextOutlined />, label: 'Reports', roles: ['ADMIN', 'OFFICER'] },
  { key: '/graph', icon: <ApartmentOutlined />, label: 'Criminal Network', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/search', icon: <SearchOutlined />, label: 'Advanced Search', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/chat', icon: <RobotOutlined />, label: 'AI Assistant', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
  { key: '/help', icon: <QuestionCircleOutlined />, label: 'How to Use', roles: ['ADMIN', 'OFFICER', 'VIEWER'] },
];

const roleColor: Record<string, string> = {
  ADMIN: 'red',
  OFFICER: 'blue',
  VIEWER: 'green',
};

export const AppLayout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useAuthStore((s) => s.logout);
  const userRoles = useAuthStore((s) => s.userRoles);

  const visibleItems = menuItems.filter(
    (item) => item.roles.some((r) => userRoles.includes(r))
  );

  const selectedKey = visibleItems.find(m =>
    m.key === '/' ? location.pathname === '/' : location.pathname.startsWith(m.key)
  )?.key || '/dashboard';

  const primaryRole = userRoles[0] || 'VIEWER';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        breakpoint="lg"
        collapsedWidth="0"
        style={{
          background: 'linear-gradient(180deg, #001529 0%, #002140 100%)',
          borderRight: '1px solid rgba(255,255,255,0.06)',
        }}
      >
        <div
          style={{
            height: 64,
            margin: 0,
            padding: '0 16px',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            borderBottom: '1px solid rgba(255,255,255,0.08)',
          }}
        >
          <div
            style={{
              width: 36,
              height: 36,
              borderRadius: 10,
              background: 'linear-gradient(135deg, #1890ff, #096dd9)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0,
            }}
          >
            <AlertOutlined style={{ fontSize: 16, color: '#fff' }} />
          </div>
          <div>
            <div style={{ color: '#fff', fontWeight: 700, fontSize: 14, lineHeight: 1.2 }}>Crime</div>
            <div style={{ color: 'rgba(255,255,255,0.5)', fontSize: 11, lineHeight: 1.2 }}>Analytics</div>
          </div>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={visibleItems}
          onClick={({ key }) => navigate(key)}
          style={{ background: 'transparent', borderRight: 'none', marginTop: 8 }}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: '#fff',
            padding: '0 24px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
            position: 'relative',
            zIndex: 1,
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <h2 style={{ margin: 0, fontSize: 16, fontWeight: 600, color: '#1a1a2e' }}>
              {visibleItems.find(m => m.key === selectedKey)?.label || 'Crime Analytics Platform'}
            </h2>
            <Tag color={roleColor[primaryRole] || 'default'} style={{ borderRadius: 12, fontSize: 11, lineHeight: '20px', padding: '0 8px' }}>
              {primaryRole}
            </Tag>
          </div>
          <Button
            type="text"
            icon={<LogoutOutlined />}
            onClick={logout}
            size="small"
            style={{ color: '#888', borderRadius: 6 }}
          >
            Sign Out
          </Button>
        </Header>
        <div style={{ height: 3, background: 'linear-gradient(90deg, #1890ff, #722ed1, #13c2c2)' }} />
        <Content style={{ margin: 0, padding: 0, background: '#f5f7fa' }}>
          {children}
        </Content>
      </Layout>
    </Layout>
  );
};
