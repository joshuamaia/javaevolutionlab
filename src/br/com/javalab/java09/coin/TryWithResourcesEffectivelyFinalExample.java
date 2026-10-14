package br.com.javalab.java09.coin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Java 9 - try-with-resources com variável effectively final (JEP 213 - Milling Project Coin)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o {@code try(...)} aceita uma variável já declarada, desde que ela seja
 * {@code final} ou <i>effectively final</i> (nunca reatribuída).</p>
 * <p>Benefício: quando o recurso vem de fora (parâmetro, fábrica, campo final) não é
 * preciso declarar uma segunda variável só para o try.</p>
 * <p>Antes (Java 7/8): {@code try (BufferedReader r = reader) { ... }} — variável
 * duplicada apenas por exigência da sintaxe.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/coin/TryWithResourcesEffectivelyFinalExample.java
 * </pre>
 */
public class TryWithResourcesEffectivelyFinalExample {

    /** Recurso que avisa quando é fechado. */
    static class AuditLog implements AutoCloseable {
        private final String name;

        AuditLog(String name) {
            this.name = name;
        }

        void write(String message) {
            System.out.println("  [" + name + "] " + message);
        }

        @Override
        public void close() {
            System.out.println("  [" + name + "] fechado");
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("pedido-1\npedido-2"));
        AuditLog audit = new AuditLog("auditoria");

        System.out.println("=== Abordagem antiga (Java 7/8): nova variável dentro do try ===");
        AuditLog oldAudit = new AuditLog("auditoria-antiga");
        try (AuditLog log = oldAudit) {
            log.write("processando...");
        }

        System.out.println();
        System.out.println("=== Java 9: variáveis existentes direto no try ===");
        try (reader; audit) {
            String line;
            while ((line = reader.readLine()) != null) {
                audit.write("lido " + line);
            }
        }
        System.out.println("Recursos fechados na ordem inversa da declaração (audit, depois reader).");

        // reader = null;  // se descomentar, 'reader' deixa de ser effectively final e o try não compila
    }
}
