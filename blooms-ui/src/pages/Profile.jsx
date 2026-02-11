import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Card, Avatar, Typography, Divider, Space, message, Popconfirm, Tag, Row, Col } from 'antd';
import { UserOutlined, EditOutlined, SaveOutlined, DeleteOutlined, LogoutOutlined, MailOutlined, PhoneOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { useAuth } from '../context/AuthContext';
import { updateSelf, deleteSelf } from '../services/apiService';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;

const Profile = () => {
    const { currentUser, login, logout } = useAuth();
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();

    useEffect(() => {
        if (currentUser) {
            form.setFieldsValue(currentUser);
        }
    }, [currentUser, form]);

    const handleUpdate = async (values) => {
        setLoading(true);
        try {
            const updateData = { ...currentUser, ...values };
            const response = await updateSelf(updateData);
            if (response.data.success) {
                message.success('Profile updated successfully');
                setIsEditing(false);
                const updatedUser = response.data.data;
                localStorage.setItem('user', JSON.stringify(updatedUser));
                window.location.reload();
            } else {
                message.error(response.data.message || 'Update failed');
            }
        } catch (error) {
            message.error('Failed to update profile');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteAccount = async () => {
        try {
            await deleteSelf(currentUser.userId);
            message.success('Account deleted successfully');
            logout();
            navigate('/login');
        } catch (error) {
            message.error('Failed to delete account');
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    if (loading) {
        return <div style={{ textAlign: 'center', padding: 50 }}>Loading profile...</div>;
    }

    if (!currentUser) {
        return <div style={{ textAlign: 'center', padding: 50 }}>User not found. Please log in again.</div>;
    }

    return (
        <div style={{ maxWidth: 800, margin: '0 auto', padding: '24px 0' }}>
            <Card
                style={{
                    borderRadius: 16,
                    boxShadow: '0 10px 30px rgba(0,0,0,0.1)',
                    border: 'none',
                    overflow: 'hidden'
                }}
                bodyStyle={{ padding: 0 }}
            >
                {/* Header Section */}
                <div style={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    padding: '40px 20px',
                    textAlign: 'center',
                    color: 'white'
                }}>
                    <Avatar
                        size={120}
                        icon={<UserOutlined />}
                        src={currentUser?.profileImage}
                        style={{
                            border: '4px solid white',
                            boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
                            marginBottom: 16,
                            backgroundColor: '#f0f2f5',
                            color: '#764ba2'
                        }}
                    />
                    <Title level={2} style={{ color: 'white', margin: 0 }}>{currentUser?.name}</Title>
                    <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: 16 }}>@{currentUser?.userName}</Text>
                    <div style={{ marginTop: 12 }}>
                        <Tag color="gold" style={{ padding: '4px 12px', fontSize: 14, borderRadius: 12 }}>
                            {currentUser?.role || 'USER'}
                        </Tag>
                    </div>
                </div>

                <div style={{ padding: 40 }}>
                    <Form
                        form={form}
                        layout="vertical"
                        onFinish={handleUpdate}
                        disabled={!isEditing}
                        initialValues={currentUser}
                        requiredMark={false}
                    >
                        <Row gutter={24}>
                            <Col span={24} md={12}>
                                <Form.Item label="Full Name" name="name" rules={[{ required: true, message: 'Please input your name!' }]}>
                                    <Input prefix={<UserOutlined style={{ color: '#bfbfbf' }} />} size="large" />
                                </Form.Item>
                            </Col>
                            <Col span={24} md={12}>
                                <Form.Item label="Phone Number" name="phoneNumber">
                                    <Input prefix={<PhoneOutlined style={{ color: '#bfbfbf' }} />} size="large" />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Row gutter={24}>
                            <Col span={24} md={12}>
                                <Form.Item label="Username" name="userName">
                                    <Input prefix={<SafetyCertificateOutlined style={{ color: '#bfbfbf' }} />} disabled size="large" style={{ backgroundColor: '#f9f9f9', color: '#999' }} />
                                </Form.Item>
                            </Col>
                            <Col span={24} md={12}>
                                <Form.Item label="Email Address" name="email">
                                    <Input prefix={<MailOutlined style={{ color: '#bfbfbf' }} />} disabled size="large" style={{ backgroundColor: '#f9f9f9', color: '#999' }} />
                                </Form.Item>
                            </Col>
                        </Row>

                        <Divider style={{ margin: '24px 0' }} />

                        {isEditing ? (
                            <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                                <Space>
                                    <Button size="large" onClick={() => setIsEditing(false)}>
                                        Cancel
                                    </Button>
                                    <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={loading} size="large" style={{ padding: '0 32px' }}>
                                        Save Changes
                                    </Button>
                                </Space>
                            </Form.Item>
                        ) : (
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <Button type="primary" ghost icon={<EditOutlined />} onClick={() => setIsEditing(true)} size="large">
                                    Edit Profile
                                </Button>

                                <Space>
                                    <Button type="text" danger icon={<LogoutOutlined />} onClick={handleLogout} size="large">
                                        Logout
                                    </Button>
                                    <Popconfirm
                                        title="Delete Account"
                                        description="Are you sure? This action is permanent."
                                        onConfirm={handleDeleteAccount}
                                        okText="Delete"
                                        cancelText="Cancel"
                                        okButtonProps={{ danger: true }}
                                    >
                                        <Button type="primary" danger icon={<DeleteOutlined />} size="large">
                                            Delete Account
                                        </Button>
                                    </Popconfirm>
                                </Space>
                            </div>
                        )}
                    </Form>
                </div>
            </Card>
        </div>
    );
};

export default Profile;
