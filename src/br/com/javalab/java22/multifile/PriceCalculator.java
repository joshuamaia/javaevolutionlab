package br.com.javalab.java22.multifile;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Classe auxiliar do exemplo {@link MultiFileLauncherExample} (Java 22 - JEP 458).
 *
 * <p>Não é um exemplo executável: existe em um arquivo SEPARADO justamente para mostrar
 * que o launcher de código-fonte do Java 22 encontra e compila sob demanda outras classes
 * do mesmo pacote, a partir da raiz de código-fonte deduzida pelo pacote do arquivo
 * principal.</p>
 */
public class PriceCalculator {

    private final BigDecimal discountPercent;

    public PriceCalculator(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal finalPrice(BigDecimal unitPrice, int quantity) {
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = gross.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return gross.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
}
