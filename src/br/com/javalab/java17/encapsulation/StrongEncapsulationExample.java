package br.com.javalab.java17.encapsulation;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

/**
 * Java 17 - Strongly Encapsulate JDK Internals (JEP 403)
 *
 * <p>Status: Definitivo. Histórico: Java 9 introduziu módulos com acesso "relaxado" por padrão
 * ({@code --illegal-access=permit}); Java 16 (JEP 396) mudou o padrão para {@code deny};
 * Java 17 removeu a opção {@code --illegal-access} por completo.</p>
 *
 * <p>O que é: código fora do JDK não consegue mais usar reflexão profunda
 * ({@code setAccessible(true)}) em membros não públicos de pacotes internos do JDK,
 * como o campo privado {@code value} de {@link String}. A tentativa lança
 * {@link InaccessibleObjectException}. ({@code sun.misc.Unsafe} continua acessível via
 * módulo {@code jdk.unsupported}, mas também está sendo restringido em versões posteriores.)</p>
 * <p>Benefício: segurança e manutenibilidade - bibliotecas não dependem de detalhes internos
 * que podem mudar a qualquer versão.</p>
 * <p>Antes: era possível (com aviso "illegal reflective access" no 9-15) alterar campos privados do JDK.</p>
 *
 * <p>Escape explícito, quando realmente necessário (ex.: frameworks antigos):</p>
 * <pre>
 * java --add-opens java.base/java.lang=ALL-UNNAMED -cp ... MinhaClasse
 * </pre>
 * <p>Também é possível declarar {@code Add-Opens:} no MANIFEST de um JAR executável.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/encapsulation/StrongEncapsulationExample.java
 * </pre>
 */
public class StrongEncapsulationExample {

    /** Classe nossa: reflexão profunda continua permitida dentro do próprio código. */
    static class Customer {
        private String document = "123.456.789-00";
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Reflexão profunda em código próprio: permitido ===");
        Customer customer = new Customer();
        Field document = Customer.class.getDeclaredField("document");
        document.setAccessible(true);
        System.out.println("Campo privado de Customer: " + document.get(customer));

        System.out.println();
        System.out.println("=== Reflexão profunda em java.lang.String: bloqueado ===");
        Field value = String.class.getDeclaredField("value");
        System.out.println("Campo encontrado: " + value);
        try {
            value.setAccessible(true);
            System.out.println("setAccessible funcionou (a JVM foi iniciada com --add-opens?)");
        } catch (InaccessibleObjectException e) {
            System.out.println("InaccessibleObjectException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== trySetAccessible: versão sem exceção ===");
        System.out.println("value.trySetAccessible() = " + value.trySetAccessible());

        System.out.println();
        System.out.println("=== Por quê? ===");
        Module javaBase = String.class.getModule();
        Module ours = StrongEncapsulationExample.class.getModule();
        System.out.println("String está no módulo: " + javaBase.getName());
        System.out.println("java.lang exportado para nós? " + javaBase.isExported("java.lang", ours));
        System.out.println("java.lang ABERTO (opens) para nós? " + javaBase.isOpen("java.lang", ours));
        System.out.println("Para liberar: --add-opens java.base/java.lang=ALL-UNNAMED");
    }
}
