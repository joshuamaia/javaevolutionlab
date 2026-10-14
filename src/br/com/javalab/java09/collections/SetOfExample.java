package br.com.javalab.java09.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Java 9 - Set.of: fábrica de conjuntos imutáveis (JEP 269)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code Set.of(...)} cria um conjunto imutável.</p>
 * <p>Benefício: declaração concisa de conjuntos constantes (perfis, categorias, flags).</p>
 * <p>Antes: {@code Collections.unmodifiableSet(new HashSet<>(Arrays.asList(...)))}.</p>
 *
 * <p>Regras: elementos duplicados lançam {@code IllegalArgumentException} (provavelmente
 * é um bug no código); {@code null} é proibido; a ordem de iteração é <b>não especificada</b>
 * e pode mudar entre execuções.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/collections/SetOfExample.java
 * </pre>
 */
public class SetOfExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (Java 8) ===");
        Set<String> oldRoles = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList("ADMIN", "VENDEDOR", "CLIENTE")));
        System.out.println("Perfis: " + oldRoles);

        System.out.println();
        System.out.println("=== Abordagem nova (Java 9): Set.of ===");
        Set<String> roles = Set.of("ADMIN", "VENDEDOR", "CLIENTE");
        System.out.println("Perfis: " + roles + "  (ordem não garantida)");
        System.out.println("Contém ADMIN? " + roles.contains("ADMIN"));

        System.out.println();
        System.out.println("=== Duplicatas são erro ===");
        try {
            Set.of("ADMIN", "ADMIN");
        } catch (IllegalArgumentException e) {
            System.out.println("Set.of(\"ADMIN\", \"ADMIN\") -> IllegalArgumentException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Imutável e sem null ===");
        try {
            roles.remove("ADMIN");
        } catch (UnsupportedOperationException e) {
            System.out.println("remove() -> UnsupportedOperationException");
        }
        try {
            Set.of("ADMIN", null);
        } catch (NullPointerException e) {
            System.out.println("Set.of(\"ADMIN\", null) -> NullPointerException");
        }
    }
}
