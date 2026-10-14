package br.com.javalab.java16.encapsulation;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

/**
 * Java 16 - Strongly Encapsulate JDK Internals by Default (JEP 396)
 *
 * <p>Status: Definitivo. O padrao de {@code --illegal-access} mudou de "permit"
 * (Java 9 a 15: acesso reflexivo a internos permitido com aviso) para "deny".
 * No Java 17 (JEP 403) a opcao --illegal-access deixou de ter efeito.</p>
 *
 * <p>O que e: codigo no classpath nao pode mais usar reflexao profunda
 * ({@code setAccessible(true)}) em membros privados de pacotes internos do JDK nem em
 * membros privados de classes de pacotes exportados (ex.: o array {@code value}
 * de {@code String}). Excecao: {@code sun.misc.Unsafe} (modulo jdk.unsupported).</p>
 * <p>Beneficio: seguranca e liberdade para o JDK evoluir sua implementacao interna.</p>
 * <p>Antes: bibliotecas acessavam internos livremente; liberar hoje so de forma
 * explicita: {@code --add-opens java.base/java.lang=ALL-UNNAMED}.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/encapsulation/StrongEncapsulationExample.java
 * java --add-opens java.base/java.lang=ALL-UNNAMED src/br/com/javalab/java16/encapsulation/StrongEncapsulationExample.java
 * </pre>
 */
public class StrongEncapsulationExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Reflexao publica continua funcionando ===");
        System.out.println("String.length() via reflexao: "
                + String.class.getMethod("length").invoke("Notebook"));

        System.out.println("=== Reflexao profunda em campo privado do JDK ===");
        Field value = String.class.getDeclaredField("value");
        try {
            value.setAccessible(true);
            byte[] bytes = (byte[]) value.get("Notebook");
            System.out.println("Acesso liberado (via --add-opens): " + bytes.length + " bytes internos");
        } catch (InaccessibleObjectException e) {
            System.out.println("Bloqueado: " + e.getClass().getSimpleName());
            System.out.println("Mensagem: " + e.getMessage());
        }

        System.out.println("=== Pacote interno nao exportado ===");
        try {
            Class<?> internal = Class.forName("jdk.internal.misc.VM");
            internal.getMethod("isBooted").invoke(null);
            System.out.println("Acesso permitido (inesperado sem --add-exports)");
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: jdk.internal.misc nao e exportado");
        }
    }
}
