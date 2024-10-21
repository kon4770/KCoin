package org.pw.edu.pl.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pw.edu.pl.node.App;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pw.edu.pl.orchestrator.Config.NUMBER_OF_NODES;
import static org.pw.edu.pl.orchestrator.Config.PORT_START;

public class Builder {
    public static ObjectMapper objectMapper = new ObjectMapper();
    public static void saveAllPasswords(Map<String, String> passwords) throws Exception {
        File file = new File("AllPasswordsJustForManagement.json");
        file.delete();
        if(!file.exists() && file.createNewFile()){
            FileWriter writer = new FileWriter(file);
            writer.write(objectMapper.writeValueAsString(passwords));
            writer.close();
        }
    }

    public static Map<String, String> readAllPasswords() throws Exception {
        File file = new File("AllPasswordsJustForManagement.json");
        if(!file.exists()){
            System.out.println("No password file to read");
            return new HashMap<>();
        } else {
            return objectMapper.readValue(file, Map.class);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("This is builder");
        for (int i = 0; i < NUMBER_OF_NODES; i++) {
            String port = String.valueOf(PORT_START + i);
            // Command to be executed
            String[] command = {"C:\\Users\\Legion\\.jdks\\corretto-11.0.15\\bin\\java.exe", "-jar", ".\\target\\KCoin-1.0-SNAPSHOT-jar-with-dependencies.jar", "--server.port=" + port};

            try {
                // Create a ProcessBuilder with the command
                ProcessBuilder processBuilder = new ProcessBuilder(command);

                // Start the process
                Process process = processBuilder.start();

                new Thread(() -> {
                    // Wait for the process to finish and get the exit code
                    try {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("[" + port + "]" + line);  // Output the command result
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int exitCode = 0;
                        exitCode = process.waitFor();
                        System.out.println("Exit Code: " + exitCode);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
