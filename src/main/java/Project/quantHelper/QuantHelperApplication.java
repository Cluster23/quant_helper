package Project.quantHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuantHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantHelperApplication.class, args);
	}

}
