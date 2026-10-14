package br.com.javalab.java21.patternswitch;

import java.util.Arrays;

/**
 * Java 21 - Pattern Matching for switch (JEP 441)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 17 (JEP 406), 18 (JEP 420), 19 (JEP 427) e 20 (JEP 433).
 * Na versão final saíram os parenthesized patterns e foram adicionadas constantes de enum
 * qualificadas nos rótulos {@code case}.</p>
 *
 * <p>O que é: {@code switch} (instrução ou expressão) sobre qualquer tipo de referência, com
 * type patterns, record patterns, guards {@code when}, {@code case null} e verificação de
 * exaustividade para hierarquias sealed (sem {@code default}).</p>
 * <p>Benefício: substitui cadeias de {@code if/else instanceof} por um único switch legível e
 * verificado pelo compilador; o "visitor pattern" deixa de ser necessário em muitos casos.</p>
 * <p>Antes: {@code if (o instanceof A) {...} else if (o instanceof B) {...} else {...}} e switch
 * só para primitivos, String e enum (que lançava NPE com null).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java21/patternswitch/PatternMatchingSwitchExample.java
 * </pre>
 */
public class PatternMatchingSwitchExample {

    sealed interface PaymentMethod permits Card, Pix, BankSlip { }
    enum Card implements PaymentMethod { CREDIT, DEBIT }
    record Pix(String key) implements PaymentMethod { }
    record BankSlip(String barcode, int daysToExpire) implements PaymentMethod { }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: if/else com instanceof ===");
        System.out.println(oldStyle(new Pix("ana@email.com")));

        System.out.println();
        System.out.println("=== switch com patterns, guards, enum qualificado e case null ===");
        PaymentMethod[] methods = {
                Card.CREDIT, Card.DEBIT, new Pix("ana@email.com"),
                new BankSlip("34191.79001", 0), new BankSlip("34191.79002", 3), null};
        Arrays.stream(methods).forEach(m -> System.out.println(fee(m)));

        System.out.println();
        System.out.println("=== switch sobre Object (tipos não relacionados) ===");
        for (Object value : new Object[] {42, 1_500_000L, "Pix", 3.5, new int[] {1, 2}}) {
            System.out.println(value + " -> " + classify(value));
        }
    }

    static String oldStyle(Object method) {
        if (method instanceof Pix) {
            Pix pix = (Pix) method;
            return "Pix para " + pix.key();
        } else if (method instanceof BankSlip) {
            return "Boleto";
        } else {
            return "Outro";
        }
    }

    /** Exaustivo sem default: Card (enum), Pix e BankSlip cobrem todos os subtipos de PaymentMethod. */
    static String fee(PaymentMethod method) {
        return switch (method) {
            case null -> "Nenhum método informado";
            case Card.CREDIT -> "Cartão de crédito: taxa 3,5%";   // constante de enum qualificada (novo no 21)
            case Card.DEBIT -> "Cartão de débito: taxa 1,2%";
            case Pix(var key) -> "Pix (" + key + "): sem taxa";
            case BankSlip(var code, var days) when days <= 0 -> "Boleto " + code + " VENCIDO";
            case BankSlip(var code, var days) -> "Boleto " + code + " vence em " + days + " dias: taxa R$ 2,90";
        };
    }

    static String classify(Object value) {
        return switch (value) {
            case Integer i when i > 0 -> "inteiro positivo";
            case Integer i -> "inteiro";
            case Long l when l > 1_000_000 -> "long grande";
            case String s -> "texto de " + s.length() + " caracteres";
            case Number n -> "outro número (" + n.getClass().getSimpleName() + ")";
            default -> "tipo não tratado: " + value.getClass().getSimpleName();
        };
    }
}
