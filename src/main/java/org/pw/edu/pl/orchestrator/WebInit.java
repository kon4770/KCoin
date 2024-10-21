package org.pw.edu.pl.orchestrator;

import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

public class WebInit {
    public static void main(String[] args) {
        System.out.println("WebInitiator here");
        RestTemplate restTemplate = new RestTemplate();
        int[] numberOfConnections = { 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3};
        Random random = new Random();
        List<Integer> portsToVisit = new LinkedList<>();
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            portsToVisit.add(i);
        }
        Collections.shuffle(portsToVisit);
        System.out.println(portsToVisit);
        Queue<Integer> portsToVisitQueue = new LinkedList<>(portsToVisit);
        Set<Integer> allDest = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            int port = PORT_START + i;
            restTemplate.postForObject("http://localhost:" + ((i + 1) % NUMBER_OF_NODES + PORT_START) + "/node/addDest/", "http://localhost:" + port, Set.class);
        }/*
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            int port = PORT_START + i;
            try {
                Integer dest = portsToVisitQueue.poll();
                if (dest.intValue() == i) {
                    System.out.println("Chose myself, reshuffle");
                    portsToVisitQueue.add(dest);
                    i--;
                    continue;
                }
                System.out.println("Connecting " + port + " to " + (PORT_START + dest));
                allDest.addAll(restTemplate.postForObject("http://localhost:" + port + "/node/addDest/", "http://localhost:" + (PORT_START + dest), Set.class));
                allDest.addAll(restTemplate.postForObject("http://localhost:" + (PORT_START + dest) + "/node/addDest/", "http://localhost:" + port, Set.class));
//                int randomIntWithBound = random.nextInt(numberOfConnections.length);
//                for (int j = 0; j < numberOfConnections[randomIntWithBound]; j++) {
//                    int randomPort = PORT_START + random.nextInt(NUMBER_OF_NODES);
//                    if (randomPort != port) {
//                        System.out.println("Connecting " + port + " to " + randomPort);
//                        allDest.addAll(restTemplate.postForObject("http://localhost:" + port + "/node/addDest/", "http://localhost:" + randomPort, Set.class));
//                    }
//                }
            } catch (Exception e) {
                System.out.println("No response from " + port);
            }
        }*/
        System.out.println(allDest);
        System.out.println(allDest.size());
    }
}
