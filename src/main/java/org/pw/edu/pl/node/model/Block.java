package org.pw.edu.pl.node.model;

import lombok.*;

import java.security.MessageDigest;

@Getter
@Setter
@ToString
@Builder
public class Block {

    private int number;
    private String hash;
    private String previousHash;
    private int timestamp;
    private String data;
    private int difficulty;
    private String nonce;
}
