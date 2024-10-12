package org.pw.edu.pl.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


/**
 * Hello world!
 */
@SpringBootApplication
public class App {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        System.out.println("Hello World!");
        context = new SpringApplicationBuilder(App.class).run(args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static void turnOff() {
        System.out.println("Turning off");
        SpringApplication.exit(context, () -> 0);
    }
}
