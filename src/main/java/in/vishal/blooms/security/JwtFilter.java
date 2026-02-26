package in.vishal.blooms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // 1️⃣ PUBLIC URLS (Login, Register, Swagger) - Inpe koi rok-tok nahi
        if (path.startsWith("/api/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || (request.getMethod().equals("GET") && path.startsWith("/api/BLog"))
                || (request.getMethod().equals("GET") && path.startsWith("/api/Category/all"))
                || (request.getMethod().equals("GET") && path.startsWith("/api/SubCategory/all"))
                || (request.getMethod().equals("GET") && path.startsWith("/api/User/search"))
                || (request.getMethod().equals("GET") && path.startsWith("/api/connection/followers"))
                || (request.getMethod().equals("GET") && path.startsWith("/api/connection/following"))) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // 2️⃣ TOKEN CHECK: Token hai ya nahi?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Missing Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // 3️⃣ VALIDITY CHECK: Token expire toh nahi hua?
        if (!jwtUtil.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.getWriter().write("Invalid or expired token");
            return;
        }

        // =================================================================
        // 🚀 ROLE BASED ACCESS CONTROL (RBAC) - Modified Logic
        // =================================================================

        // Token se Role nikalo (e.g., "admin" ya "user")
        String role = jwtUtil.extractRole(token);

        // LOGIC: Sirf Admin Area ("/api/Admin") ko protect karna hai.

        if (path.startsWith("/api/Admin")) {
            // Agar koi /api/Admin access kar raha hai, toh check karo wo ADMIN hai ya nahi.
            if (role == null || !role.equalsIgnoreCase("admin")) {

                // Agar wo Admin nahi hai (User hai), toh yahin rok do.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                response.getWriter().write("Access Denied: Only Admins can access this area!");
                return;
            }
        }

        // NOTE: Agar path "/api/User" ya "/api/BLog" hai, toh hum check nahi laga rahe.
        // Iska matlab:
        // - Agar User aayega -> Access milega.
        // - Agar Admin aayega -> Access milega (Kyunki Admin ko sab allowed hai).

        // =================================================================

        // ✅ Sab sahi hai -> Request aage jane do
        filterChain.doFilter(request, response);
    }
}