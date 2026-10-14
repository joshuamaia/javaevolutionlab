package br.com.javalab.java05.staticimport;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;

/**
 * Java 5 - Static Import (JSR 201)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code import static} traz membros estáticos (constantes e métodos) para
 * o escopo, permitindo usá-los sem qualificar pela classe.</p>
 * <p>Benefício: fórmulas e DSLs mais legíveis ({@code max(a, b)}, {@code PI},
 * asserts do JUnit 4, {@code SECONDS}) e fim do "Constant Interface antipattern".</p>
 * <p>Antes: escrever sempre {@code Math.max}, ou implementar uma interface só para
 * "herdar" constantes (antipadrão: polui a API pública da classe).</p>
 * <p>Recomendação: usar com moderação; prefira importar membros específicos a
 * {@code import static X.*}, para não esconder a origem dos nomes.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/staticimport/StaticImportExample.java
 * </pre>
 */
public class StaticImportExample {

    /** Antipadrão antigo: interface usada apenas como "saco de constantes". */
    interface ShippingConstants {
        double FREE_SHIPPING_THRESHOLD = 200.0;
    }

    /** Implementar a interface só para usar a constante sem prefixo (não faça isso). */
    static class OldShippingCalculator implements ShippingConstants {
        boolean isFree(double total) {
            return total >= FREE_SHIPPING_THRESHOLD;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: nomes qualificados ===");
        double radius = 3.0;
        double areaOld = Math.round(Math.PI * Math.pow(radius, 2) * 100) / 100.0;
        System.out.println("Área (Math.xxx): " + areaOld);
        System.out.println("Maior preço: " + Math.max(19.9, 24.5));
        System.out.println("Frete grátis (constant interface)? " + new OldShippingCalculator().isFree(250));

        System.out.println();
        System.out.println("=== Abordagem nova: import static ===");
        double area = round(PI * pow(radius, 2) * 100) / 100.0;
        System.out.println("Área (static import): " + area);
        System.out.println("Maior preço: " + max(19.9, 24.5));

        List<String> customers = asList("Carla", "Ana", "Bruno");
        sort(customers);
        System.out.println("Clientes ordenados: " + customers);

        long timeoutMs = SECONDS.toMillis(30);
        System.out.println("Timeout de 30 s em ms: " + timeoutMs);
    }
}
