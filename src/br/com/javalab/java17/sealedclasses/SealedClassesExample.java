package br.com.javalab.java17.sealedclasses;

import java.util.Arrays;
import java.util.List;

/**
 * Java 17 - Sealed Classes (JEP 409)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 15 (JEP 360),
 * segunda Preview no Java 16 (JEP 397).</p>
 *
 * <p>O que é: uma classe ou interface {@code sealed} declara, com {@code permits},
 * exatamente quais tipos podem estendê-la/implementá-la. Cada subtipo permitido
 * precisa ser {@code final}, {@code sealed} ou {@code non-sealed}.</p>
 * <p>Benefício: o autor do tipo controla a hierarquia (modelagem de domínio fechada),
 * o compilador conhece todos os subtipos e, a partir do Java 21, consegue verificar
 * exaustividade em {@code switch} com patterns.</p>
 * <p>Antes: só havia {@code final} (ninguém estende) ou classe aberta (qualquer um estende);
 * o truque do construtor package-private não funcionava para interfaces.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/sealedclasses/SealedClassesExample.java
 * </pre>
 */
public class SealedClassesExample {

    /** Forma de pagamento aceita pela loja: só estes três tipos podem existir. */
    sealed interface Payment permits CreditCard, Pix, Voucher {
        double amount();
    }

    /** final: ninguém mais estende. Records são implicitamente final. */
    record CreditCard(double amount, int installments) implements Payment { }

    /** final: a hierarquia termina aqui. */
    static final class Pix implements Payment {
        private final double amount;
        private final String key;

        Pix(double amount, String key) {
            this.amount = amount;
            this.key = key;
        }

        public double amount() { return amount; }
        String key() { return key; }
    }

    /** non-sealed: reabre a hierarquia a partir deste ponto (qualquer classe pode estender Voucher). */
    static non-sealed class Voucher implements Payment {
        private final double amount;

        Voucher(double amount) { this.amount = amount; }

        public double amount() { return amount; }
    }

    /** Permitido porque Voucher é non-sealed. */
    static class BirthdayVoucher extends Voucher {
        BirthdayVoucher(double amount) { super(amount); }
    }

    // A linha abaixo NÃO compila: Boleto não está na lista "permits" de Payment.
    // static final class Boleto implements Payment { public double amount() { return 0; } }

    public static void main(String[] args) {
        System.out.println("=== Hierarquia selada ===");
        System.out.println("Payment é sealed? " + Payment.class.isSealed());
        System.out.println("Subtipos permitidos: " + Arrays.stream(Payment.class.getPermittedSubclasses())
                .map(Class::getSimpleName).toList());
        System.out.println("Voucher é sealed? " + Voucher.class.isSealed() + " (non-sealed)");

        System.out.println();
        System.out.println("=== Processando pagamentos (Java 17: instanceof com pattern) ===");
        List<Payment> payments = List.of(
                new CreditCard(300.0, 3),
                new Pix(89.90, "cliente@email.com"),
                new BirthdayVoucher(50.0));

        for (Payment payment : payments) {
            System.out.println(describe(payment));
        }
    }

    // No Java 17 o switch com patterns ainda era Preview (JEP 406); por isso usamos instanceof.
    // O compilador ainda não exigia exaustividade aqui - veja RegularVsSealedClassesComparison.
    static String describe(Payment payment) {
        if (payment instanceof CreditCard card) {
            return "Cartão: R$ %.2f em %dx".formatted(card.amount(), card.installments());
        } else if (payment instanceof Pix pix) {
            return "Pix: R$ %.2f para a chave %s".formatted(pix.amount(), pix.key());
        } else if (payment instanceof Voucher voucher) {
            return "Vale (" + voucher.getClass().getSimpleName() + "): R$ %.2f".formatted(voucher.amount());
        }
        throw new IllegalStateException("Impossível: Payment é sealed");
    }
}
