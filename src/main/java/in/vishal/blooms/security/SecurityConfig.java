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

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Missing or Invalid Authorization header\", \"data\": null}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Access Denied: Only Admins can access this area!\", \"data\": null}");
                        })
                )

                .authorizeHttpRequests(auth -> auth
                        // ✅ JADU YAHAN HAI: Swagger ki saari files aur main HTML page ko properly ALLOW kar diya
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/ws/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/BLog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/Category/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/SubCategory/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/User/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/followers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/following").permitAll()

                        .requestMatchers("/api/Admin/**").hasAuthority("admin")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}