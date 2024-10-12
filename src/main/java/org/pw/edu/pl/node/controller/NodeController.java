package org.pw.edu.pl.node.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLOutput;
import java.util.*;

@Controller
public class NodeController {

    @Autowired
    private RestTemplate restTemplate;

    public static Set<Integer> destinations = new HashSet<>();

    @GetMapping("/node/addDest/{portNumber}")
    @ResponseBody
    public String addDest(@PathVariable Integer portNumber) throws Exception {
        System.out.println("Adding destination port: " + portNumber);
        if (portNumber != null) {
            String responce = restTemplate.getForObject("http://localhost:" + portNumber + "/wallet/getPublicKey", String.class);
            if (responce != null && !responce.isBlank()) {
                if (destinations.contains(portNumber)) {
                    System.out.println("Im already here");
                }
                destinations.add(portNumber);
                return destinations.toString();
            }
            throw new Exception("Destination not ready!");
        }
        throw new Exception("Failed to add myself to parent node!");
    }


}
