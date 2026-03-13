package in.vishal.blooms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ JADU YAHAN HAI: Dono Guards (401 aur 403) ko unka message sikha diya
                .exceptionHandling(ex -> ex

                        // GUARD 1: Jab Token na ho ya galat ho (401 UNAUTHORIZED)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Missing or Invalid Authorization header\", \"data\": null}");
                        })

                        // GUARD 2: Jab Token sahi ho par Role Admin na ho (403 FORBIDDEN)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            // Tumhara manpasand purana message yahan aayega!
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Access Denied: Only Admins can access this area!\", \"data\": null}");
                        })
                )

                // ✅ BAAKI SAARI URL ROUTING EKDUM PERFECT HAI
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/BLog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/Category/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/SubCategory/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/User/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/followers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/following").permitAll()

                        // Yahan Spring check karega ki role "admin" hai ya nahi
                        .requestMatchers("/api/Admin/**").hasAuthority("admin")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}