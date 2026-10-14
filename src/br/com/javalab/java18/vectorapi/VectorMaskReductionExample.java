package br.com.javalab.java18.vectorapi;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

/**
 * Java 18 - Vector API, terceira Incubadora (JEP 417)
 *
 * <p>Status: Incubadora (terceira). Histórico: 1a incubadora no Java 16 (JEP 338), 2a no 17 (JEP 414);
 * segue em incubação até hoje (JDK 25: décima incubadora, JEP 508), aguardando o Projeto Valhalla.</p>
 *
 * <p>O que é: API para expressar computações SIMD (uma instrução, vários dados) que o JIT
 * compila para instruções vetoriais da CPU (SSE/AVX/NEON/SVE).</p>
 * <p>Foco deste exemplo (diferente do exemplo do Java 16, que somava arrays elemento a elemento):
 * <b>máscaras</b> ({@link VectorMask}) e <b>reduções</b> ({@code reduceLanes}). O Java 18 melhorou
 * o suporte a máscaras em ARM SVE e as operações lane-wise com máscara.</p>
 * <ul>
 *   <li>{@code compare(GT, x)} gera uma máscara com as lanes que satisfazem a condição;</li>
 *   <li>{@code reduceLanes(ADD, mask)} soma só as lanes ativas;</li>
 *   <li>{@code species.indexInRange(i, n)} cria a máscara do "resto" do array, eliminando o laço escalar final.</li>
 * </ul>
 * <p>Antes: laço escalar com {@code if}, confiando na auto-vetorização do JIT (que frequentemente
 * desiste quando há condicionais).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --add-modules jdk.incubator.vector src/br/com/javalab/java18/vectorapi/VectorMaskReductionExample.java
 * </pre>
 */
public class VectorMaskReductionExample {

    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        float[] orderTotals = new float[1_003]; // tamanho não múltiplo do número de lanes, de propósito
        for (int i = 0; i < orderTotals.length; i++) {
            orderTotals[i] = (i * 37) % 500 + 0.5f;
        }
        float threshold = 300f;

        System.out.println("=== Espécie preferida nesta CPU ===");
        System.out.println(SPECIES + " -> " + SPECIES.length() + " lanes de float");

        System.out.println();
        System.out.println("=== Abordagem antiga: laço escalar com if ===");
        Summary scalar = scalarSum(orderTotals, threshold);
        System.out.println(scalar);

        System.out.println();
        System.out.println("=== Vector API: máscara de comparação + máscara de faixa + reduceLanes ===");
        Summary vector = vectorSum(orderTotals, threshold);
        System.out.println(vector);
        System.out.println("Mesmo resultado? " + (scalar.count() == vector.count()
                && Math.abs(scalar.sum() - vector.sum()) < 0.5));

        System.out.println();
        System.out.println("=== Máscara de um único vetor, lane a lane ===");
        FloatVector sample = FloatVector.fromArray(SPECIES, orderTotals, 8); // trecho com valores mistos
        VectorMask<Float> bigOrders = sample.compare(VectorOperators.GT, threshold);
        System.out.println("valores : " + sample);
        System.out.println("máscara : " + bigOrders);
        System.out.println("trueCount = " + bigOrders.trueCount() + ", firstTrue = " + bigOrders.firstTrue());
        System.out.println("max (lanes ativas) = " + sample.reduceLanes(VectorOperators.MAX, bigOrders));
    }

    record Summary(int count, double sum) {
        @Override
        public String toString() {
            return "Pedidos acima do limite: %d, soma: %.2f".formatted(count, sum);
        }
    }

    static Summary scalarSum(float[] values, float threshold) {
        int count = 0;
        double sum = 0;
        for (float v : values) {
            if (v > threshold) {
                count++;
                sum += v;
            }
        }
        return new Summary(count, sum);
    }

    static Summary vectorSum(float[] values, float threshold) {
        int count = 0;
        double sum = 0;
        for (int i = 0; i < values.length; i += SPECIES.length()) {
            VectorMask<Float> inRange = SPECIES.indexInRange(i, values.length); // trata o "resto" sem laço extra
            FloatVector v = FloatVector.fromArray(SPECIES, values, i, inRange);
            VectorMask<Float> selected = v.compare(VectorOperators.GT, threshold).and(inRange);
            count += selected.trueCount();
            sum += v.reduceLanes(VectorOperators.ADD, selected);
        }
        return new Summary(count, sum);
    }
}
