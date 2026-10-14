package br.com.javalab.java25.vectorapi;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

/**
 * Java 25 - Vector API, décima incubadora (JEP 508)
 *
 * <p>Status: Incubadora (10ª). Histórico das incubadoras: Java 16 (JEP 338), 17 (JEP 414),
 * 18 (JEP 417), 19 (JEP 426), 20 (JEP 438), 21 (JEP 448), 22 (JEP 460), 23 (JEP 469),
 * 24 (JEP 489) e 25 (JEP 508). Continua incubando até que os value classes do Projeto
 * Valhalla estejam disponíveis. Módulo: {@code jdk.incubator.vector}.</p>
 *
 * <p>O que é: API para expressar cálculos vetoriais (SIMD) que a JIT traduz para instruções
 * de CPU como AVX/AVX-512 (x64) e NEON/SVE (ARM): uma operação processa 4, 8 ou 16 floats de
 * uma vez. Código portátil, com fallback correto em CPUs sem suporte.</p>
 * <p>Foco deste exemplo: similaridade de cosseno entre "embeddings" (vetores de recomendação
 * de produtos) - produto escalar e normas com {@code fma} (multiply-add fundido) e
 * {@code reduceLanes}, comparando com o laço escalar.</p>
 * <p>Antes: torcer para a auto-vetorização da JIT acontecer, ou JNI com intrínsecos em C.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --add-modules jdk.incubator.vector src/br/com/javalab/java25/vectorapi/VectorDotProductExample.java
 * </pre>
 */
public class VectorDotProductExample {

    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        int dims = 1_000_003; // propositalmente não múltiplo do tamanho do vetor (testa a "cauda")
        float[] productA = new float[dims];
        float[] productB = new float[dims];
        for (int i = 0; i < dims; i++) {
            productA[i] = (float) Math.sin(i * 0.001);
            productB[i] = (float) Math.sin(i * 0.001 + 0.3);
        }

        System.out.println("=== Espécie preferida nesta CPU ===");
        System.out.println(SPECIES + " -> " + SPECIES.length() + " floats por operação");

        // aquecimento da JIT
        for (int i = 0; i < 20; i++) {
            cosineScalar(productA, productB);
            cosineVector(productA, productB);
        }

        System.out.println();
        System.out.println("=== Laço escalar ===");
        long t0 = System.nanoTime();
        double scalar = 0;
        for (int i = 0; i < 50; i++) scalar = cosineScalar(productA, productB);
        long scalarUs = (System.nanoTime() - t0) / 50 / 1_000;
        System.out.printf("similaridade = %.6f | %d µs por cálculo%n", scalar, scalarUs);

        System.out.println();
        System.out.println("=== Vector API (fma + reduceLanes) ===");
        t0 = System.nanoTime();
        double vector = 0;
        for (int i = 0; i < 50; i++) vector = cosineVector(productA, productB);
        long vectorUs = (System.nanoTime() - t0) / 50 / 1_000;
        System.out.printf("similaridade = %.6f | %d µs por cálculo%n", vector, vectorUs);

        System.out.println();
        System.out.printf("Diferença numérica: %.2e (ordem de soma diferente em float)%n", Math.abs(scalar - vector));
        System.out.println("Speedup aproximado: " + (vectorUs == 0 ? "n/d" : String.format("%.1fx", (double) scalarUs / vectorUs)));
    }

    static double cosineScalar(float[] a, float[] b) {
        float dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    static double cosineVector(float[] a, float[] b) {
        FloatVector dotAcc = FloatVector.zero(SPECIES);
        FloatVector normAAcc = FloatVector.zero(SPECIES);
        FloatVector normBAcc = FloatVector.zero(SPECIES);
        int i = 0;
        int upper = SPECIES.loopBound(a.length);
        for (; i < upper; i += SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
            dotAcc = va.fma(vb, dotAcc);       // dotAcc += va * vb (em todas as lanes)
            normAAcc = va.fma(va, normAAcc);
            normBAcc = vb.fma(vb, normBAcc);
        }
        float dot = dotAcc.reduceLanes(VectorOperators.ADD);
        float normA = normAAcc.reduceLanes(VectorOperators.ADD);
        float normB = normBAcc.reduceLanes(VectorOperators.ADD);
        for (; i < a.length; i++) {            // cauda escalar
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
