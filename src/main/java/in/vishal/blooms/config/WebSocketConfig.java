package in.vishal.blooms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// @Configuration: Spring ko batata hai ki ye settings wali file hai, ise pehle padho.
@Configuration
// @EnableWebSocketMessageBroker: Ye real-time messaging ka feature ON kar deta hai. Agar ye nahi likhenge toh server simple API server hi rahega.
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Ye function hamare chat "Tower" ka rasta (URL) banata hai.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // "/ws": Tumhara React frontend is URL par aakar backend se phone call connect karega.
        // setAllowedOrigins("*"): Iska matlab kisi bhi website se log chat connect kar sakte hain (Security block nahi karegi).
        // withSockJS(): Agar kisi ka internet/browser WebSocket support nahi karta, toh ye usko backup connection de deta hai taaki chat na ruke.
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

    // Ye function Messages ko route (sahi raste par bhejna) karta hai.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/app": Jab bhi React se koi naya message AAYEGA, uske URL ke aage "/app" laga hona chahiye.
        registry.setApplicationDestinationPrefixes("/app");

        // "/topic": Jab backend se message wapas JAYEGA (dusre user ko), toh wo "/topic" wale raste se jayega.
        registry.enableSimpleBroker("/topic");
    }
}