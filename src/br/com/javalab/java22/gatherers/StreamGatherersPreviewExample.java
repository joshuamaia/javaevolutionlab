package br.com.javalab.java22.gatherers;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * Java 22 - Stream Gatherers (JEP 461)
 *
 * <p>Status: Preview. Evolução: segunda Preview no Java 23 (JEP 473) e definitivo no
 * Java 24 (JEP 485). A API usada aqui não mudou, por isso este código compila e roda no
 * JDK 25 sem flags (no JDK 22 exigia {@code --enable-preview}).</p>
 *
 * <p>O que é: a nova operação intermediária {@code Stream.gather(Gatherer)} - o equivalente
 * intermediário do {@code collect(Collector)}. Um gatherer pode ter estado, transformar
 * 1 elemento em 0..N, agrupar elementos e interromper o stream. A classe {@link Gatherers}
 * traz implementações prontas: {@code windowFixed}, {@code windowSliding}, {@code fold},
 * {@code scan} e {@code mapConcurrent}.</p>
 * <p>Benefício: operações que antes exigiam laços manuais, coletar tudo numa lista ou
 * bibliotecas externas agora ficam no pipeline do stream.</p>
 * <p>Antes: só havia o conjunto fixo de operações intermediárias (map, filter, flatMap,
 * distinct, sorted, limit...). Agrupar "de 3 em 3" exigia índices e sublistas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java22/gatherers/StreamGatherersPreviewExample.java
 * </pre>
 * <p>Este exemplo mostra o básico (janelas e fold); o Java 23 mostra gatherers
 * customizados, {@code scan} e {@code mapConcurrent}.</p>
 */
public class StreamGatherersPreviewExample {

    record Sale(String day, double amount) {}

    public static void main(String[] args) {
        List<String> orderIds = List.of("P1", "P2", "P3", "P4", "P5", "P6", "P7");

        System.out.println("=== windowFixed: pedidos em lotes de 3 ===");
        orderIds.stream()
                .gather(Gatherers.windowFixed(3))
                .forEach(batch -> System.out.println("Lote: " + batch));

        List<Sale> sales = List.of(
                new Sale("seg", 100), new Sale("ter", 140), new Sale("qua", 90),
                new Sale("qui", 200), new Sale("sex", 160));

        System.out.println();
        System.out.println("=== windowSliding: média móvel de vendas (janela de 3 dias) ===");
        sales.stream()
                .gather(Gatherers.windowSliding(3))
                .forEach(window -> {
                    double avg = window.stream().mapToDouble(Sale::amount).average().orElse(0);
                    String days = window.getFirst().day() + ".." + window.getLast().day();
                    System.out.printf("%s -> média %.2f%n", days, avg);
                });

        System.out.println();
        System.out.println("=== fold: reduz tudo a um único resultado (aqui, um relatório em texto) ===");
        String report = sales.stream()
                .gather(Gatherers.fold(() -> "Vendas:", (text, sale) -> text + " " + sale.day() + "=" + sale.amount()))
                .findFirst()
                .orElseThrow();
        System.out.println(report);
    }
}
