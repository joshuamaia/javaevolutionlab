package br.com.javalab.java22.constructors;

/**
 * Java 22 - Statements before super(...) (JEP 447)
 *
 * <p>Status: Preview. Evolução: renomeado para "Flexible Constructor Bodies" na segunda
 * Preview do Java 23 (JEP 482), terceira Preview no Java 24 (JEP 492) e definitivo no
 * Java 25 (JEP 513). Este código usa só o que já era permitido no 22 e compila no JDK 25
 * sem flags (no JDK 22 exigia {@code --enable-preview}).</p>
 *
 * <p>O que é: permite instruções ANTES da chamada explícita a {@code super(...)} ou
 * {@code this(...)} - desde que não usem a instância em construção ({@code this}), ou seja,
 * não leiam campos, não chamem métodos de instância. Na Preview do 22, também não era
 * possível atribuir campos antes do {@code super(...)} (isso chegou no 23).</p>
 * <p>Benefício: validar/preparar argumentos antes de chamar o construtor da superclasse
 * (falhar rápido, sem executar trabalho caro na superclasse por nada).</p>
 * <p>Antes: {@code super(...)} tinha de ser a primeira instrução; a validação ficava
 * depois (tarde demais) ou num método estático auxiliar enfiado no argumento.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java22/constructors/StatementsBeforeSuperExample.java
 * </pre>
 */
public class StatementsBeforeSuperExample {

    static class Account {
        final String owner;
        final long balanceInCents;

        Account(String owner, long balanceInCents) {
            System.out.println("  Account(): registrando conta de " + owner + " (operação cara)");
            this.owner = owner;
            this.balanceInCents = balanceInCents;
        }
    }

    // ---- Antes do Java 22: validação via método estático no argumento ----
    static class OldSavingsAccount extends Account {
        OldSavingsAccount(String owner, long balanceInCents) {
            super(owner, requirePositive(balanceInCents)); // super() obrigatoriamente primeiro
        }

        private static long requirePositive(long value) {
            if (value <= 0) throw new IllegalArgumentException("saldo inicial deve ser positivo");
            return value;
        }
    }

    // ---- Java 22: instruções antes do super(...) ----
    static class SavingsAccount extends Account {
        SavingsAccount(String owner, double initialBalance) {
            if (initialBalance <= 0) {                      // valida antes
                throw new IllegalArgumentException("saldo inicial deve ser positivo");
            }
            long cents = Math.round(initialBalance * 100);  // prepara argumento
            String normalizedOwner = owner.strip().toUpperCase();
            super(normalizedOwner, cents);                  // depois chama a superclasse
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (método estático auxiliar) ===");
        OldSavingsAccount old = new OldSavingsAccount("ana", 1000);
        System.out.println("Conta criada: " + old.owner + " " + old.balanceInCents);

        System.out.println();
        System.out.println("=== Instruções antes de super(...) ===");
        SavingsAccount ok = new SavingsAccount("  bruno ", 25.75);
        System.out.println("Conta criada: " + ok.owner + " " + ok.balanceInCents + " centavos");
        try {
            new SavingsAccount("carla", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejeitado ANTES de executar Account(): " + e.getMessage());
        }
    }
}
