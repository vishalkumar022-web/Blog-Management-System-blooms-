package in.vishal.blooms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity // Ye annotation Spring ko batata hai ki "Bhai, Security rules yahan likhe hain, inko follow karna"
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter; // Ye hamara banaya hua guard (Filter) hai jo har aane wali request ka Token check karega

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. CSRF Disable: Kyunki hum JWT use kar rahe hain (jo apne aap me secure hai), toh hamein Spring ki default CSRF security nahi chahiye.
        http.csrf(csrf -> csrf.disable())

                // 2. CORS: Ye rule Frontend (React) ko Backend se data lene ki permission deta hai (jo humne WebConfig me set kiya tha).
                .cors(Customizer.withDefaults())

                // 3. Session Management: 'STATELESS' ka matlab hai ki server kisi ko yaad nahi rakhega. Har baar aane par naya/valid token dikhana padega.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Exception Handling: Agar kisi ke paas Token nahi hai, ya galat power (Role) hai, toh usko kya error message dena hai.
                .exceptionHandling(ex -> ex
                        // A. Jab bina Token (login) wala banda kisi aisi chiz ko chuyega jo private hai:
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Missing or Invalid Authorization header\", \"data\": null}");
                        })
                        // B. Jab User login toh hai, par uske paas Admin wali power nahi hai:
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"success\": false, \"errorMessage\": \"Access Denied: Only Admins can access this area!\", \"data\": null}");
                        })
                )

                // 5. AUTHORIZATION RULES: Yahan tay hota hai ki kaun kis URL (Raste) par jaa sakta hai.
                .authorizeHttpRequests(auth -> auth

                        // 🟢 GROUP 1: FULLY PUBLIC (Bina Login Ke Allow)
                        .requestMatchers(
                                "/",                    // ✅ ADDED: Sirf "/" add kiya hai taaki direct link kholne par wo ganda error na aaye.
                                "/api/auth/**",         // Login aur Register wale pages pe toh token ho hi nahi sakta, isliye ye public hai.
                                "/v3/api-docs/**",      // Swagger documentation files
                                "/swagger-ui/**",       // Swagger UI design files
                                "/swagger-ui.html",     // Swagger Main Page
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll() // permitAll() ka matlab: "Jisko aana hai aane do, Token mat maango"

                        // WebSocket (Chat) connection public kiya hai (baad me token yahan bhi lagta hai but abhi thik hai)
                        .requestMatchers("/ws/**").permitAll()

                        // 🟡 GROUP 2: GET ONLY PUBLIC (List dekhna free hai)
                        // KYA BADLA HAI? Kuch nahi, tumne already ekdum sahi kiya tha.
                        // KYU SAHI HAI? Kyunki yahan 'HttpMethod.GET' likha hai.
                        // KAISE KAAM KARTA HAI? Iska matlab koi bhi user "/api/BLog" se data DEKH (GET) sakta hai bina login ke.
                        // Lekin agar wo CREATE (POST), UPDATE (PUT), ya DELETE karna chahega, toh ye line usko permit nahi karegi.
                        .requestMatchers(HttpMethod.GET, "/api/BLog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/Category/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/SubCategory/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/User/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/followers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/connection/following").permitAll()

                        // 🔴 GROUP 3: ADMIN ONLY
                        // Agar URL "/api/Admin" se shuru hota hai, toh Token me role 'admin' hona compulsion hai.
                        .requestMatchers("/api/Admin/**").hasAuthority("admin")

                        // 🔒 GROUP 4: STRICTLY PRIVATE (Create, Update, Delete)
                        // Upar ki lists me jisko humne allow nahi kiya (Jaise Blogs ka POST ya PUT), wo sab yahan aa kar phas jayenge.
                        // '.authenticated()' ka matlab hai "Token dikhao, warna entry nahi milegi!"
                        .anyRequest().authenticated()
                )

                // 6. Hamara Custom Guard: Spring ki basic check se theek pehle, humne apna `JwtFilter` khada kar diya hai jo Token verify karega.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}