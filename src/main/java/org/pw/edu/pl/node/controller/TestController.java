package org.pw.edu.pl.node.controller;

import org.pw.edu.pl.node.App;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {


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
}