package br.com.javalab.java24.aot;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Java 24 - Ahead-of-Time Class Loading &amp; Linking (JEP 483)
 *
 * <p>Status: Produto (JVM). Primeiro passo do Projeto Leyden. Evolução: no Java 25 o fluxo
 * ficou em um passo só ({@code -XX:AOTCacheOutput}, JEP 514) e o cache passou a guardar
 * perfis de métodos (JEP 515). Base: CDS (Class Data Sharing), AppCDS (Java 10, JEP 310) e
 * CDS dinâmico (Java 13, JEP 350).</p>
 *
 * <p>O que é: num "treino" a JVM observa a aplicação e grava quais classes foram usadas;
 * depois cria um <b>cache AOT</b> com essas classes já lidas, verificadas, CARREGADAS e
 * LINKADAS (além do que o CDS fazia: só lidas/parseadas). Nas execuções seguintes, as classes
 * aparecem instantaneamente prontas, reduzindo o tempo de startup (tipicamente 20-40% em apps
 * com muitas classes, como Spring).</p>
 * <p>Benefício: startup mais rápido sem mudar código e sem as restrições de uma imagem nativa.</p>
 *
 * <p>Exemplo de observação: o programa usa streams, lambdas, regex e java.time (muitas classes
 * do JDK) e imprime quantas classes foram carregadas e o tempo desde o início da JVM.
 * Compare o tempo rodando com e sem o cache. As classes da APLICAÇÃO só entram no cache se
 * vierem de um JAR no classpath (de um diretório aparecem como "Unsupported location" e são
 * ignoradas; as classes do JDK são cacheadas mesmo assim). Não funciona no modo código-fonte.
 * Comandos (com as classes compiladas em {@code out}; o mesmo {@code -cp} nas três etapas):</p>
 * <pre>
 * jar --create --file app.jar -C out br/com/javalab/java24/aot
 * # 1) treino: grava a configuração
 * java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf -cp app.jar br.com.javalab.java24.aot.AheadOfTimeClassLoadingExample
 * # 2) cria o cache a partir da configuração (não executa a aplicação)
 * java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot -cp app.jar
 * # 3) produção: usa o cache
 * java -XX:AOTCache=app.aot -cp app.jar br.com.javalab.java24.aot.AheadOfTimeClassLoadingExample
 * # (opcional) ver de onde as classes vieram ("source: shared objects file")
 * java -XX:AOTCache=app.aot -Xlog:class+load -cp app.jar br.com.javalab.java24.aot.AheadOfTimeClassLoadingExample
 * </pre>
 * <p>Medido neste projeto (JDK 24, Windows): ~49 ms sem cache e ~30 ms com cache até o fim do main.</p>
 */
public class AheadOfTimeClassLoadingExample {

    record Order(String customer, String category, double total, LocalDate date) {}

    public static void main(String[] args) {
        System.out.println("=== Trabalho típico de inicialização (carrega muitas classes) ===");
        List<Order> orders = List.of(
                new Order("ana", "livros", 89.9, LocalDate.of(2025, 3, 1)),
                new Order("bruno", "eletrônicos", 1299.0, LocalDate.of(2025, 3, 2)),
                new Order("ana", "eletrônicos", 349.5, LocalDate.of(2025, 3, 3)));
        Map<String, Double> byCategory = orders.stream()
                .collect(Collectors.groupingBy(Order::category, Collectors.summingDouble(Order::total)));
        System.out.println("Total por categoria: " + byCategory);
        Pattern email = Pattern.compile("^[\\w.]+@[\\w.]+\\.[a-z]{2,}$");
        System.out.println("E-mail válido? " + email.matcher("ana@loja.com.br").matches());
        System.out.println("Hoje formatado: " + LocalDate.of(2025, 3, 18).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        System.out.println();
        System.out.println("=== Observação ===");
        ClassLoadingMXBean classes = ManagementFactory.getClassLoadingMXBean();
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        System.out.println("Classes carregadas: " + classes.getLoadedClassCount());
        System.out.println("Tempo desde o início da JVM até aqui: " + uptime + " ms");
        List<String> aotFlags = ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(a -> a.startsWith("-XX:AOT")).toList();
        System.out.println("Flags AOT nesta execução: " + (aotFlags.isEmpty() ? "nenhuma (rode com -XX:AOTCache=app.aot)" : aotFlags));
    }
}
