package br.com.javalab.java15.records;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.RecordComponent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 15 - Records, segunda Preview (JEP 384)
 *
 * <p>Status: Segunda Preview. Preview no Java 14 (JEP 359); Definitivo no Java 16
 * (JEP 395). A sintaxe abaixo permaneceu igual e compila no JDK 25 sem flags.</p>
 *
 * <p>O que mudou/foi destacado no 15 (o basico esta em java14/records):</p>
 * <ul>
 *   <li>Records LOCAIS (declarados dentro de um metodo) para resultados intermediarios
 *       de streams; e tambem enums e interfaces locais. Sao implicitamente static (nao
 *       capturam variaveis do metodo).</li>
 *   <li>Records implementando interfaces, inclusive interfaces {@code sealed} (JEP 360,
 *       tambem Preview no 15) - records sao implicitamente final.</li>
 *   <li>Anotacoes em componentes propagadas para campo/accessor/parametro conforme o
 *       {@code @Target}, e o novo alvo {@code ElementType.RECORD_COMPONENT}, visivel via
 *       {@code Class.getRecordComponents()}.</li>
 *   <li>No construtor compacto e proibido atribuir aos campos ({@code this.x = ...});
 *       so os parametros podem ser reatribuidos.</li>
 * </ul>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/records/RecordSecondPreviewExample.java
 * </pre>
 */
public class RecordSecondPreviewExample {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.RECORD_COMPONENT, ElementType.FIELD})
    @interface Sensitive {}

    /** Interface selada implementada apenas por records. */
    sealed interface Shipment permits LocalDelivery, Carrier {
        double cost();
    }

    record LocalDelivery(String neighborhood, double cost) implements Shipment {}

    record Carrier(String company, @Sensitive String trackingCode, double cost) implements Shipment {}

    record Sale(String seller, String product, double value) {}

    public static void main(String[] args) throws NoSuchFieldException {
        List<Sale> sales = List.of(
                new Sale("Ana", "Notebook", 4500), new Sale("Bruno", "Mouse", 90),
                new Sale("Ana", "Monitor", 1200), new Sale("Bruno", "Teclado", 250));

        System.out.println("=== Record local como resultado intermediario ===");
        record SellerTotal(String seller, double total) {}   // so existe dentro do main
        List<SellerTotal> ranking = sales.stream()
                .collect(Collectors.groupingBy(Sale::seller, Collectors.summingDouble(Sale::value)))
                .entrySet().stream()
                .map(e -> new SellerTotal(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingDouble(SellerTotal::total).reversed())
                .collect(Collectors.toList());
        ranking.forEach(System.out::println);

        System.out.println("=== Enum e interface locais (tambem permitidos no 15) ===");
        enum Level { BRONZE, GOLD }
        interface Classifier { Level classify(double total); }
        Classifier classifier = total -> total > 2000 ? Level.GOLD : Level.BRONZE;
        ranking.forEach(r -> System.out.println(r.seller() + " -> " + classifier.classify(r.total())));

        System.out.println("=== Records implementando interface selada ===");
        List<Shipment> shipments = List.of(
                new LocalDelivery("Boa Viagem", 12.0),
                new Carrier("Transportadora X", "BR123456789", 35.5));
        for (Shipment s : shipments) {
            System.out.println(s + " -> custo " + s.cost());
        }

        System.out.println("=== Anotacoes em componentes (reflexao) ===");
        for (RecordComponent component : Carrier.class.getRecordComponents()) {
            System.out.printf("componente %-13s tipo=%-6s @Sensitive no componente? %s%n",
                    component.getName(), component.getType().getSimpleName(),
                    component.isAnnotationPresent(Sensitive.class));
        }
        System.out.println("Propagada ao campo privado? "
                + Carrier.class.getDeclaredField("trackingCode").isAnnotationPresent(Sensitive.class));
    }
}
