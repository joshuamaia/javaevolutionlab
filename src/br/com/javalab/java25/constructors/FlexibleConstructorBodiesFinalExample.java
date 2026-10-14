package br.com.javalab.java25.constructors;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Java 25 - Flexible Constructor Bodies (JEP 513)
 *
 * <p>Status: Definitivo. Histórico: Preview no 22 como "Statements before super(...)"
 * (JEP 447), segunda Preview no 23 (JEP 482) e terceira no 24 (JEP 492), sem mudanças
 * desde o 23.</p>
 *
 * <p>O que é: o corpo do construtor tem duas partes - o <b>prólogo</b> (antes de
 * {@code super(...)}/{@code this(...)}) e o <b>epílogo</b> (depois). No prólogo é permitido:
 * validar e calcular com parâmetros e variáveis locais, chamar métodos estáticos,
 * ATRIBUIR campos da própria classe ainda não inicializados. É proibido: ler campos,
 * chamar métodos de instância, usar {@code this} explicitamente ou passá-lo adiante, usar
 * {@code super.campo}.</p>
 * <p>Benefício: construtores mais naturais (falha rápida, preparação de argumentos) e
 * objetos que nunca são observados em estado parcialmente inicializado.</p>
 *
 * <p>Resumo das três Previews (exemplos em java22, java23 e java24): validação antes de
 * super, inicialização de campos antes de super, e prólogo antes de {@code this(...)} em
 * records. Este exemplo junta tudo num caso realista: hierarquia de produtos.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java25/constructors/FlexibleConstructorBodiesFinalExample.java
 * </pre>
 */
public class FlexibleConstructorBodiesFinalExample {

    abstract static class Product {
        private final String sku;

        Product(String sku) {
            this.sku = sku;
            System.out.println("  Product(" + sku + ") -> etiqueta: " + label()); // chama método sobrescrito
        }

        abstract String label();

        String sku() {
            return sku;
        }
    }

    static final class PerishableProduct extends Product {
        private final int shelfLifeDays;
        private final BigDecimal price;

        PerishableProduct(String sku, int shelfLifeDays, String price) {
            // ---- prólogo ----
            Objects.requireNonNull(sku, "sku");                       // validação
            if (shelfLifeDays <= 0) {
                throw new IllegalArgumentException("validade deve ser positiva");
            }
            BigDecimal parsed = new BigDecimal(price);                // cálculo com local
            this.shelfLifeDays = shelfLifeDays;                       // atribuir campo: ok
            this.price = parsed;
            // this.label();          -> erro: método de instância no prólogo
            // int x = this.shelfLifeDays; -> erro: leitura de campo no prólogo
            super(normalize(sku));                                    // método estático: ok
            // ---- epílogo: aqui 'this' pode ser usado livremente ----
            System.out.println("  PerishableProduct pronto: " + label());
        }

        PerishableProduct(String csvLine) {
            String[] cols = csvLine.split(";");                       // prólogo antes de this(...)
            this(cols[0], Integer.parseInt(cols[1]), cols[2]);
        }

        private static String normalize(String sku) {
            return sku.strip().toUpperCase();
        }

        @Override
        String label() {
            // graças ao prólogo, shelfLifeDays e price já estão atribuídos quando Product() chama label()
            return sku() + " | validade " + shelfLifeDays + " dias | R$ " + price;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Construção com prólogo e epílogo ===");
        new PerishableProduct(" iog-001 ", 15, "4.99");

        System.out.println();
        System.out.println("=== Construtor alternativo (CSV) delegando com this(...) ===");
        new PerishableProduct("lei-002;7;6.49");

        System.out.println();
        System.out.println("=== Validação no prólogo: Product() nem chega a rodar ===");
        try {
            new PerishableProduct("pao-003", 0, "1.00");
        } catch (IllegalArgumentException e) {
            System.out.println("Rejeitado: " + e.getMessage());
        }
    }
}
