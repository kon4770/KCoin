package org.pw.edu.pl.node.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUnit {

    private BigDecimal amount;
    private String publicKey;
    private String signature;
}
