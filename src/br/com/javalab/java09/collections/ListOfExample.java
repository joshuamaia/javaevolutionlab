package br.com.javalab.java09.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Java 9 - List.of: fábrica de listas imutáveis (JEP 269)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code List.of(e1, e2, ...)} cria uma lista <b>imutável</b> (não modificável
 * de verdade, não uma "view") em uma única expressão.</p>
 * <p>Benefício: código curto, intenção clara, implementação compacta e segura para
 * compartilhar entre threads.</p>
 * <p>Antes: {@code Collections.unmodifiableList(new ArrayList<>(Arrays.asList(...)))}.</p>
 *
 * <p>Regras: qualquer operação de escrita lança {@code UnsupportedOperationException};
 * elementos {@code null} são proibidos ({@code NullPointerException}), inclusive em
 * {@code contains(null)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/collections/ListOfExample.java
 * </pre>
 */
public class ListOfExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 8) ===");
        List<String> oldStatuses = Collections.unmodifiableList(
                new ArrayList<>(Arrays.asList("NOVO", "PAGO", "ENVIADO")));
        System.out.println("Status: " + oldStatuses);

        System.out.println();
        System.out.println("=== Abordagem nova (Java 9): List.of ===");
        List<String> statuses = List.of("NOVO", "PAGO", "ENVIADO", "ENTREGUE");
        System.out.println("Status: " + statuses);
        System.out.println("Vazia: " + List.of());
        System.out.println("Classe da implementação: " + statuses.getClass().getName());

        System.out.println();
        System.out.println("=== Imutabilidade ===");
        try {
            statuses.add("CANCELADO");
        } catch (UnsupportedOperationException e) {
            System.out.println("add() -> UnsupportedOperationException");
        }
        try {
            statuses.set(0, "RASCUNHO");
        } catch (UnsupportedOperationException e) {
            System.out.println("set() -> UnsupportedOperationException");
        }

        System.out.println();
        System.out.println("=== null é proibido ===");
        try {
            List.of("NOVO", null);
        } catch (NullPointerException e) {
            System.out.println("List.of(\"NOVO\", null) -> NullPointerException");
        }
        try {
            statuses.contains(null);
        } catch (NullPointerException e) {
            System.out.println("contains(null) -> NullPointerException");
        }

        System.out.println();
        System.out.println("=== Precisa de lista mutável? Copie ===");
        List<String> editable = new ArrayList<>(statuses);
        editable.add("CANCELADO");
        System.out.println("Cópia editável: " + editable);
    }
}
