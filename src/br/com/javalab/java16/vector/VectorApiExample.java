package br.com.javalab.java16.vector;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

/**
 * Java 16 - Vector API (JEP 338)
 *
 * <p>Status: Incubadora (modulo jdk.incubator.vector). Continua em incubadora ate
 * hoje (10a incubacao no Java 25, JEP 508), aguardando o Projeto Valhalla. As classes
 * usadas aqui (IntVector, FloatVector, SPECIES_PREFERRED, fromArray, add, mul,
 * intoArray, loopBound) existem desde o Java 16 e ainda existem no JDK 25.</p>
 *
 * <p>O que e: API para expressar calculos vetoriais (SIMD) que a JIT compila para
 * instrucoes da CPU (SSE/AVX no x64, NEON/SVE no ARM), processando varios elementos
 * por instrucao.</p>
 * <p>Beneficio: desempenho previsivel em calculos numericos (precos, imagens, ML)
 * sem depender da auto-vetorizacao da JIT, que pode ou nao acontecer.</p>
 * <p>Antes: laco escalar (um elemento por vez) e torcer pela auto-vetorizacao.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java --add-modules jdk.incubator.vector src/br/com/javalab/java16/vector/VectorApiExample.java
 * </pre>
 */
public class VectorApiExample {

    static final VectorSpecies<Float> FLOAT_SPECIES = FloatVector.SPECIES_PREFERRED;
    static final VectorSpecies<Integer> INT_SPECIES = IntVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        System.out.println("=== Especies preferidas nesta CPU ===");
        System.out.println("FloatVector: " + FLOAT_SPECIES.length() + " floats por vetor (" + FLOAT_SPECIES.vectorBitSize() + " bits)");
        System.out.println("IntVector:   " + INT_SPECIES.length() + " ints por vetor");

        int size = 1_000_003; // tamanho nao multiplo do vetor, para exercitar a "cauda"
        float[] prices = new float[size];
        float[] taxRates = new float[size];
        for (int i = 0; i < size; i++) {
            prices[i] = 10 + (i % 100);
            taxRates[i] = 1.10f;
        }
        float[] scalarResult = new float[size];
        float[] vectorResult = new float[size];

        // aquecimento da JIT
        for (int i = 0; i < 20; i++) {
            scalarFinalPrice(prices, taxRates, scalarResult);
            vectorFinalPrice(prices, taxRates, vectorResult);
        }

        System.out.println("=== Abordagem antiga: laco escalar ===");
        long t0 = System.nanoTime();
        scalarFinalPrice(prices, taxRates, scalarResult);
        long scalarMicros = (System.nanoTime() - t0) / 1_000;
        System.out.println("Tempo: " + scalarMicros + " us");

        System.out.println("=== Java 16: Vector API ===");
        long t1 = System.nanoTime();
        vectorFinalPrice(prices, taxRates, vectorResult);
        long vectorMicros = (System.nanoTime() - t1) / 1_000;
        System.out.println("Tempo: " + vectorMicros + " us");
        System.out.println("Resultados iguais? " + java.util.Arrays.equals(scalarResult, vectorResult));
        System.out.println("Ex.: preco " + prices[5] + " -> final " + vectorResult[5]);

        System.out.println("=== IntVector: soma de quantidades ===");
        int[] quantities = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
        int[] doubled = new int[quantities.length];
        int i = 0;
        for (; i < INT_SPECIES.loopBound(quantities.length); i += INT_SPECIES.length()) {
            IntVector v = IntVector.fromArray(INT_SPECIES, quantities, i);
            v.add(v).intoArray(doubled, i);
        }
        for (; i < quantities.length; i++) {          // cauda escalar
            doubled[i] = quantities[i] + quantities[i];
        }
        System.out.println("Dobro: " + java.util.Arrays.toString(doubled));
        System.out.println("(mini-benchmark ingenuo: a JIT pode auto-vetorizar lacos simples como o escalar;");
        System.out.println(" o ganho da Vector API aparece em calculos que a JIT nao vetoriza sozinha. Use JMH.)");
    }

    static void scalarFinalPrice(float[] prices, float[] rates, float[] out) {
        for (int i = 0; i < prices.length; i++) {
            out[i] = prices[i] * rates[i] + 2.0f;
        }
    }

    static void vectorFinalPrice(float[] prices, float[] rates, float[] out) {
        int i = 0;
        int upperBound = FLOAT_SPECIES.loopBound(prices.length);
        for (; i < upperBound; i += FLOAT_SPECIES.length()) {
            FloatVector p = FloatVector.fromArray(FLOAT_SPECIES, prices, i);
            FloatVector r = FloatVector.fromArray(FLOAT_SPECIES, rates, i);
            p.mul(r).add(2.0f).intoArray(out, i);
        }
        for (; i < prices.length; i++) {               // elementos restantes
            out[i] = prices[i] * rates[i] + 2.0f;
        }
    }
}
