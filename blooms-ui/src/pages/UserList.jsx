import React, { useEffect, useState } from 'react';
import { Table, Button, Popconfirm, message, Space, Tag } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { getAllUsers, deleteUser } from '../services/apiService';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const { currentUser } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        // Double check admin role
        const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.roles?.includes('ROLE_ADMIN');
        if (!isAdmin) {
            navigate('/');
            return;
        }
        fetchUsers();
    }, [currentUser, navigate]);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await getAllUsers();
            if (response.data.success) {
                setUsers(response.data.data);
            }
        } catch (error) {
            message.error('Failed to fetch users');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteUser(id);
            message.success('User deleted');
            fetchUsers();
        } catch (error) {
            message.error('Failed to delete user');
        }
    };

    const columns = [
        { title: 'Name', dataIndex: 'name', key: 'name' },
        { title: 'Username', dataIndex: 'userName', key: 'userName' },
        { title: 'Email', dataIndex: 'email', key: 'email' },
        { title: 'Phone', dataIndex: 'phoneNumber', key: 'phoneNumber' },
        {
            title: 'Role',
            dataIndex: 'role',
            key: 'role',
            render: (role) => <Tag color={role === 'ADMIN' ? 'red' : 'blue'}>{role}</Tag>
        },
        {
            title: 'Actions',
            key: 'action',
            render: (_, record) => (
                <Space>
                    <Popconfirm title="Delete User?" onConfirm={() => handleDelete(record.userId)}>
                        <Button icon={<DeleteOutlined />} danger disabled={record.role === 'ADMIN'}>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <h2>User Management</h2>
            <Table columns={columns} dataSource={users} rowKey="userId" loading={loading} />
        </div>
    );
};

export default UserList;
