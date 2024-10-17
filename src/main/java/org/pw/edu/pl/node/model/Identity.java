package org.pw.edu.pl.node.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Identity {
    private PrivateKey privateKey;
    private PublicKey publicKey;
}
