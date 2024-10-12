package org.pw.edu.pl.orchestrator;

import org.pw.edu.pl.node.App;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Builder {

    public static void main(String[] args) {
        System.out.println("This is builder");

        for (int i = 0; i < 20; i++) {
            String port = "3" + (100 + i);
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
