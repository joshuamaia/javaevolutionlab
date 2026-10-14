package br.com.javalab.java08.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Java 8 - Iterable.forEach, Collection.removeIf, List.replaceAll e List.sort
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: métodos default adicionados às interfaces de coleção do JDK (possíveis
 * graças aos default methods do JSR 335).</p>
 * <p>Benefício: iterar e remover com segurança sem Iterator explícito e sem
 * {@code ConcurrentModificationException}.</p>
 * <p>Antes: remover durante for-each lançava ConcurrentModificationException; era preciso
 * usar {@code Iterator.remove()} manualmente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/collections/IterableForEachRemoveIfExample.java
 * </pre>
 */
public class IterableForEachRemoveIfExample {

    public static void main(String[] args) {
        List<String> cart = new ArrayList<>(Arrays.asList("mouse", "CUPOM-EXPIRADO", "teclado", "CUPOM-EXPIRADO", "cabo"));

        System.out.println("=== Erro clássico: remover dentro do for-each ===");
        List<String> broken = new ArrayList<>(cart);
        try {
            for (String item : broken) {
                if (item.startsWith("CUPOM")) {
                    broken.remove(item);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("ConcurrentModificationException!");
        }

        System.out.println();
        System.out.println("=== Abordagem antiga correta: Iterator.remove ===");
        List<String> old = new ArrayList<>(cart);
        Iterator<String> it = old.iterator();
        while (it.hasNext()) {
            if (it.next().startsWith("CUPOM")) {
                it.remove();
            }
        }
        System.out.println(old);

        System.out.println();
        System.out.println("=== Java 8: removeIf ===");
        boolean removed = cart.removeIf(item -> item.startsWith("CUPOM"));
        System.out.println("Removeu algo? " + removed + " -> " + cart);

        System.out.println();
        System.out.println("=== Java 8: replaceAll, sort e forEach ===");
        cart.replaceAll(String::toUpperCase);
        cart.sort(String::compareTo);
        cart.forEach(item -> System.out.println("  item: " + item));
    }
}
