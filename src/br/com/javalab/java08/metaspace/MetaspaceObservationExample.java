package br.com.javalab.java08.metaspace;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 8 - Remoção da PermGen / introdução do Metaspace (JEP 122)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é: os metadados de classes saíram da "Permanent Generation" (área de tamanho fixo
 * dentro do heap, configurada com {@code -XX:MaxPermSize}) e foram para o <b>Metaspace</b>,
 * em memória nativa, que cresce sob demanda.</p>
 * <p>Benefício: fim do clássico {@code OutOfMemoryError: PermGen space} em servidores de
 * aplicação com muitos redeploys/classes geradas; menos ajuste manual.</p>
 * <p>Antes: {@code -XX:PermSize} / {@code -XX:MaxPermSize} (ignorados com aviso a partir do Java 8
 * e removidos depois).</p>
 * <p>Como não é algo "programável", este exemplo OBSERVA a JVM: lista os memory pools
 * (incluindo "Metaspace" e "Compressed Class Space") e mostra o Metaspace crescendo ao
 * gerar classes de proxy dinamicamente.</p>
 *
 * <p>Experimente:</p>
 * <pre>
 * java -XX:MaxMetaspaceSize=64m src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java
 * java -Xlog:gc+metaspace src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java
 * </pre>
 */
public class MetaspaceObservationExample {

    public static void main(String[] args) {
        System.out.println("=== Memory pools da JVM ===");
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage usage = pool.getUsage();
            System.out.printf("%-35s %-8s usado=%,d KB, max=%s%n", pool.getName(), pool.getType(),
                    usage.getUsed() / 1024, usage.getMax() < 0 ? "ilimitado" : (usage.getMax() / 1024) + " KB");
        }
        boolean hasPermGen = false;
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            hasPermGen |= pool.getName().contains("Perm Gen");
        }
        System.out.println("Existe pool 'Perm Gen'? " + hasPermGen + " (no Java 7 existiria)");

        System.out.println();
        System.out.println("=== Metaspace crescendo ao carregar classes ===");
        MemoryPoolMXBean metaspace = findPool("Metaspace");
        if (metaspace == null) {
            System.out.println("Pool Metaspace não encontrado nesta JVM.");
            return;
        }
        long before = metaspace.getUsage().getUsed();
        int loadedBefore = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();

        // Cada class loader novo gera uma nova classe de proxy -> mais metadados no Metaspace
        List<Object> keepAlive = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            ClassLoader loader = new ClassLoader(MetaspaceObservationExample.class.getClassLoader()) {
            };
            Object proxy = Proxy.newProxyInstance(loader, new Class<?>[] {Runnable.class},
                    (p, method, methodArgs) -> null);
            keepAlive.add(proxy);
        }

        long after = metaspace.getUsage().getUsed();
        int loadedAfter = ManagementFactory.getClassLoadingMXBean().getLoadedClassCount();
        System.out.printf("Classes carregadas: %d -> %d%n", loadedBefore, loadedAfter);
        System.out.printf("Metaspace usado:    %,d KB -> %,d KB%n", before / 1024, after / 1024);
        System.out.println("Proxies mantidos vivos: " + keepAlive.size());
    }

    private static MemoryPoolMXBean findPool(String name) {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getName().equals(name)) {
                return pool;
            }
        }
        return null;
    }
}
