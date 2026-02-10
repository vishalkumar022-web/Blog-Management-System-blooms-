import React, { useEffect, useState } from 'react';
import { Table, Button, Space, Popconfirm, message, Modal, Form, Input, Select } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { getBlogs, createBlog, updateBlog, deleteBlog, getCategories, getSubCategories } from '../services/apiService';
import { useAuth } from '../context/AuthContext';

const BlogList = () => {
    const [blogs, setBlogs] = useState([]);
    const [categories, setCategories] = useState([]);
    const [subCategories, setSubCategories] = useState([]); // All subcats
    const [filteredSubCats, setFilteredSubCats] = useState([]); // Filtered by selected cat
    const [loading, setLoading] = useState(false);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [currentBlog, setCurrentBlog] = useState(null);
    const [form] = Form.useForm();
    const { currentUser } = useAuth();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [blogsRes, catRes, subCatRes] = await Promise.all([getBlogs(), getCategories(), getSubCategories()]);
            if (blogsRes.data.success) setBlogs(blogsRes.data.data);
            if (catRes.data.success) setCategories(catRes.data.data);
            if (subCatRes.data.success) setSubCategories(subCatRes.data.data);
        } catch (error) {
            message.error('Failed to fetch data');
        } finally {
            setLoading(false);
        }
    };

    const handleCategoryChange = (categoryId) => {
        const filtered = subCategories.filter(sc => sc.category && sc.category.categoryId === categoryId);
        setFilteredSubCats(filtered);
        form.setFieldsValue({ subCategoryId: undefined }); // Reset subcat selection
    };

    const handleDelete = async (id) => {
        try {
            await deleteBlog(id);
            message.success('Blog deleted');
            fetchData();
        } catch (error) {
            message.error('Failed to delete blog');
        }
    };

    const handleAdd = () => {
        setCurrentBlog(null);
        form.resetFields();
        setFilteredSubCats([]);
        setIsModalVisible(true);
    };

    const handleEdit = (record) => {
        setCurrentBlog(record);
        // Pre-filter subcategories based on current blog's category
        if (record.category) {
            const filtered = subCategories.filter(sc => sc.category && sc.category.categoryId === record.category.categoryId);
            setFilteredSubCats(filtered);
        }

        form.setFieldsValue({
            ...record,
            categoryId: record.category?.categoryId,
            subCategoryId: record.subCategory?.subCategoryId
        });
        setIsModalVisible(true);
    };

    const handleModalOk = async () => {
        try {
            const values = await form.validateFields();
            const payload = {
                ...values,
                user: { userId: currentUser.userId || currentUser.id }, // Associate current user
            };

            if (currentBlog) {
                await updateBlog({ ...currentBlog, ...payload });
                message.success('Blog updated');
            } else {
                await createBlog(payload);
                message.success('Blog created');
            }
            setIsModalVisible(false);
            fetchData();
        } catch (error) {
            console.error(error);
            message.error('Failed to save blog');
        }
    };

    const columns = [
        { title: 'Title', dataIndex: 'title', key: 'title' },
        { title: 'Content', dataIndex: 'content', key: 'content', ellipsis: true },
        { title: 'Category', dataIndex: ['category', 'title'], key: 'category' },
        { title: 'SubCategory', dataIndex: ['subCategory', 'title'], key: 'subCategory' },
        {
            title: 'Actions',
            key: 'action',
            render: (_, record) => (
                <Space size="middle">
                    <Button icon={<EditOutlined />} onClick={() => handleEdit(record)}>Edit</Button>
                    <Popconfirm title="Delete?" onConfirm={() => handleDelete(record.blogId)}>
                        <Button icon={<DeleteOutlined />} danger>Delete</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <h2>Blog Management</h2>
                <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>Add Blog</Button>
            </div>
            <Table columns={columns} dataSource={blogs} rowKey="blogId" loading={loading} />

            <Modal title={currentBlog ? "Edit Blog" : "Add Blog"} open={isModalVisible} onOk={handleModalOk} onCancel={() => setIsModalVisible(false)} width={800}>
                <Form form={form} layout="vertical">
                    <Form.Item name="title" label="Title" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="content" label="Content" rules={[{ required: true }]}>
                        <Input.TextArea rows={6} />
                    </Form.Item>
                    <Form.Item name="categoryId" label="Category" rules={[{ required: true }]}>
                        <Select onChange={handleCategoryChange}>
                            {categories.map(cat => (
                                <Select.Option key={cat.categoryId} value={cat.categoryId}>{cat.title}</Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                    <Form.Item name="subCategoryId" label="SubCategory" rules={[{ required: true }]}>
                        <Select disabled={!filteredSubCats.length}>
                            {filteredSubCats.map(sc => (
                                <Select.Option key={sc.subCategoryId} value={sc.subCategoryId}>{sc.title}</Select.Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default BlogList;
