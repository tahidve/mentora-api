package vn.mentora.mentora_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MentoraApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentoraApiApplication.class, args);
	}

}
