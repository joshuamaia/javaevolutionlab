package br.com.javalab.java17.prng;

import java.util.Comparator;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Java 17 - Enhanced Pseudo-Random Number Generators (JEP 356)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a interface comum {@link RandomGenerator} (implementada também por {@code Random},
 * {@code SplittableRandom} e {@code ThreadLocalRandom}), a fábrica {@link RandomGeneratorFactory}
 * para escolher algoritmos pelo nome, e novos algoritmos (família LXM como {@code L64X128MixRandom},
 * Xoshiro/Xoroshiro). Subinterfaces descrevem capacidades: {@code JumpableGenerator},
 * {@code LeapableGenerator}, {@code SplittableGenerator}, {@code StreamableGenerator}.</p>
 * <p>Benefício: trocar de algoritmo sem reescrever o código, escolher pelo perfil
 * (período, qualidade estatística, suporte a paralelismo) e gerar fluxos independentes para threads.</p>
 * <p>Antes: classes concretas sem interface comum ({@code Random}, {@code SplittableRandom},
 * {@code ThreadLocalRandom}), cada uma com sua própria API.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/prng/EnhancedPseudoRandomGeneratorsExample.java
 * </pre>
 */
public class EnhancedPseudoRandomGeneratorsExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: classe concreta ===");
        Random legacy = new Random(42);
        System.out.println("Random(42).nextInt(100) = " + legacy.nextInt(100));
        System.out.println("Random também é um RandomGenerator? " + (legacy instanceof RandomGenerator));

        System.out.println();
        System.out.println("=== Escolhendo o algoritmo pelo nome ===");
        RandomGenerator lxm = RandomGenerator.of("L64X128MixRandom");
        System.out.println("Algoritmo L64X128MixRandom -> sorteio de cupom: " + lxm.nextInt(1000, 10000));
        RandomGenerator byDefault = RandomGenerator.getDefault();
        System.out.println("RandomGenerator.getDefault(): " + byDefault.getClass().getSimpleName());

        System.out.println();
        System.out.println("=== Algoritmos disponíveis (RandomGeneratorFactory.all()) ===");
        System.out.printf("%-22s %-12s %-9s %-9s %-10s%n", "nome", "grupo", "jumpable", "splittable", "bits estado");
        RandomGeneratorFactory.all()
                .sorted(Comparator.comparing(RandomGeneratorFactory::name))
                .forEach(factory -> System.out.printf("%-22s %-12s %-9s %-10s %-10d%n",
                        factory.name(), factory.group(), factory.isJumpable(),
                        factory.isSplittable(), factory.stateBits()));

        System.out.println();
        System.out.println("=== Mesma semente = mesma sequência (reprodutibilidade) ===");
        RandomGeneratorFactory<RandomGenerator> factory = RandomGeneratorFactory.of("L64X128MixRandom");
        RandomGenerator first = factory.create(2021L);
        RandomGenerator second = factory.create(2021L);
        System.out.println("Gerador A: " + first.ints(5, 0, 100).boxed().toList());
        System.out.println("Gerador B: " + second.ints(5, 0, 100).boxed().toList());

        System.out.println();
        System.out.println("=== JumpableGenerator: fluxos que não se sobrepõem (um por worker) ===");
        RandomGenerator.JumpableGenerator jumpable = RandomGenerator.JumpableGenerator.of("Xoshiro256PlusPlus");
        jumpable.jumps(3).forEach(worker ->
                System.out.println("Worker com cópia saltada -> " + worker.nextInt(100)));

        System.out.println();
        System.out.println("=== SplittableGenerator: dividir para tarefas paralelas ===");
        RandomGenerator.SplittableGenerator splittable = RandomGenerator.SplittableGenerator.of("L64X128MixRandom");
        double average = splittable.splits(4)
                .parallel()
                .mapToDouble(child -> child.doubles(1_000).average().orElse(0))
                .average().orElse(0);
        System.out.printf("Média de 4 x 1000 doubles em paralelo: %.3f (esperado ~0.5)%n", average);
    }
}
