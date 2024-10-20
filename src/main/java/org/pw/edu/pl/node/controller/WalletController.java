package org.pw.edu.pl.node.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pw.edu.pl.node.model.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;


@Controller
public class WalletController {

    public static List<Identity> identityList;
    public static byte[] password;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebServerApplicationContext webServerAppCtxt;

    @EventListener(ApplicationReadyEvent.class)
    public void readIdentityFile() throws IOException {

    }

    @GetMapping("/wallet/generateNewKey")
    @ResponseBody
    public ResponseEntity<String> generateNewKey(@RequestBody(required = false) String plainTextPassword) {
        try {
            if (password == null) {
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128); // For AES, you can choose 128, 192 or 256-bit key
                SecretKey secretKey = keyGen.generateKey();
                password = secretKey.getEncoded();
            } else if (!Objects.equals(plainTextPassword, Base64.getEncoder().encodeToString(password))) {
                return ResponseEntity.status(400).body("Wrong password");
            }
            int port = webServerAppCtxt.getWebServer().getPort();
            // Specify the file name and path (change the path as per your need)
            String filePath = "IdentityFile.ShouldContainFullURL" + port + ".json";
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                PrivateKey privateKey = keyPair.getPrivate();
                Identity identity = new Identity();
                identity.setPrivateKey(privateKey);
                identity.setPublicKey(keyPair.getPublic());
                if (identityList == null) {
                    identityList = new ArrayList<>();
                }
                identityList.add(identity);
                List<List<String>> encryptedIdentityList = new ArrayList<>(generateEncryptedList(identityList, password));
                FileWriter writer = new FileWriter(file);
                writer.write(objectMapper.writeValueAsString(encryptedIdentityList));
                writer.close();
                System.out.println("Values written to the file.");
            }
            return ResponseEntity.ok(Base64.getEncoder().encodeToString(password));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Dont play with me");
        }
    }

    @PostMapping("/wallet/initialize")
    @ResponseBody
    public ResponseEntity<String> initWalletController(@RequestBody String passwordBase64) throws Exception {
        identityList = new ArrayList<>();
        int port = webServerAppCtxt.getWebServer().getPort();
        byte[] susPassword = Base64.getDecoder().decode(passwordBase64);
        System.out.println(Arrays.toString(susPassword));
        String filePath = "IdentityFile.ShouldContainFullURL" + port + ".json";
        File file = new File(filePath);
        if (file.exists()) {
            List<List<String>> parsedFile = objectMapper.readValue(file, List.class);
            for (List<String> list : parsedFile) {
                String encryptedPrivateKey = list.get(0);
                String hashedPrivateKey = list.get(1);
                SecretKey secretKey = new SecretKeySpec(susPassword, "AES");
                byte[] privateKeyBytesEncrypted = Base64.getDecoder().decode(encryptedPrivateKey);
                byte[] privateKey = AESdecrypt(privateKeyBytesEncrypted, secretKey);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String trustedHashedPrivateKey = bytesToHex(digest.digest(privateKey));
                if (hashedPrivateKey.equals(trustedHashedPrivateKey.strip())) {
                    Identity identity = new Identity();
                    identity.setPrivateKey(getPrivateKeyFromBytes(privateKey));
                    identity.setPublicKey(getPublicKeyFromPrivateKey(identity.getPrivateKey()));
                    identityList.add(identity);
                    password = susPassword;
                } else {
                    return ResponseEntity.status(400).body("Wrong password");
                }
            }
        }
        return ResponseEntity.ok("Initialized");
    }

    @GetMapping("/wallet/getPublicKeys")
    @ResponseBody
    public String addDest() throws JsonProcessingException {
        List<String> result = new ArrayList<>();
        identityList.forEach(identity -> {
            result.add(Base64.getEncoder().encodeToString(identity.getPublicKey().getEncoded()));
        });
        return objectMapper.writeValueAsString(result);
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            result.append(characters.charAt(randomIndex));
        }
        return result.toString();
    }

    // AES Encryption
    public static String AESencrypt(String plainText, SecretKey secretKey) throws Exception {
        // Create Cipher instance and initialize it for encryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Perform encryption
        byte[] encryptedBytes = cipher.doFinal(Base64.getDecoder().decode(plainText));

        // Return the encrypted data as a Base64-encoded string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES Decryption
    public static byte[] AESdecrypt(byte[] encryptedText, SecretKey secretKey) throws Exception {
        // Create Cipher instance and initialize it for decryption
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Perform decryption
        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        // Convert the decrypted byte array to a string and return
        return decryptedBytes;
    }

    // Method to convert byte array into hexadecimal string
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static PublicKey getPublicKeyFromPrivateKey(PrivateKey privateKey) throws Exception {
        // Get the KeyFactory instance for RSA
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Get the private key's specification
        RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

        // Derive the public key specification from the private key specification
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKeySpec.getModulus(), RSAKeyGenParameterSpec.F4);

        // Generate the public key from the public key specification
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey getPrivateKeyFromBytes(byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateKey);

        // Get a KeyFactory for RSA (or other algorithm, e.g., "EC", "DSA")
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Generate the PublicKey from the KeySpec
        return keyFactory.generatePrivate(keySpecPrivate);
    }

    public List<List<String>> generateEncryptedList(List<Identity> identityList, byte[] password) throws Exception {
        if (identityList == null) {
            return List.of();
        }
        List<List<String>> resultList = new ArrayList<>();
        for (Identity identity : identityList) {
            SecretKey secretKey = new SecretKeySpec(password, "AES");
            String privateKey = Base64.getEncoder().encodeToString(identity.getPrivateKey().getEncoded());
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hashedPrivateKey = bytesToHex(digest.digest(identity.getPrivateKey().getEncoded()));
            List<String> singlePair = List.of(AESencrypt(privateKey, secretKey), hashedPrivateKey);
            resultList.add(singlePair);
        }
        return resultList;
    }

}
