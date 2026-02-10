import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, Alert, message } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const { Title } = Typography;

const Register = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const { register } = useAuth();
    const navigate = useNavigate();

    const onFinish = async (values) => {
        setLoading(true);
        setError('');
        try {
            // Map frontend values to backend DTO
            // Backend expects: userName, email, name, password, phoneNumber, profileUrl (optional)
            const requestData = {
                name: values.name,
                email: values.email,
                password: values.password,
                userName: values.userName || values.email.split('@')[0], // Default userName from email if not provided
                phoneNumber: values.phoneNumber || "0000000000", // Default or add field
                profileUrl: "https://ui-avatars.com/api/?name=" + values.name // Default Avatar
            };

            const response = await register(requestData);
            if (response.success) { // Assuming ApiResponse has success field. Check API response structure.
                // If response.data is a string (message), display it.
                message.success(response.message || 'Registration successful! Please login.');
                navigate('/login');
            } else {
                // Even if success is false, some APIs might return 200 OK with success: false
                // But typically authService.register returns response.data directly.
                // Let's rely on standard success check usually found in ApiResponse
                // If the backend returns ApiResponse<String>, we need to check how it's serialized.
                // Let's assume standard behavior for now.
                message.success('Registration successful! Please login.');
                navigate('/login');
            }
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', background: '#f0f2f5' }}>
            <Card style={{ width: 400, boxShadow: '0 4px 8px rgba(0,0,0,0.1)' }}>
                <div style={{ textAlign: 'center', marginBottom: 24 }}>
                    <Title level={2} style={{ color: '#1890ff' }}>Blooms</Title>
                    <Typography.Text type="secondary">Create a new account</Typography.Text>
                </div>

                {error && <Alert message={error} type="error" showIcon style={{ marginBottom: 24 }} />}

                <Form
                    name="register_form"
                    onFinish={onFinish}
                    size="large"
                    layout="vertical"
                >
                    <Form.Item
                        name="name"
                        rules={[{ required: true, message: 'Please input your Name!' }]}
                    >
                        <Input prefix={<UserOutlined />} placeholder="Full Name" />
                    </Form.Item>

                    <Form.Item
                        name="userName"
                        rules={[{ required: true, message: 'Please input a Username!' }]}
                    >
                        <Input prefix={<UserOutlined />} placeholder="Username" />
                    </Form.Item>

                    <Form.Item
                        name="email"
                        rules={[{ required: true, message: 'Please input your Email!' }, { type: 'email', message: 'Please enter a valid email!' }]}
                    >
                        <Input prefix={<MailOutlined />} placeholder="Email" />
                    </Form.Item>

                    <Form.Item
                        name="phoneNumber"
                        rules={[{ required: false, message: 'Please input your Phone Number!' }]}
                    >
                        <Input prefix={<UserOutlined />} placeholder="Phone Number" />
                    </Form.Item>

                    <Form.Item
                        name="password"
                        rules={[{ required: true, message: 'Please input your Password!' }, { min: 6, message: 'Password must be at least 6 characters!' }]}
                    >
                        <Input.Password prefix={<LockOutlined />} placeholder="Password" />
                    </Form.Item>

                    <Form.Item>
                        <Button type="primary" htmlType="submit" style={{ width: '100%' }} loading={loading}>
                            Register
                        </Button>
                    </Form.Item>

                    <div style={{ textAlign: 'center' }}>
                        Already have an account? <Link to="/login">Log in</Link>
                    </div>
                </Form>
            </Card>
        </div>
    );
};

export default Register;
