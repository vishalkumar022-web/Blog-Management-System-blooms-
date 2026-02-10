import React, { createContext, useState, useEffect, useContext } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const checkLoggedIn = () => {
            const user = JSON.parse(localStorage.getItem('user'));
            const token = authService.getToken();

            if (user && token) {
                // Determine role from user object or token decoding
                // Assuming the user object has a 'roles' array or 'role' string
                // If not, you might need to decode the token here
                const decodedUser = authService.getUserFromToken();
                // Merge decoded info if needed, but primary source is what we stored on login
                setCurrentUser({ ...user, ...decodedUser });
            }
            setLoading(false);
        };
        checkLoggedIn();
    }, []);

    const login = async (userData) => {
        const response = await authService.login(userData);
        if (response.success && response.data) {
            const user = response.data;
            // Ensure role is present. If it's in the token, we extracted it in authService or will decode it
            const decoded = authService.getUserFromToken();
            setCurrentUser({ ...user, ...decoded });
            return user;
        }
        throw new Error(response.message || "Login failed");
    };

    const logout = () => {
        authService.logout();
        setCurrentUser(null);
    };

    const register = async (userData) => {
        return await authService.register(userData);
    };

    return (
        <AuthContext.Provider value={{ currentUser, login, logout, register, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
