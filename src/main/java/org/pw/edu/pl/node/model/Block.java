package org.pw.edu.pl.node.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import static org.pw.edu.pl.node.controller.WalletController.bytesToHex;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    private int index;
    private String hash;
    private String previousHash;
    private long timestamp;
    private String difficulty;
    private String nonce;
    private List<Transaction> transactionList;

    public static boolean isValid(Block susBlock, Map<String, Block> map) throws Exception {
        if (map.containsKey(susBlock.getPreviousHash())) {
            Block previousBlock = map.get(susBlock.getPreviousHash());
            if (susBlock.getHash().startsWith(previousBlock.getDifficulty())) {
                //calculate hash
                String freshHash = calculateHash(susBlock);
                System.out.println("CalculatedHash" + freshHash);
                return freshHash.equals(susBlock.getHash());
            }
        }
        return false;
    }

    public static String calculateHash(Block block) throws Exception {
        String hash = block.getHash();
        block.setHash(null);
        String stringBlockWithoutHash = new ObjectMapper().writeValueAsString(block);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        block.setHash(hash);
        return bytesToHex(digest.digest(stringBlockWithoutHash.getBytes()));
    }
}
