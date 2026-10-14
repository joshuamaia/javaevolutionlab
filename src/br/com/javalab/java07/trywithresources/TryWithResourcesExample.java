package br.com.javalab.java07.trywithresources;

/**
 * Java 7 - Try-with-resources (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: recursos declarados no cabeçalho do {@code try} que implementam
 * {@link AutoCloseable} são fechados automaticamente, na ordem inversa da abertura,
 * mesmo quando ocorre exceção.</p>
 * <p>Benefício: elimina blocos {@code finally} verbosos e propensos a erro; exceções
 * lançadas no {@code close()} não escondem a exceção principal (ficam como "suppressed").</p>
 * <p>Antes: try/finally manual, com null-checks e try/catch aninhado dentro do finally;
 * a exceção do finally <b>substituía</b> a exceção original (que era perdida).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/trywithresources/TryWithResourcesExample.java
 * </pre>
 */
public class TryWithResourcesExample {

    /** Recurso próprio: simula uma conexão com banco que pode falhar ao fechar. */
    static class DatabaseConnection implements AutoCloseable {
        private final String name;
        private final boolean failOnClose;

        DatabaseConnection(String name, boolean failOnClose) {
            this.name = name;
            this.failOnClose = failOnClose;
            System.out.println("  abrindo " + name);
        }

        void query(String sql, boolean fail) {
            System.out.println("  [" + name + "] executando: " + sql);
            if (fail) {
                throw new IllegalStateException("Falha ao executar '" + sql + "'");
            }
        }

        @Override
        public void close() {
            System.out.println("  fechando " + name);
            if (failOnClose) {
                throw new IllegalStateException("Falha ao fechar " + name);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: try/finally ===");
        try {
            oldStyle();
        } catch (RuntimeException e) {
            System.out.println("Exceção recebida: " + e.getMessage());
            System.out.println("-> a exceção da query foi PERDIDA (substituída pela do close)");
        }

        System.out.println();
        System.out.println("=== Java 7: try-with-resources (dois recursos) ===");
        try (DatabaseConnection orders = new DatabaseConnection("orders-db", false);
             DatabaseConnection audit = new DatabaseConnection("audit-db", false)) {
            orders.query("SELECT * FROM orders", false);
            audit.query("INSERT INTO audit ...", false);
        }
        System.out.println("(observe: fechados na ordem inversa da abertura)");

        System.out.println();
        System.out.println("=== Java 7: exceção principal + suppressed ===");
        try (DatabaseConnection conn = new DatabaseConnection("orders-db", true)) {
            conn.query("UPDATE orders SET status='PAID'", true);
        } catch (IllegalStateException e) {
            System.out.println("Exceção principal: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("  suppressed: " + suppressed.getMessage());
            }
        }
    }

    private static void oldStyle() {
        DatabaseConnection conn = null;
        try {
            conn = new DatabaseConnection("legacy-db", true);
            conn.query("UPDATE orders SET status='PAID'", true);
        } finally {
            if (conn != null) {
                conn.close(); // esta exceção substitui a exceção da query
            }
        }
    }
}
