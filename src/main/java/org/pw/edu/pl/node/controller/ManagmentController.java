package org.pw.edu.pl.node.controller;

import org.pw.edu.pl.node.App;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static org.pw.edu.pl.node.controller.NodeController.destinations;

@Controller
public class ManagmentController {

    public static Set<String> myPingIdSet = new HashSet<>();

    @GetMapping("/turnOff")
    @ResponseBody
    public void turnOff() {

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                App.turnOff();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @GetMapping("/myPing/{pingId}")
    @ResponseBody
    public String ping(@PathVariable String pingId) {
        System.out.println("I have received ping with id: " + pingId);
        RestTemplate restTemplate = new RestTemplate();
        if (myPingIdSet.contains(pingId)) {
            System.out.println("This message is duplicate, stopping");
            return "This message is duplicate, stopping";
        } else {
            myPingIdSet.add(pingId);
            destinations.parallelStream().forEach(dest -> {
                System.out.println(dest + " returned " + restTemplate.getForObject(dest + "/myPing/" + pingId, String.class));
            });
        }
        return "Ping end";
    }
}