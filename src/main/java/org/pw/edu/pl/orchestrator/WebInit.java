package org.pw.edu.pl.orchestrator;

import org.springframework.web.client.RestTemplate;

import java.util.*;

public class WebInit {
    public static void main(String[] args) {
        System.out.println("WebInitiator here");
        RestTemplate restTemplate = new RestTemplate();
        int[] numberOfConnections = {1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3};
        Random random = new Random();
        List<Integer> portsToVisit = new LinkedList<>() ;
        for (int i = 0; i < 20; i++) {
            portsToVisit.add(i);
        }
        Collections.shuffle(portsToVisit);
        System.out.println(portsToVisit);
        Queue<Integer> portsToVisitQueue = new LinkedList<>(portsToVisit);
        Set<Integer> allDest = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            try {
                Integer dest = portsToVisitQueue.poll();
                allDest.addAll(restTemplate.getForObject("http://localhost:3" + (100 + i) + "/node/addDest/3" + (100 + dest), Set.class));
                int randomIntWithBound = random.nextInt(numberOfConnections.length);
                for (int j = 0; j < randomIntWithBound; j++) {
                    int randomPort = 100 + random.nextInt(20);
                    System.out.println("Connecting 3" + (100 + i) + " to 3" + randomPort);
                    allDest.addAll(restTemplate.getForObject("http://localhost:3" + (100 + i) + "/node/addDest/3" + randomPort, Set.class));
                }
            } catch (Exception e) {
                System.out.println("No response from 3" + (100 + i));
            }
        }
        System.out.println(allDest);
        System.out.println(allDest.size());
    }
}
