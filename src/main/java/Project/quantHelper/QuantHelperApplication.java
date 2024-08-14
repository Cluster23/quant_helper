package Project.quantHelper;

import Project.quantHelper.service.NewsService;
import com.google.gson.JsonObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class QuantHelperApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantHelperApplication.class, args);
	}

}
