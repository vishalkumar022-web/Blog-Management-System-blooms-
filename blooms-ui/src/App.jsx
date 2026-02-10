import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import AppLayout from './components/AppLayout';
import Dashboard from './pages/Dashboard';
import CategoryList from './pages/CategoryList';
import SubCategoryList from './pages/SubCategoryList';
import BlogList from './pages/BlogList';
import UserList from './pages/UserList';
import Login from './pages/Login';
import Register from './pages/Register';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

const App = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
        },
      }}
    >
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<AppLayout />}>
                <Route index element={<Dashboard />} />

                {/* Module Routes */}
                <Route path="categories" element={<CategoryList />} />
                <Route path="subcategories" element={<SubCategoryList />} />
                <Route path="posts" element={<BlogList />} />

                {/* Admin Only Route */}
                {/* Ideally ProtectedRoute should accept roles, but for simplicity we rely on UserList's internal check or UI hiding 
                        To be stricter: <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}> ... </Route>
                    */}
                <Route path="users" element={<UserList />} />
              </Route>
            </Route>

            {/* Catch all */}
            <Route path="*" element={<Navigate to="/" replace />} />

          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  );
};

export default App;
