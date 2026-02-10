import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Popconfirm, message, Modal, Form, Input, Select } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { getSubCategories, createSubCategory, updateSubCategory, deleteSubCategory, getCategories } from '../services/apiService';

const SubCategoryList = () => {
    const [subCategories, setSubCategories] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentSubCategory, setCurrentSubCategory] = useState(null);
    const [form] = Form.useForm();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [subCatRes, catRes] = await Promise.all([getSubCategories(), getCategories()]);
            if (subCatRes.data.success) setSubCategories(subCatRes.data.data);
            if (catRes.data.success) setCategories(catRes.data.data);
        } catch (error) {
            message.error('Failed to fetch data');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteSubCategory(id);
            message.success('SubCategory deleted');
            fetchData(); // Refresh
        } catch (error) {
            message.error('Failed to delete');
        }
    };

    const handleAdd = () => {
        setCurrentSubCategory(null);
        form.resetFields();
        setIsModalVisible(true);
    };

    const handleEdit = (record) => {
        setCurrentSubCategory(record);
        // Ensure categoryId is set correctly for Select
        form.setFieldsValue({
            ...record,
            categoryId: record.category ? record.category.categoryId : undefined
        });
        setIsModalVisible(true);
    };

    const handleModalOk = async () => {
        try {
            const values = await form.validateFields();
            if (currentSubCategory) {
                await updateSubCategory({ ...currentSubCategory, ...values });
                message.success('SubCategory updated');
            } else {
                await createSubCategory(values);
                message.success('SubCategory created');
            }
            setIsModalVisible(false);
            fetchData();
        } catch (error) {
            message.error('Failed to save subcategory');
        }
    };

    const columns = [
        { title: 'Title', dataIndex: 'title', key: 'title' },
        { title: 'Description', dataIndex: 'description', key: 'description' },
        {
            title: 'Category',
            dataIndex: 'category',
            key: 'category',
            render: (cat) => cat ? cat.title : '-'
        },
        {
            title: 'Actions',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button icon={<EditOutlined />} onClick={() => handleEdit(record)}>Edit</Button>
                    <Popconfirm title="Delete?" onConfirm={() => handleDelete(record.subCategoryId)}>
                        <Button icon={<DeleteOutlined />} danger>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <h2>SubCategory Management</h2>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>Add SubCategory</Button>
            </div>
            <Table columns={columns} dataSource={subCategories} rowKey="subCategoryId" loading={loading} />

            <Modal title={currentSubCategory ? "Edit" : "Add"} open={isModalVisible} onOk={handleModalOk} onCancel={() => setIsModalVisible(false)}>
                <Form form={form} layout="vertical">
                    <Form.Item name="title" label="Title" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="description" label="Description">
                        <Input.TextArea />
                    </Form.Item>
                    <Form.Item name="categoryId" label="Category" rules={[{ required: true }]}>
                        <Select>
                            {categories.map(cat => (
                                <Select.Option key={cat.categoryId} value={cat.categoryId}>{cat.title}</Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default SubCategoryList;
