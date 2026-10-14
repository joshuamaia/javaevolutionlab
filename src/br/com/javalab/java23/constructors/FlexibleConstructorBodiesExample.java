package br.com.javalab.java23.constructors;

/**
 * Java 23 - Flexible Constructor Bodies, segunda Preview (JEP 482)
 *
 * <p>Status: Segunda Preview. Histórico: Preview no Java 22 como "Statements before
 * super(...)" (JEP 447); terceira Preview no 24 (JEP 492) e definitivo no 25 (JEP 513).
 * Compila no JDK 25 sem flags (no JDK 23 exigia {@code --enable-preview}).</p>
 *
 * <p>O que mudou em relação ao Java 22: além de instruções que não usam {@code this}, o
 * "prólogo" do construtor (antes do {@code super(...)}) agora pode ATRIBUIR campos da própria
 * classe. Assim, se o construtor da superclasse chamar um método sobrescrito, a subclasse
 * já enxerga seus campos inicializados - em vez de {@code null}/zero.</p>
 * <p>Benefício: elimina uma classe clássica de bugs ("método sobrescrito chamado pelo
 * construtor da superclasse vê campos não inicializados") e fortalece a integridade de
 * campos {@code final}.</p>
 * <p>Antes: o campo só podia ser atribuído depois do {@code super(...)}, tarde demais.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java23/constructors/FlexibleConstructorBodiesExample.java
 * </pre>
 */
public class FlexibleConstructorBodiesExample {

    /** Superclasse que chama um método sobrescrevível no construtor (padrão comum em frameworks). */
    abstract static class Notification {
        Notification() {
            System.out.println("  Notification(): " + describe());
        }

        abstract String describe();
    }

    // ---- Antes: campo atribuído depois do super() ----
    static class OldEmailNotification extends Notification {
        private final String recipient;

        OldEmailNotification(String recipient) {
            super();                       // describe() roda aqui e vê recipient == null
            this.recipient = recipient;
        }

        @Override
        String describe() {
            return "e-mail para " + recipient;
        }
    }

    // ---- Java 23: campo atribuído no prólogo, antes do super() ----
    static class EmailNotification extends Notification {
        private final String recipient;

        EmailNotification(String recipient) {
            if (!recipient.contains("@")) {
                throw new IllegalArgumentException("e-mail inválido: " + recipient);
            }
            this.recipient = recipient.toLowerCase(); // permitido: atribuir campo antes do super
            // System.out.println(this.recipient);    // proibido: LER campo antes do super
            super();
        }

        @Override
        String describe() {
            return "e-mail para " + recipient;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga ===");
        new OldEmailNotification("ana@loja.com");

        System.out.println();
        System.out.println("=== Campo inicializado antes de super() ===");
        new EmailNotification("Bruno@Loja.com");

        try {
            new EmailNotification("sem-arroba");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejeitado: " + e.getMessage());
        }
    }
}
