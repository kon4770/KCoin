package org.pw.edu.pl.node.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLOutput;
import java.util.*;

import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

@Controller
public class NodeController {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    public static Set<String> destinations = new HashSet<>();

    @PostMapping("/node/addDest/")
    @ResponseBody
    public Set<String> addDest(@RequestBody String destinationUrl, HttpServletRequest request) {
        if (!destinations.contains(destinationUrl)) {
            System.out.println("Adding destination: " + destinationUrl);
            destinations.add(destinationUrl);
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String rootUrl = scheme + "://" + serverName + ":" + serverPort;
            restTemplate.postForObject(destinationUrl + "/node/addDest/", rootUrl, Set.class);
        }
        System.out.println("I have this destination");
        return destinations;
    }


}
