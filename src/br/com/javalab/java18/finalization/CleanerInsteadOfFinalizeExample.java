package br.com.javalab.java18.finalization;

import java.lang.ref.Cleaner;

/**
 * Java 18 - Deprecate Finalization for Removal (JEP 421)
 *
 * <p>Status: Deprecated (para remoção). {@code Object.finalize()} já era deprecated desde o Java 9;
 * no 18 passou a {@code forRemoval=true}. Pode-se testar a ausência da finalização com
 * {@code java --finalization=disabled ...}.</p>
 *
 * <p>O que é: a finalização ({@code finalize()}) roda em momento imprevisível, numa thread
 * qualquer, pode "ressuscitar" objetos, atrasa o GC e pode nem rodar. Será removida.</p>
 * <p>Alternativas: (1) try-with-resources com {@link AutoCloseable} para liberar recursos de forma
 * determinística; (2) {@link Cleaner} (Java 9) como rede de segurança quando alguém esquece
 * de chamar {@code close()}.</p>
 * <p>Antes: sobrescrever {@code protected void finalize()} para fechar arquivos/conexões.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java18/finalization/CleanerInsteadOfFinalizeExample.java
 * java --finalization=disabled src/br/com/javalab/java18/finalization/CleanerInsteadOfFinalizeExample.java
 * </pre>
 */
public class CleanerInsteadOfFinalizeExample {

    private static final Cleaner CLEANER = Cleaner.create();

    /** Antes: depender de finalize() (gera aviso de remoção na compilação). */
    static class LegacyConnection {
        @SuppressWarnings({"removal", "deprecation"})
        @Override
        protected void finalize() {
            System.out.println("   [finalize] LegacyConnection fechada (quando? ninguém sabe)");
        }
    }

    /** Agora: AutoCloseable + Cleaner como rede de segurança. */
    static class DatabaseConnection implements AutoCloseable {

        /** Estado a limpar: NÃO pode referenciar o DatabaseConnection (senão ele nunca vira lixo). */
        private record State(String name) implements Runnable {
            @Override
            public void run() {
                System.out.println("   [cleaner] liberando recursos de " + name);
            }
        }

        private final Cleaner.Cleanable cleanable;

        DatabaseConnection(String name) {
            System.out.println("   abrindo " + name);
            this.cleanable = CLEANER.register(this, new State(name));
        }

        @Override
        public void close() {
            cleanable.clean(); // executa a ação no máximo uma vez
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Forma recomendada: try-with-resources (determinístico) ===");
        try (DatabaseConnection connection = new DatabaseConnection("conexão-pedidos")) {
            System.out.println("   consultando pedidos...");
        }

        System.out.println();
        System.out.println("=== Rede de segurança: alguém esqueceu o close() ===");
        new DatabaseConnection("conexão-esquecida"); // fica inalcançável imediatamente
        new LegacyConnection();
        for (int i = 0; i < 5; i++) {
            System.gc(); // apenas uma sugestão; a limpeza pelo Cleaner/finalize não é garantida
            Thread.sleep(100);
        }
        System.out.println("(A ordem e a presença das mensagens acima dependem do GC - por isso não confie nelas.)");
        System.out.println("Com --finalization=disabled, a mensagem de [finalize] nunca aparece; a do [cleaner] sim.");
    }
}
