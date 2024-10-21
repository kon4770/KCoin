package org.pw.edu.pl.orchestrator;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.pw.edu.pl.orchestrator.Builder.readAllPasswords;
import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

public class WalletInitializer {
    public static void main(String[] args) throws Exception {
        System.out.println("Im initializing all wallets");
        Map<String, String> passwords = readAllPasswords();
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            int port = PORT_START + i;
            String password = passwords.get("http://localhost:" + port);
            if(password==null){
                System.out.println("No password for this wallet");
                continue;
            }
            String result = restTemplate.postForObject("http://localhost:" + port + "/wallet/initialize", password, String.class);
            System.out.println("Wallet " + port + " initialized " + result);
        }
    }
}
