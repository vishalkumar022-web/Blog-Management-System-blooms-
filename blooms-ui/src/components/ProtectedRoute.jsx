import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ allowedRoles }) => {
    const { currentUser } = useAuth();

    if (!currentUser) {
        return <Navigate to="/login" replace />;
    }

    // Role check
    // Assuming currentUser.role or currentUser.roles contains the role string like "ROLE_ADMIN" or "ADMIN"
    // Adjust based on your actual JWT payload/User object structure
    if (allowedRoles && allowedRoles.length > 0) {
        const userRoles = currentUser.roles || [currentUser.role]; // Handle array or single string
        const hasRole = allowedRoles.some(role => userRoles.includes(role));
        if (!hasRole) {
            return <Navigate to="/unauthorized" replace />; // Or redirect to dashboard
        }
    }

    return <Outlet />;
};

export default ProtectedRoute;
