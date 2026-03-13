package in.vishal.blooms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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

        // ✅ 1. Request me se Token nikalo
        String authHeader = request.getHeader("Authorization");

        // ✅ 2. Agar token nahi hai, toh chup-chap aage bhej do.
        // (Daro mat! Agar ye private URL hogi, toh SecurityConfig isko khud aage jaakar laat maar dega. Public hogi toh nikal jayega.)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // ✅ 3. Token ki Validity Check karo
        if (jwtUtil.isTokenValid(token)) {

            // Token me se User ka ID aur Role nikalo
            String userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // ✅ 4. SPRING SECURITY KO BATAO KI YE BANDA ASLI HAI! (The VIP Register)
            // Agar SecurityContextHolder (VIP Register) me iska naam nahi likha hai, toh likh do.
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Role ko authority me badalna taaki Spring Security ko samajh aaye (jaise "admin" ya "user")
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                // Authentication object banana (Ye banda ab officially authenticated hai)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.singletonList(authority));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // VIP Register me Entry pakki kar di! Ab SecurityConfig isko nahi rokega.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ✅ 5. Sab check ho gaya, ab request ko aage processing ke liye bhej do
        filterChain.doFilter(request, response);
    }
}