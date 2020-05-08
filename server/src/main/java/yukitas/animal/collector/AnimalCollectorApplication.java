package yukitas.animal.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AnimalCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalCollectorApplication.class, args);
	}

}