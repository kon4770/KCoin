package org.pw.edu.pl.node.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pw.edu.pl.node.model.Block;
import org.pw.edu.pl.node.model.Transaction;
import org.pw.edu.pl.node.model.TransactionUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

import static org.pw.edu.pl.node.controller.WalletController.bytesToHex;
import static org.pw.edu.pl.node.controller.WalletController.identityList;

@Controller
public class NodeController {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();
    public final int difficulty = 3;
    public static Set<String> destinations = new HashSet<>();
    public static Transaction genesisTransaction = Transaction.builder()
            .destinations(List.of(TransactionUnit.builder()
                    .publicKey("PUBLIC_KEY")
                    .amount(new BigDecimal("100"))
                    .signature("SIGNATURE").build()))
            .build();
    public static Block genesisBlock = Block.builder()
            .difficulty("0")
            .nonce("0")
            .transactionList(List.of(genesisTransaction))
            .hash("HASHHH")
            .build();

    public static Map<String, Block> blockMap = new HashMap<>(Map.of(genesisBlock.getHash(), genesisBlock));
    public static Block lastTrueBLock = genesisBlock;


    //getBlocks
    @GetMapping("/node/getBlocks/")
    @ResponseBody
    public Map<String, Block> getBlocks() {
        return blockMap;
    }

    @PostMapping("/node/addBlock/")
    @ResponseBody
    public Map<String, Block> addNewBlock(@RequestBody Block block) throws Exception {
        String hash = block.getHash();
        if (blockMap.containsKey(hash)) {
            return blockMap;
        } else {
            if (Block.isValid(block, blockMap)) {
                blockMap.put(hash, block);
                lastTrueBLock = block;
            }
        }
        return blockMap;
    }

    //mineBlock
    @GetMapping("/node/mineBlock/")
    @ResponseBody
    public Block mineBlockEndpoint() throws Exception {
        Block block = mineBlock(); //TODO
        blockMap.put(block.getHash(), block);
        for (String dest : destinations) {
            restTemplate.postForObject(dest + "/node/addBlock/", block, Block.class);
        }
        return block;
    }

    //addDestination
    @PostMapping("/node/addDest/")
    @ResponseBody
    public Set<String> addDest(@RequestBody String destinationUrl, HttpServletRequest request) {
        if (!destinations.contains(destinationUrl)) {
            System.out.println("Adding destination: " + destinationUrl);
            destinations.add(destinationUrl);
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String rootUrl = scheme + "://" + serverName + ":" + serverPort;
            restTemplate.postForObject(destinationUrl + "/node/addDest/", rootUrl, Set.class);
        }
        System.out.println("I have this destination");
        return destinations;
    }

    @GetMapping//TODO do we need this?
    @ResponseBody
    public Block getLastBlock() {
        return lastTrueBLock;
    }


    private Block mineBlock() throws Exception {
        Transaction coinBaseTransaction = generateNewCoinBaseTransaction();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Block newMinedBlock = Block.builder()
                .index(lastTrueBLock.getIndex() + 1)
                .previousHash(lastTrueBLock.getHash())
                .difficulty("0".repeat(difficulty))
                .transactionList(List.of(coinBaseTransaction)) // TODO add transaction list here
                .nonce(generateRandomString())
                .timestamp(timestamp.getTime())
                .build();
        String hash = calculateHash(newMinedBlock);
        long tries = 1L;
        while (!hash.startsWith(newMinedBlock.getDifficulty())) {
            newMinedBlock.setNonce(generateRandomString());
            hash = calculateHash(newMinedBlock);
            if (tries % 1000 == 0) {
                System.out.println("Tried: " + tries);
            }
            tries++;
        }
        newMinedBlock.setHash(hash);
        System.out.println("Overall tried: " + tries);
        return newMinedBlock;
    }

    private Transaction generateNewCoinBaseTransaction() {
        return Transaction.builder()
                .destinations(List.of(
                        TransactionUnit.builder()
                                .publicKey(identityList.get(0).getPublicKeyStringHex())
                                .amount(new BigDecimal("50"))
                                .build()
                ))
                .build();
    }

    public static String calculateHash(Block block) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String stringBlock = new ObjectMapper().writeValueAsString(block);
        return bytesToHex(digest.digest(stringBlock.getBytes()));
    }

    public static String generateRandomString() {
        int leftLimit = 33; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 100;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
