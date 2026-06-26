package id.seapedia.seapediaprojectbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SeapediaProjectBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeapediaProjectBeApplication.class, args);
    }

}
