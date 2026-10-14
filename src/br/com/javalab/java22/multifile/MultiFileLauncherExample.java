package br.com.javalab.java22.multifile;

import java.math.BigDecimal;

/**
 * Java 22 - Launch Multi-File Source-Code Programs (JEP 458)
 *
 * <p>Status: Definitivo. Histórico: o Java 11 (JEP 330) permitiu executar UM arquivo
 * {@code .java} diretamente com {@code java Arquivo.java}.</p>
 *
 * <p>O que é: o launcher de código-fonte passa a aceitar programas com vários arquivos.
 * Ao executar {@code java .../MultiFileLauncherExample.java}, o launcher compila esse arquivo
 * em memória e, quando ele referencia {@code PriceCalculator}, procura
 * {@code PriceCalculator.java} na árvore de código-fonte (a raiz é deduzida a partir do
 * pacote: {@code src/}) e o compila sob demanda.</p>
 * <p>Benefício: protótipos e programas pequenos crescem para vários arquivos sem precisar
 * de {@code javac}, Maven ou Gradle.</p>
 * <p>Antes (Java 11-21): o modo código-fonte só via o próprio arquivo; usar outra classe
 * exigia compilá-la antes com {@code javac} e passar {@code -cp}.</p>
 *
 * <p>Execução (JDK 25), a partir da raiz do projeto:</p>
 * <pre>
 * java src/br/com/javalab/java22/multifile/MultiFileLauncherExample.java
 * </pre>
 * <p>(No IntelliJ roda normalmente, pois ele compila todo o {@code src/}.)</p>
 */
public class MultiFileLauncherExample {

    public static void main(String[] args) {
        System.out.println("=== Programa com dois arquivos executado pelo launcher ===");
        PriceCalculator calculator = new PriceCalculator(new BigDecimal("10")); // 10% de desconto
        BigDecimal total = calculator.finalPrice(new BigDecimal("59.90"), 3);
        System.out.println("3 x 59.90 com 10% de desconto = " + total);

        // De onde veio a classe auxiliar? No modo código-fonte, de um class loader em memória.
        ClassLoader loader = PriceCalculator.class.getClassLoader();
        System.out.println("PriceCalculator carregada por: " + loader.getClass().getName());
    }
}
