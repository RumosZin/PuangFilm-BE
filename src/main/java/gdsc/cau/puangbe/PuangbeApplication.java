package gdsc.cau.puangbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class PuangbeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuangbeApplication.class, args);
	}

}
