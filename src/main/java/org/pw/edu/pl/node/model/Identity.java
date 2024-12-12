package org.pw.edu.pl.node.model;

import lombok.*;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.pw.edu.pl.node.controller.WalletController.identityList;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Identity {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private byte[] iv;
    public String getPublicKeyStringHex(){
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    public String getPrivateKeyStringHex(){
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

}
