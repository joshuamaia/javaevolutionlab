package br.com.javalab.java15.sealed;

/**
 * Java 15 - Sealed Classes (JEP 360)
 *
 * <p>Status: Preview. Segunda Preview no Java 16 (JEP 397); Definitivo no Java 17
 * (JEP 409). No Java 15 exigia {@code --enable-preview --source 15}; a sintaxe usada
 * aqui (sealed, permits, final, non-sealed) permaneceu igual e compila no JDK 25 sem
 * flags. Obs.: a API de reflexao do Java 15 era {@code Class.permittedSubclasses()}
 * devolvendo {@code ClassDesc[]}; no Java 16 mudou para {@code getPermittedSubclasses()}
 * devolvendo {@code Class<?>[]} (ver java16/sealed).</p>
 *
 * <p>O que e: uma classe/interface {@code sealed} declara EXATAMENTE quais tipos podem
 * estende-la ({@code permits}). Cada subtipo permitido deve ser {@code final},
 * {@code sealed} (restringe de novo) ou {@code non-sealed} (reabre a hierarquia).</p>
 * <p>Beneficio: modelar dominios fechados (ex.: formas de pagamento) com garantia do
 * compilador; base para a exaustividade do pattern matching em switch (Java 21).</p>
 * <p>Antes: so era possivel escolher entre "final" (ninguem estende) e aberto (todos
 * estendem); truques como construtor package-private.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/sealed/SealedClassesPreviewExample.java
 * </pre>
 */
public class SealedClassesPreviewExample {

    /** Hierarquia fechada: apenas estes tres tipos de pagamento existem. */
    abstract static sealed class Payment permits CardPayment, PixPayment, VoucherPayment {
        final double amount;

        Payment(double amount) {
            this.amount = amount;
        }
    }

    /** final: ninguem mais estende. */
    static final class PixPayment extends Payment {
        PixPayment(double amount) { super(amount); }
    }

    /** sealed de novo: restringe a credito e debito. */
    abstract static sealed class CardPayment extends Payment permits CreditCard, DebitCard {
        CardPayment(double amount) { super(amount); }
    }

    static final class CreditCard extends CardPayment {
        final int installments;
        CreditCard(double amount, int installments) { super(amount); this.installments = installments; }
    }

    static final class DebitCard extends CardPayment {
        DebitCard(double amount) { super(amount); }
    }

    /** non-sealed: parceiros podem criar vouchers proprios. */
    static non-sealed class VoucherPayment extends Payment {
        VoucherPayment(double amount) { super(amount); }
    }

    static class PartnerVoucher extends VoucherPayment {
        PartnerVoucher(double amount) { super(amount); }
    }

    // class BoletoPayment extends Payment {}  // ERRO: BoletoPayment nao esta em "permits"

    public static void main(String[] args) {
        Payment[] payments = {
                new PixPayment(100.0), new CreditCard(1200.0, 3),
                new DebitCard(80.0), new PartnerVoucher(50.0)};

        System.out.println("=== Processando hierarquia fechada (Java 15: cadeia de instanceof) ===");
        for (Payment payment : payments) {
            System.out.printf("%-15s R$ %8.2f -> %s%n",
                    payment.getClass().getSimpleName(), payment.amount, describe(payment));
        }

        System.out.println("=== sealed / final / non-sealed ===");
        System.out.println("Payment e sealed?        " + Payment.class.isSealed());
        System.out.println("CardPayment e sealed?    " + CardPayment.class.isSealed());
        System.out.println("VoucherPayment e sealed? " + VoucherPayment.class.isSealed() + " (non-sealed)");
    }

    private static String describe(Payment payment) {
        if (payment instanceof PixPayment) {
            return "aprovacao instantanea";
        } else if (payment instanceof CreditCard) {
            return "credito em " + ((CreditCard) payment).installments + "x";
        } else if (payment instanceof DebitCard) {
            return "debito em conta";
        } else if (payment instanceof VoucherPayment) {
            return "voucher (hierarquia aberta a partir daqui)";
        }
        // No Java 15 o compilador ainda nao usa "sealed" para exaustividade:
        // este throw continua necessario (switch exaustivo so no Java 21).
        throw new IllegalStateException("tipo inesperado");
    }
}
