import React, { useState } from 'react';
import { Layout, Menu, theme, Dropdown, Avatar, Space, Typography } from 'antd';
import {
    DashboardOutlined,
    FileTextOutlined,
    UserOutlined,
    AppstoreOutlined,
    TagsOutlined,
    LogoutOutlined,
    TeamOutlined
} from '@ant-design/icons';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const { Header, Content, Footer, Sider } = Layout;

const AppLayout = () => {
    const [collapsed, setCollapsed] = useState(false);
    const {
        token: { colorBgContainer, borderRadiusLG },
    } = theme.useToken();
    const location = useLocation();
    const { currentUser, logout } = useAuth();
    const navigate = useNavigate();

    // Determine Role
    // Adjust this logic based on how your backend sends the role
    // Common patterns: user.role = "ADMIN" or user.roles = ["ROLE_ADMIN"]
    const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.roles?.includes('ROLE_ADMIN');

    const menuItems = [
        {
            key: '/',
            icon: <DashboardOutlined />,
            label: <Link to="/">Dashboard</Link>,
        },
        {
            key: 'modules',
            label: 'Modules',
            type: 'group',
            children: [
                {
                    key: '/categories',
                    icon: <AppstoreOutlined />,
                    label: <Link to="/categories">Categories</Link>,
                },
                {
                    key: '/subcategories',
                    icon: <TagsOutlined />,
                    label: <Link to="/subcategories">SubCategories</Link>,
                },
                {
                    key: '/posts',
                    icon: <FileTextOutlined />,
                    label: <Link to="/posts">Blogs</Link>,
                },
            ]
        },
    ];

    // Add Admin-only items
    if (isAdmin) {
        menuItems.push({
            key: 'admin',
            label: 'Admin',
            type: 'group',
            children: [
                {
                    key: '/users',
                    icon: <TeamOutlined />,
                    label: <Link to="/users">User Management</Link>,
                }
            ]
        });
    }

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const userMenu = (
        <Menu items={[
            {
                key: 'profile',
                icon: <UserOutlined />,
                label: <Link to="/profile">Profile</Link>
            },
            {
                key: 'logout',
                icon: <LogoutOutlined />,
                label: 'Logout',
                onClick: handleLogout
            }
        ]} />
    );

    return (
        <Layout style={{ minHeight: '100vh', background: 'transparent' }}>
            <Sider
                collapsible
                collapsed={collapsed}
                onCollapse={(value) => setCollapsed(value)}
                style={{
                    boxShadow: '2px 0 8px rgba(0,0,0,0.1)',
                    zIndex: 10
                }}
            >
                <div style={{
                    height: 64,
                    margin: 16,
                    background: 'rgba(255, 255, 255, 0.1)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: 'white',
                    fontWeight: 'bold',
                    fontSize: 20,
                    letterSpacing: 2,
                    borderRadius: 8
                }}>
                    {collapsed ? 'B' : 'BLOOMS'}
                </div>
                <Menu theme="dark" defaultSelectedKeys={['/']} selectedKeys={[location.pathname]} mode="inline" items={menuItems} style={{ background: 'transparent' }} />
            </Sider>
            <Layout style={{ background: 'transparent' }}>
                <Header style={{ padding: '0 24px', background: colorBgContainer, display: 'flex', justifyContent: 'flex-end', alignItems: 'center', boxShadow: '0 2px 8px rgba(0,0,0,0.05)' }}>
                    <Dropdown overlay={userMenu} placement="bottomRight" arrow>
                        <Space style={{ cursor: 'pointer', padding: '4px 8px', borderRadius: 4, transition: 'all 0.3s' }}>
                            <Avatar size="large" icon={<UserOutlined />} src={currentUser?.profileImage} style={{ backgroundColor: '#6C63FF' }} />
                            <Typography.Text strong style={{ fontSize: 16 }}>{currentUser?.name || currentUser?.email}</Typography.Text>
                        </Space>
                    </Dropdown>
                </Header>
                <Content style={{ margin: '24px 16px', padding: 24, minHeight: 280 }}>
                    {/* Removed the white background container to let individual pages decide their container or let the body gradient show through */}
                    {/* But existing pages might expect a white background. Let's keep it but make it nicer. */}
                    {/* Actually, user wants a modern look. Letting the gradient show through and having cards on pages is better. */}
                    {/* However, for existing pages like Dashboard/Lists, they might look broken without a container. */}
                    {/* Let's render outlet directly and let pages handle their containers, OR wrap in a transparent helper. */}
                    {/* To be safe for existing pages, I will wrap Outlet in a div BUT if I want "Dashing" I should arguably remove the white box wrapper if pages have their own cards. */}
                    {/* Looking at UserList.jsx, it has a Table. Tables look best in Cards. */}
                    {/* I'll wrap existing content in a translucent card or just keep it simple. */}
                    {/* Let's keep the content wrapper but style it better. */}
                    <div style={{ padding: 24, minHeight: 360, background: 'rgba(255, 255, 255, 0.8)', borderRadius: borderRadiusLG, backdropFilter: 'blur(10px)', boxShadow: '0 4px 16px rgba(0,0,0,0.05)' }}>
                        <Outlet />
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center', background: 'transparent' }}>
                    Blooms Blog System ©{new Date().getFullYear()}
                </Footer>
            </Layout>
        </Layout>
    );
};

export default AppLayout;
