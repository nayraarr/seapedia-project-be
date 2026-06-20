package id.seapedia.seapediaprojectbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SeapediaProjectBeApplication {

    public static void main(String[] args) {
        System.out.println("JWT_SECRET = " + System.getenv("JWT_SECRET"));
        SpringApplication.run(SeapediaProjectBeApplication.class, args);
    }

}
