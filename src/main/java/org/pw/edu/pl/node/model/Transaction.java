package org.pw.edu.pl.node.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private List<TransactionUnit> sources;
    private List<TransactionUnit> destinations;
    private String signature;

    public String getTransactionWithoutSignature() throws JsonProcessingException {
        Map<String, List<TransactionUnit>> result = new HashMap<>();
        result.put("sources", sources);
        result.put("destinations", destinations);
        return new ObjectMapper().writeValueAsString(result);
    }
}
