package org.pw.edu.pl.node.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.*;

@Controller
public class WalletController {
    public static double myMoney = 0;
    public PrivateKey myPrivateKey;
    public PublicKey myPublicKey;

    public WalletController(){
        try {
            // Step 1: Create a KeyPairGenerator for the RSA algorithm
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

            // Step 2: Initialize the key size (e.g., 2048 bits)
            keyPairGenerator.initialize(2048);

            // Step 3: Generate the KeyPair (which contains both public and private keys)
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Step 4: Extract the private and public keys from the KeyPair
            this.myPrivateKey = keyPair.getPrivate();
            this.myPublicKey = keyPair.getPublic();

            // Step 5: Print out the keys (optional, in encoded form)
            System.out.println("Private Key: " + this.myPrivateKey);
            System.out.println("Public Key: " + this.myPublicKey);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/wallet/getPublicKey")
    @ResponseBody
    public String addDest() throws Exception {
        if(myPublicKey!=null){
            return myPublicKey.toString();
        }
        throw new Exception("Wallet not initialized");
    }
}
