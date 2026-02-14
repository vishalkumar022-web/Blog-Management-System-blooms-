package in.vishal.blooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // ✅ Jadu shuru karne ka button! Ye Spring Boot ko batata hai ki Cache on karo.
public class BloomsApplication {

    public static void main(String[] args) {

        SpringApplication.run(BloomsApplication.class, args);
        System.out.println("Bloom application started sucessfully ");
    }

}
