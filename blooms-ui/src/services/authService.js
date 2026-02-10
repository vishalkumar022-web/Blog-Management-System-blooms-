import axios from 'axios';

const API_URL = 'http://localhost:8081/api/auth';

// Helper to set token
const setToken = (token) => {
    if (token) {
        localStorage.setItem('token', token);
    } else {
        localStorage.removeItem('token');
    }
};

// Helper to get token
export const getToken = () => {
    return localStorage.getItem('token');
};

// Helper to decode token (basic decoding to get role/user info if payload allows)
// Note: For better security, use a library like jwt-decode, but for now we'll rely on backend response
export const getUserFromToken = () => {
    const token = getToken();
    if (!token) return null;
    try {
        // Basic payload decode if needed, or rely on stored user object
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (error) {
        return null;
    }
};


const register = async (userData) => {
    const response = await axios.post(`${API_URL}/register`, userData);
    return response.data;
};

const login = async (userData) => {
    const response = await axios.post(`${API_URL}/login`, userData);
    if (response.data.success) {
        if (response.data.data && response.data.data.token) {
            setToken(response.data.data.token);
            localStorage.setItem('user', JSON.stringify(response.data.data));
        }
        return response.data;
    } else {
        throw new Error(response.data.message || 'Login failed');
    }
};

const logout = () => {
    setToken(null);
    localStorage.removeItem('user');
    window.location.href = '/login';
};

const authService = {
    register,
    login,
    logout,
    getToken,
    getUserFromToken
};

export default authService;
