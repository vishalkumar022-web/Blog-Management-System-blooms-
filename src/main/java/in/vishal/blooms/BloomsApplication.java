package in.vishal.blooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BloomsApplication {

    public static void main(String[] args) {

        SpringApplication.run(BloomsApplication.class, args);
        System.out.println("Bloom application started sucessfully ");
    }

}
