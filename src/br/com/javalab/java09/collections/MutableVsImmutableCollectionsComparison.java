package br.com.javalab.java09.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java 9 - Comparação: coleções mutáveis x "não modificáveis" x imutáveis (JEP 269)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: comparação lado a lado de três formas de expor uma lista:</p>
 * <ul>
 *   <li>{@code ArrayList}: mutável — qualquer um com a referência altera.</li>
 *   <li>{@code Collections.unmodifiableList} (Java 1.2): <b>view</b> somente leitura. Quem
 *       a recebe não consegue alterar, mas ela <b>reflete</b> mudanças feitas na lista
 *       original — não é imutável!</li>
 *   <li>{@code List.of} (Java 9): imutável de verdade, sem lista "por trás".</li>
 * </ul>
 * <p>Benefício: entender a diferença evita bugs sutis (ex.: retornar uma view e o
 * conteúdo mudar depois "sozinho").</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/collections/MutableVsImmutableCollectionsComparison.java
 * </pre>
 */
public class MutableVsImmutableCollectionsComparison {

    public static void main(String[] args) {
        List<String> cart = new ArrayList<>();
        cart.add("Teclado");
        cart.add("Mouse");

        List<String> mutable = cart;
        List<String> unmodifiableView = Collections.unmodifiableList(cart);
        List<String> immutable = List.of("Teclado", "Mouse");

        System.out.println("=== Estado inicial ===");
        print(mutable, unmodifiableView, immutable);

        System.out.println();
        System.out.println("=== Tentando alterar cada uma diretamente ===");
        tryAdd("ArrayList", mutable);
        tryAdd("unmodifiableList", unmodifiableView);
        tryAdd("List.of", immutable);

        System.out.println();
        System.out.println("=== Alterando a lista ORIGINAL (cart) ===");
        cart.add("Monitor");
        print(mutable, unmodifiableView, immutable);
        System.out.println("A view 'unmodifiable' MUDOU junto com a original!");
        System.out.println("List.of continua igual: é imutável de verdade.");

        System.out.println();
        System.out.println("=== Resumo ===");
        System.out.println("ArrayList          -> mutável, aceita null");
        System.out.println("unmodifiableList   -> view somente leitura; reflete a original; aceita null");
        System.out.println("List.of (Java 9)   -> imutável, não aceita null, implementação compacta");
        System.out.println("Dica: para snapshot imutável de uma lista existente use List.copyOf (Java 10).");
    }

    private static void tryAdd(String label, List<String> list) {
        try {
            list.add("Cabo HDMI");
            System.out.printf("%-17s add OK -> %s%n", label, list);
        } catch (UnsupportedOperationException e) {
            System.out.printf("%-17s add -> UnsupportedOperationException%n", label);
        }
    }

    private static void print(List<String> mutable, List<String> view, List<String> immutable) {
        System.out.println("ArrayList        : " + mutable);
        System.out.println("unmodifiableList : " + view);
        System.out.println("List.of          : " + immutable);
    }
}
