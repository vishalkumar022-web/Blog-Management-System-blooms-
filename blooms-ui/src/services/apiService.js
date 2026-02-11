import axios from 'axios';

const API_URL = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add the auth token header
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// --- Category Service ---
export const getCategories = () => api.get('/Category/all'); // Public
export const createCategory = (data) => api.post('/Category', data);
export const updateCategory = (data) => api.put('/Category', data);
export const deleteCategory = (id) => api.delete(`/Category?categoryId=${id}`);
export const updateCategoryStatus = (id, status) => api.put(`/Admin/categories/UpdateStatus?categoryId=${id}&status=${status}`); // Admin

// --- SubCategory Service ---
export const getSubCategories = () => api.get('/SubCategory/all');
export const createSubCategory = (data) => api.post('/SubCategory', data);
export const updateSubCategory = (data) => api.put('/SubCategory', data);
export const deleteSubCategory = (id) => api.delete(`/SubCategory?subCategoryId=${id}`);

// --- Blog Service ---
export const getBlogs = () => api.get('/BLog');
export const createBlog = (data) => api.post('/BLog', data);
export const updateBlog = (data) => api.put('/BLog', data);
export const deleteBlog = (id) => api.delete(`/BLog?blogId=${id}`);

// --- User Service (Admin) ---
export const getAllUsers = () => api.get('/Admin/users/all');
export const getUserById = (id) => api.get(`/Admin/users/user_id?userId=${id}`);
export const deleteUser = (id) => api.delete(`/Admin/usersById?userId=${id}`);

// --- Dashboard ---
// stats fetching is handled in component or we can add specific aggregations here if backend supports

// --- User Service (Self) ---
export const updateSelf = (data) => api.put('/User', data);
export const deleteSelf = (id) => api.delete(`/User?userId=${id}`);

// --- Forgot Password Service ---
export const forgotPassword = (email) => api.post('/auth/forgot-password', { email });
export const verifyOtp = (email, otp) => api.post('/auth/verify-otp', { email, otp });
export const resetPassword = (email, newPassword) => api.post('/auth/reset-password', { email, newPassword });

export default api;
