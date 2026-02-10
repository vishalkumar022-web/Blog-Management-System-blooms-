import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Popconfirm, message, Modal, Form, Input, Switch, Tag } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const CategoryList = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentCategory, setCurrentCategory] = useState(null);
    const [form] = Form.useForm();
    const { currentUser } = useAuth();

    // Determine Role
    const isAdmin = currentUser?.role === 'ADMIN' || currentUser?.roles?.includes('ROLE_ADMIN');

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        setLoading(true);
        try {
            // Admin can see everything via different endpoint if needed, but usually 'all' works for listing
            // From controller: /api/Category/all (Public) or /api/Admin/categories/all (Admin)
            // Let's use public one for now as it returns List<CategoryResponse>
            const response = await axios.get('http://localhost:8081/api/Category/all');
            if (response.data.success) {
                setCategories(response.data.data);
            }
        } catch (error) {
            message.error('Failed to fetch categories');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            // Admin endpoint or public endpoint if user owns it (usually categories are admin/public managed)
            // Controller: DELETE /api/Category?categoryId=...
            await axios.delete(`http://localhost:8081/api/Category?categoryId=${id}`);
            message.success('Category deleted successfully');
            fetchCategories();
        } catch (error) {
            message.error('Failed to delete category');
        }
    };

    const handleStatusChange = async (id, checked) => {
        try {
            const status = checked ? 'ACTIVE' : 'INACTIVE';
            await axios.put(`http://localhost:8081/api/Admin/categories/UpdateStatus?categoryId=${id}&status=${status}`);
            message.success('Status updated');
            fetchCategories();
        } catch (error) {
            message.error('Failed to update status');
        }
    };

    const handleAdd = () => {
        setCurrentCategory(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const handleEdit = (record) => {
        setCurrentCategory(record);
        form.setFieldsValue(record);
        setIsModalVisible(true);
    };

    const handleModalOk = async () => {
        try {
            const values = await form.validateFields();
            if (currentCategory) {
                // Update: PUT /api/Category
                await axios.put('http://localhost:8081/api/Category', { ...currentCategory, ...values });
                message.success('Category updated');
            } else {
                // Create: POST /api/Category
                await axios.post('http://localhost:8081/api/Category', values);
                message.success('Category created');
            }
            setIsModalVisible(false);
            fetchCategories();
        } catch (error) {
            message.error('Failed to save category');
        }
    };

    const columns = [
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
        },
        {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
        },
        {
            title: 'Cover Image',
            dataIndex: 'coverImage',
            key: 'coverImage',
            render: (text) => text ? <img src={text} alt="cover" style={{ width: 50, height: 50, objectFit: 'cover' }} /> : 'No Image'
        },
        // Only Admin usually sees/edits status or performs actions if specific rules apply
        ...(isAdmin ? [{
            title: 'Status',
            key: 'status', // Assuming 'isActive' or 'status' field exists. If boolean use switch.
            render: (_, record) => (
                <Switch
                    checked={record.status === 'ACTIVE' || record.isActive}
                    onChange={(checked) => handleStatusChange(record.id || record.categoryId, checked)}
                />
            )
        }] : []),
        {
            title: 'Actions',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button icon={<EditOutlined />} onClick={() => handleEdit(record)}>Edit</Button>
                    <Popconfirm
                        title="Delete the category"
                        onConfirm={() => handleDelete(record.id || record.categoryId)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button icon={<DeleteOutlined />} danger>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2>Category Management</h2>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                    Add Category
                </Button>
            </div>
            <Table
                columns={columns}
                dataSource={categories}
                rowKey={(record) => record.id || record.categoryId}
                loading={loading}
            />
            <Modal
                title={currentCategory ? "Edit Category" : "Add Category"}
                open={isModalVisible}
                onOk={handleModalOk}
                onCancel={() => setIsModalVisible(false)}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="title" label="Title" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="description" label="Description">
                        <Input.TextArea />
                    </Form.Item>
                    <Form.Item name="coverImage" label="Cover Image URL">
                        <Input />
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CategoryList;
