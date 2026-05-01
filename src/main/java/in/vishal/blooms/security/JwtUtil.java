package in.vishal.blooms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ✅ Properties file se key utha rahe hain!
    @Value("${jwt.secret}")
    private String secretKey;

    // Ek helper method jo key banayega
    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Neeche jahan bhi 'key' use hua tha (generateToken aur getClaims me),
    // wahan 'key' ki jagah 'getKey()' likhna hoga.
    private final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

    // ✅ TOKEN GENERATE
    public String generateToken(String userId, String role) {

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ USER ID
    public String extractUserId(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ ROLE
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ VALIDATION
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
