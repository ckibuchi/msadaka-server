package msadaka.controllers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@SpringBootApplication
@EntityScan(basePackages = {"msadaka.models"})  // scan JPA entities
@EnableJpaRepositories("msadaka.repositories") //Reposiroties
@ComponentScan("msadaka.controllers")
@EnableScheduling
public class Application {
    @RequestMapping("/")
    public String home() {
        return "Hello Docker World.. You are home..";
    }


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);


    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/payments/**");
                registry.addMapping("/churches/**");
            }
        };
    }


}
