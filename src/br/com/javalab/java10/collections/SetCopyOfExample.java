package br.com.javalab.java10.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Java 10 - Set.copyOf
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code Set.copyOf(colecao)} cria um conjunto imutável a partir de qualquer
 * coleção. Diferente de {@code Set.of}, <b>duplicatas são aceitas</b> e descartadas
 * (comporta-se como {@code new HashSet<>(colecao)}).</p>
 * <p>Benefício: remover duplicatas e congelar o resultado em uma chamada.</p>
 * <p>Antes: {@code Collections.unmodifiableSet(new HashSet<>(colecao))}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/collections/SetCopyOfExample.java
 * </pre>
 */
public class SetCopyOfExample {

    public static void main(String[] args) {
        List<String> purchasedCategories = new ArrayList<>(List.of("LIVROS", "GAMES", "LIVROS", "MUSICA", "GAMES"));

        System.out.println("=== Set.copyOf: duplicatas são descartadas ===");
        Set<String> categories = Set.copyOf(purchasedCategories);
        System.out.println("Compras    : " + purchasedCategories);
        System.out.println("Categorias : " + categories + "  (ordem não garantida)");

        System.out.println();
        System.out.println("=== Diferença para Set.of (Java 9) ===");
        try {
            Set.of("LIVROS", "LIVROS");
        } catch (IllegalArgumentException e) {
            System.out.println("Set.of com duplicata     -> IllegalArgumentException");
        }
        System.out.println("Set.copyOf com duplicata -> " + Set.copyOf(List.of("LIVROS", "LIVROS")));

        System.out.println();
        System.out.println("=== Snapshot imutável ===");
        Set<String> mutable = new HashSet<>(categories);
        Set<String> snapshot = Set.copyOf(mutable);
        mutable.add("ESPORTES");
        System.out.println("Original alterado: " + mutable.size() + " itens | snapshot: " + snapshot.size() + " itens");
        try {
            snapshot.add("ESPORTES");
        } catch (UnsupportedOperationException e) {
            System.out.println("snapshot.add -> UnsupportedOperationException");
        }
    }
}
