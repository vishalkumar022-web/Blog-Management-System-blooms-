import React, { useEffect, useState } from 'react';
import { Card, Col, Row, Statistic, Spin, Alert } from 'antd';
import { FileTextOutlined, MessageOutlined, LikeOutlined, UserOutlined, TeamOutlined, TagsOutlined } from '@ant-design/icons';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

const Dashboard = () => {
    const { currentUser } = useAuth();
    const [stats, setStats] = useState({
        totalPosts: 0,
        totalComments: 0,
        totalLikes: 0,
        totalUsers: 0,
        totalCategories: 0
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchStats();
    }, []);

    const fetchStats = async () => {
        try {
            // Check if backend has a dedicated stats endpoint. 
            // Based on controller analysis, there isn't one directly.
            // We can aggregation by fetching counts from lists if paginated metadata is available, 
            // or just fetch list size (not efficient for large data but okay for start).

            // Let's try to fetch lists.
            // Admin can see everything. User can see public blogs.

            const [postsRes, usersRes, categoriesRes] = await Promise.allSettled([
                axios.get('http://localhost:8081/api/BLog?size=1'), // Just one to check totalElements if available in Page object
                currentUser?.role === 'ADMIN' ? axios.get('http://localhost:8081/api/Admin/users/all?size=1') : Promise.resolve({ data: { data: [] } }),
                axios.get('http://localhost:8081/api/Category/all?size=1')
            ]);

            const newStats = { ...stats };

            if (postsRes.status === 'fulfilled' && postsRes.value.data.success) {
                // Assuming Spring Data Page object structure in response.data.data
                // If backend returns List, use length. If Page, use totalElements
                const data = postsRes.value.data.data;
                newStats.totalPosts = data.totalElements !== undefined ? data.totalElements : (Array.isArray(data) ? data.length : 0);
            }

            if (usersRes.status === 'fulfilled' && usersRes.value.data && usersRes.value.data.success) {
                const data = usersRes.value.data.data;
                newStats.totalUsers = data.totalElements !== undefined ? data.totalElements : (Array.isArray(data) ? data.length : 0);
            }

            if (categoriesRes.status === 'fulfilled' && categoriesRes.value.data.success) {
                const data = categoriesRes.value.data.data;
                newStats.totalCategories = data.totalElements !== undefined ? data.totalElements : (Array.isArray(data) ? data.length : 0);
            }

            setStats(newStats);

        } catch (error) {
            console.error("Failed to fetch dashboard stats", error);
            setError("Could not load some dashboard statistics.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <Spin size="large" style={{ display: 'block', margin: 'auto', marginTop: 50 }} />;

    const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.roles?.includes('ROLE_ADMIN');

    return (
        <div>
            <h2>Dashboard</h2>
            <p>Welcome, {currentUser?.name || currentUser?.userName || 'User'}!</p>
            {error && <Alert message={error} type="warning" showIcon style={{ marginBottom: 24 }} />}

            <Row gutter={16}>
                <Col span={8}>
                    <Card>
                        <Statistic
                            title="Total Blogs"
                            value={stats.totalPosts}
                            prefix={<FileTextOutlined />}
                            valueStyle={{ color: '#3f8600' }}
                        />
                    </Card>
                </Col>
                <Col span={8}>
                    <Card>
                        <Statistic
                            title="Total Categories"
                            value={stats.totalCategories}
                            prefix={<TagsOutlined />}
                            valueStyle={{ color: '#1890ff' }}
                        />
                    </Card>
                </Col>
                <Col span={8}>
                    <Card>
                        <Statistic
                            title="Total Likes (Est.)"
                            value={stats.totalLikes}
                            prefix={<LikeOutlined />}
                            valueStyle={{ color: '#cf1322' }}
                        />
                    </Card>
                </Col>
            </Row>

            {isAdmin && (
                <Row gutter={16} style={{ marginTop: 24 }}>
                    <Col span={8}>
                        <Card>
                            <Statistic
                                title="Total Users"
                                value={stats.totalUsers}
                                prefix={<TeamOutlined />}
                            />
                        </Card>
                    </Col>
                </Row>
            )}
        </div>
    );
};

export default Dashboard;
