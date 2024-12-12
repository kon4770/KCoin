package org.pw.edu.pl.node.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUnit implements Comparable<TransactionUnit> {

    private BigDecimal amount;
    private String publicKey;
    private String signature;

    public String getTUWithoutSignature() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        result.put("amount", amount.toString());
        result.put("publicKey", publicKey);
        return new ObjectMapper().writeValueAsString(result);
    }

    @Override
    public int compareTo(TransactionUnit other) {
        if(this.publicKey.compareTo(other.publicKey) == 0){
            return this.amount.compareTo(other.amount);
        }
        return this.publicKey.compareTo(other.publicKey);
    }
}
