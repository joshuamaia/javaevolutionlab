package br.com.javalab.java15.string;

import java.nio.CharBuffer;
import java.util.List;

/**
 * Java 15 - CharSequence.isEmpty e finalizacao de String.formatted
 *
 * <p>Status: Definitivo (APIs, sem JEP).</p>
 *
 * <p>O que e: {@code CharSequence} ganhou o metodo default {@code isEmpty()}, entao
 * StringBuilder, StringBuffer, CharBuffer e qualquer implementacao passam a te-lo.
 * Alem disso, {@code String.formatted}, {@code stripIndent} e {@code translateEscapes}
 * (Preview APIs no 13/14, ver java13/textblocks) deixaram de ser deprecated e viraram
 * API definitiva junto com os Text Blocks (JEP 378).</p>
 * <p>Beneficio: codigo generico sobre CharSequence mais legivel.</p>
 * <p>Antes: {@code cs.length() == 0}.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/string/CharSequenceIsEmptyExample.java
 * </pre>
 */
public class CharSequenceIsEmptyExample {

    public static void main(String[] args) {
        List<CharSequence> fields = List.of(
                "Ana", new StringBuilder(), CharBuffer.wrap("Rua A, 10"), new StringBuffer("  "));

        System.out.println("=== Abordagem antiga: length() == 0 ===");
        for (CharSequence field : fields) {
            System.out.println(describe(field) + " vazio? " + (field.length() == 0));
        }

        System.out.println("=== Java 15: CharSequence.isEmpty() ===");
        for (CharSequence field : fields) {
            System.out.println(describe(field) + " vazio? " + field.isEmpty());
        }

        System.out.println("=== String.formatted (definitivo no 15) ===");
        StringBuilder log = new StringBuilder();
        if (log.isEmpty()) {
            log.append("Pedido %d validado em %d campos".formatted(1001, fields.size()));
        }
        System.out.println(log);
    }

    private static String describe(CharSequence cs) {
        return "%-16s \"%s\"".formatted(cs.getClass().getSimpleName(), cs);
    }
}
