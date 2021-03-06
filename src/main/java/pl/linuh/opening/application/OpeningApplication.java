package pl.linuh.opening.application;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author marek on 24/10/15.
 */

@SpringBootApplication
@EnableAutoConfiguration
//@Configuration
//@ComponentScan({"pl.linuh.opening"})
@EnableTransactionManagement
@EnableJpaRepositories("pl.linuh.opening.repositories")
@EnableJpaAuditing
@EntityScan("pl.linuh.opening.model")
public class OpeningApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpeningApplication.class, args);
    }

}