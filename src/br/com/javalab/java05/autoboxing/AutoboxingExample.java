package br.com.javalab.java05.autoboxing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 5 - Autoboxing e Unboxing (JSR 201)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: conversão automática entre primitivos e seus wrappers
 * ({@code int <-> Integer}, {@code double <-> Double}...). O compilador insere
 * {@code Integer.valueOf(x)} (boxing) e {@code x.intValue()} (unboxing).</p>
 * <p>Benefício: usar primitivos em coleções genéricas sem conversões manuais.</p>
 * <p>Antes: {@code list.add(new Integer(5))} e {@code ((Integer) list.get(0)).intValue()}.</p>
 * <p>Armadilhas: {@code ==} entre wrappers compara referências (só funciona "por acaso"
 * no cache de -128 a 127), unboxing de {@code null} gera {@code NullPointerException},
 * boxing em laços quentes custa desempenho e {@code List<Integer>.remove(int)} remove
 * por índice, não por valor.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/autoboxing/AutoboxingExample.java
 * </pre>
 */
public class AutoboxingExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: conversão manual ===");
        List<Integer> quantities = new ArrayList<Integer>();
        // No Java 1.4 seria new Integer(3) (construtor hoje deprecated for removal) e sem generics
        quantities.add(Integer.valueOf(3));
        int firstManual = quantities.get(0).intValue();
        System.out.println("Quantidade (manual): " + firstManual);

        System.out.println();
        System.out.println("=== Abordagem nova: autoboxing/unboxing ===");
        quantities.add(5);               // boxing: Integer.valueOf(5)
        quantities.add(2);
        int total = 0;
        for (int q : quantities) {       // unboxing: q.intValue()
            total += q;
        }
        System.out.println("Quantidades " + quantities + " -> total " + total);

        Map<String, Double> prices = new HashMap<String, Double>();
        prices.put("Caneta", 2.5);       // double -> Double
        double withTax = prices.get("Caneta") * 1.1; // Double -> double
        System.out.printf("Preço com imposto: %.2f%n", withTax);

        System.out.println();
        System.out.println("=== Armadilha 1: == entre wrappers (cache de Integer) ===");
        Integer a = 127, b = 127;
        Integer c = 128, d = 128;
        System.out.println("127 == 127 ? " + (a == b) + "  (mesmo objeto do cache -128..127)");
        System.out.println("128 == 128 ? " + (c == d) + " (objetos diferentes!)");
        System.out.println("128 equals 128 ? " + c.equals(d) + "  (forma correta)");

        System.out.println();
        System.out.println("=== Armadilha 2: NullPointerException no unboxing ===");
        Map<String, Integer> stock = new HashMap<String, Integer>();
        stock.put("Caneta", 10);
        try {
            int pencils = stock.get("Lápis"); // get devolve null -> null.intValue()
            System.out.println("Lápis: " + pencils);
        } catch (NullPointerException e) {
            System.out.println("NPE: produto ausente no mapa gerou null e o unboxing falhou");
        }

        System.out.println();
        System.out.println("=== Armadilha 3: remove(int) x remove(Object) ===");
        List<Integer> codes = new ArrayList<Integer>();
        codes.add(10);
        codes.add(20);
        codes.add(1);
        codes.remove(1);                      // remove o ÍNDICE 1 (valor 20)
        System.out.println("Após remove(1): " + codes);
        codes.remove(Integer.valueOf(1));     // remove o VALOR 1
        System.out.println("Após remove(Integer.valueOf(1)): " + codes);

        System.out.println();
        System.out.println("=== Armadilha 4: custo do boxing em laços ===");
        int n = 5000000;
        long t0 = System.nanoTime();
        Long boxedSum = 0L;
        for (int i = 0; i < n; i++) {
            boxedSum += i;                    // unboxing + soma + boxing a cada iteração
        }
        long t1 = System.nanoTime();
        long primitiveSum = 0L;
        for (int i = 0; i < n; i++) {
            primitiveSum += i;
        }
        long t2 = System.nanoTime();
        System.out.println("Long (wrapper): " + boxedSum + " em " + (t1 - t0) / 1000000 + " ms");
        System.out.println("long (primitivo): " + primitiveSum + " em " + (t2 - t1) / 1000000 + " ms");
        System.out.println("(medição ingênua, só ilustrativa; o JIT pode reduzir a diferença)");
    }
}
