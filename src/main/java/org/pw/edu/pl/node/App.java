package org.pw.edu.pl.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pw.edu.pl.node.model.Block;
import org.pw.edu.pl.node.model.Transaction;
import org.pw.edu.pl.node.model.TransactionUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 */
@SpringBootApplication
public class App {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println("Hello World!");
        /*BigDecimal price = new BigDecimal("19.99");
        Block block = Block.builder().transactionList(List.of(Transaction.builder().destinations(List.of(TransactionUnit.builder().amount(price).build())).build())).build();
        System.out.println(price);
        System.out.println(new ObjectMapper().writeValueAsString(block));
        Block exampleBlock = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(block), Block.class);
        System.out.println(new ObjectMapper().writeValueAsString(exampleBlock));*/
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
