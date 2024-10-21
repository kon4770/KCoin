package org.pw.edu.pl.orchestrator;

import org.springframework.web.client.RestTemplate;

import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

public class Destroyer {
    public static void main(String[] args) {
        System.out.printf("This is destroyer");
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            String port = String.valueOf(PORT_START + i);
            try {
                System.out.println("Turning off " + port);
                restTemplate.getForObject("http://localhost:" + port + "/turnOff", Void.class);
            } catch (Exception e) {
                System.out.println("No response from " + port);
            }
        }
    }
}
