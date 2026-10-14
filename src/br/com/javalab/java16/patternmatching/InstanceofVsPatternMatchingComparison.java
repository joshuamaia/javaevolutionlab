package br.com.javalab.java16.patternmatching;

import java.util.List;

/**
 * Java 16 - Comparacao: instanceof tradicional vs pattern matching (JEP 394)
 *
 * <p>Status: Definitivo (Java 16).</p>
 *
 * <p>O que e: a mesma logica de calculo de frete para diferentes tipos de item,
 * escrita com o idioma antigo "instanceof + cast + variavel" e com pattern matching.</p>
 * <p>Problemas da forma antiga: repeticao do tipo tres vezes, cast que pode ser
 * copiado para o tipo errado (ClassCastException em tempo de execucao) e codigo mais
 * longo. Na forma nova o compilador garante que a variavel so existe quando o teste
 * passou.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/patternmatching/InstanceofVsPatternMatchingComparison.java
 * </pre>
 */
public class InstanceofVsPatternMatchingComparison {

    static class Book {
        final int pages;
        Book(int pages) { this.pages = pages; }
    }

    static class Electronic {
        final double weightKg;
        final boolean fragile;
        Electronic(double weightKg, boolean fragile) { this.weightKg = weightKg; this.fragile = fragile; }
    }

    static class GiftCard {
        final double value;
        GiftCard(double value) { this.value = value; }
    }

    public static void main(String[] args) {
        List<Object> items = List.of(new Book(350), new Electronic(2.5, true), new GiftCard(100), "desconhecido");

        System.out.println("=== Abordagem antiga: instanceof + cast ===");
        for (Object item : items) {
            System.out.printf("%-12s -> R$ %.2f%n", item.getClass().getSimpleName(), shippingOld(item));
        }

        System.out.println("=== Java 16: pattern matching ===");
        for (Object item : items) {
            System.out.printf("%-12s -> R$ %.2f%n", item.getClass().getSimpleName(), shippingNew(item));
        }
    }

    static double shippingOld(Object item) {
        if (item instanceof Book) {
            Book book = (Book) item;              // tipo repetido 3 vezes
            return book.pages > 300 ? 15.0 : 10.0;
        } else if (item instanceof Electronic) {
            Electronic e = (Electronic) item;
            if (e.fragile) {                      // condicao extra em outro if
                return 20.0 + e.weightKg * 5;
            }
            return e.weightKg * 5;
        } else if (item instanceof GiftCard) {
            return 0.0;
        }
        return 30.0;
    }

    static double shippingNew(Object item) {
        if (item instanceof Book book) {
            return book.pages > 300 ? 15.0 : 10.0;
        } else if (item instanceof Electronic e && e.fragile) {   // teste + condicao juntos
            return 20.0 + e.weightKg * 5;
        } else if (item instanceof Electronic e) {
            return e.weightKg * 5;
        } else if (item instanceof GiftCard) {
            return 0.0;
        }
        return 30.0;
    }
}
