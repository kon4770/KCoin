package org.pw.edu.pl.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static org.pw.edu.pl.orchestrator.Builder.*;
import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

public class WalletGenerateNewKey {

    public static void main(String[] args) throws Exception {
        System.out.println("Im generating new keys for all wallets");
        Map<String, String> passwords = readAllPasswords();
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            int port = PORT_START + i;
            String password = passwords.get("http://localhost:" + port);
            if(password==null){
                System.out.println("No password for this wallet");
                String result = restTemplate.postForObject("http://localhost:" + port + "/wallet/generateNewKey", password, String.class);
                System.out.println("New Private Key initialized, password: " + result);
                passwords.put("http://localhost:" + port, result);
            } else {
                String result = restTemplate.postForObject("http://localhost:" + port + "/wallet/generateNewKey", password, String.class);
                System.out.println("New Private Key initialized, password: " + result);
            }
        }
        saveAllPasswords(passwords);
    }
}
