package org.pw.edu.pl.orchestrator;

import org.springframework.web.client.RestTemplate;

public class Destroyer {
    public static void main(String[] args) {
        System.out.printf("This is destroyer");
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 20; i++) {
            try {
                System.out.println("Turning off 3" + (100 + i));
                restTemplate.getForObject("http://localhost:3" + (100 + i) + "/turnOff", Void.class);
            }catch (Exception e){
                System.out.println("No response from 3" + (100 + i));
            }
        }
    }
}
