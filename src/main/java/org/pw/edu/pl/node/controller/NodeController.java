package org.pw.edu.pl.node.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pw.edu.pl.node.model.Block;
import org.pw.edu.pl.node.model.Transaction;
import org.pw.edu.pl.node.model.TransactionUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

import static org.pw.edu.pl.node.controller.WalletController.*;

@Controller
public class NodeController {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();
    public final int difficulty = 3;
    public static Set<String> destinations = new HashSet<>();
    public static Transaction genesisTransaction = Transaction.builder()
            .destinations(List.of(TransactionUnit.builder()
                    .publicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjvHsA5RBn3MBxBEeqcDSVXjEsHBCMRmYNq5fPIS9tRaXvaW5Lrdkog5dbG+iVnwNeM6KIfwWFOB98uNaKXEgIC3i+pjmzwdLO4hmybc/HpA7NQBIj/Fy8pVtZFb4c3pH0iR5pPGOd/2Z111ZGiY3k7ZY5cPcd68awFLcOs0Mv3jfT/m1QQhIe4070zgvVsjIXwuZhj7rArblpNsYjiJZGIjcuRWso2scyQ7rWmJ/TZs4X3MIl9mtWri8ENpHsP7cjaSd7pvBA4rXTelZ3P+aQd+vCK3Mzne5UjuqLGdwg8nUKvcE/vSt/g/CmzBGXb3cL7p5xTakqmXHEYdlcGDK5QIDAQAB")
                    .amount(new BigDecimal("100")).build()))
            .signature("FAiDF0qFhn+VaQ3VsMjm9YIMdwjr2iDaN0Rq4IotK404n/ewldj6I1OCOH2FwuU/DGJ8OHi0q17H+qiraccpEtRMmaSuf9gevz7+ybRzXAi9uz+SXJCQO4+5TqSeLcF24h3VYC64sEU//WcWnqxF/WC2r4j2WrudaImDrrAvAMtObmSzqSpOF1+l7kGvGAm1cS6xGS/0i21mcqsjbmI7jcFKQSGnfCBgO6d2sRewTFOtza7ipKGSELbZKVVLIR6mMzlslgvxd8oNSAN5BOjEk4EM8NOUhSVrl4r1CiLimP5+mgDlHFuKyVpWPHiim5EUx086KSh+xB8ve+NhQBfCtQ==")
            .build();
    public static Block genesisBlock = Block.builder()
            .difficulty("0")
            .nonce("0")
            .transactionList(List.of(genesisTransaction))
            .hash("aifndjkaefuaefljkncmLKZJJpf'ainangdzfugnsrg;lfijtnavuhhlizvidojgcmguraglimfkc'ozsrigjaerng;ddmx;wekrnvxk.g.alrijgias")
            .build();

    public static Map<String, Block> blockMap = new HashMap<>(Map.of(genesisBlock.getHash(), genesisBlock));
    public static Map<String, Block> orphaneBlockMap = new HashMap<>();
    public static Block lastTrueBLock = genesisBlock;
    public static Boolean isEvil = false;

    @PostMapping("/node/isEvil/")
    @ResponseBody
    public boolean setEvil(@RequestBody Boolean isEvil){
        this.isEvil = isEvil;
        return this.isEvil;
    }

    @GetMapping("/node/isEvil/")
    @ResponseBody
    public boolean getEvil(){
        return this.isEvil;
    }

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
        if (!blockMap.containsKey(block.getPreviousHash())) { //orphan block
            orphaneBlockMap.put(block.getPreviousHash(), block);
            return blockMap;
        }
        if (orphaneBlockMap.containsKey(hash)) {
            orphaneBlockMap.remove(hash);
            addNewBlock(block);
        }
        if (blockMap.containsKey(hash)) {
            return blockMap;
        } else {
            if (Block.isValid(block, blockMap)) {
                transactionPool = new HashMap<>();
                blockMap.put(hash, block);
                if (lastTrueBLock.getIndex() > block.getIndex()) {
                    lastTrueBLock = block;
                }
            }
        }
        return blockMap;
    }

    @Scheduled(fixedDelay = 100L)
    public void triggerMining() throws Exception {
        mineBlockEndpoint();
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
                .nonce(generateRandomString(100))
                .timestamp(timestamp.getTime())
                .build();
        String hash = calculateHash(newMinedBlock);
        long tries = 1L;
        while (!hash.startsWith(newMinedBlock.getDifficulty())) {
            newMinedBlock.setNonce(generateRandomString(100));
            hash = calculateHash(newMinedBlock);
            if (tries % 1000 == 0) {
                System.out.println("Tried: " + tries);
            }
            tries++;
        }
        newMinedBlock.setHash(hash);
        transactionPool = new HashMap<>();
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

    public static String generateRandomString(int targetStringLength) {
        int leftLimit = 33; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
