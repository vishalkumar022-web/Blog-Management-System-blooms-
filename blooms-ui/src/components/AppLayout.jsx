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
                key: 'logout',
                icon: <LogoutOutlined />,
                label: 'Logout',
                onClick: handleLogout
            }
        ]} />
    );

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible collapsed={collapsed} onCollapse={(value) => setCollapsed(value)}>
                <div style={{ height: 32, margin: 16, background: 'rgba(255, 255, 255, 0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontWeight: 'bold' }}>
                    {collapsed ? 'B' : 'BLOOMS'}
                </div>
                <Menu theme="dark" defaultSelectedKeys={['/']} selectedKeys={[location.pathname]} mode="inline" items={menuItems} />
            </Sider>
            <Layout>
                <Header style={{ padding: '0 24px', background: colorBgContainer, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                    <Dropdown overlay={userMenu} placement="bottomRight">
                        <Space style={{ cursor: 'pointer' }}>
                            <Avatar icon={<UserOutlined />} src={currentUser?.profileImage} />
                            <Typography.Text>{currentUser?.name || currentUser?.email}</Typography.Text>
                        </Space>
                    </Dropdown>
                </Header>
                <Content style={{ margin: '0 16px' }}>
                    <div style={{ padding: 24, minHeight: 360, background: colorBgContainer, borderRadius: borderRadiusLG, marginTop: 16 }}>
                        <Outlet />
                    </div>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    Blooms Blog System ©{new Date().getFullYear()}
                </Footer>
            </Layout>
        </Layout>
    );
};

export default AppLayout;
