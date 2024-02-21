package com.brunoshiroma.java.vinte.um.estudos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Estudos dos novos usos do switch
 * 
 * @see https://docs.oracle.com/en/java/javase/21/language/pattern-matching-switch-expressions-and-statements.html
 * @see https://spring.io/blog/2023/09/20/hello-java-21#enhanced-switch
 */

class SwitchRecordTeste {

    record Usuario(String nome) {
    }

    record Produto(String nome, double preco) {
    }

    @Test
    void testeSwitchRecordMatching() {

        final var usuario1 = new Usuario("1");
        final var produto1 = new Produto("Produto 1", 1);

        assertEquals("Usuário 1", teste(usuario1));
        assertEquals("Produto Produto 1, custa 1.0", teste(produto1));

    }

    static String teste(Object objeto) {

        return switch (objeto) {
            case Usuario(var nome) -> STR."Usuário \{nome}";
            case Produto(var nome, var preco) -> STR."Produto \{nome}, custa \{preco}";
            default -> null;
        };
    }

}