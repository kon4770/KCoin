package org.pw.edu.pl.node.model;

import lombok.*;

import java.util.List;

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
}
