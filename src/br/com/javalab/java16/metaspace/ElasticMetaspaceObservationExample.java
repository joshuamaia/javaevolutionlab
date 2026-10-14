package br.com.javalab.java16.metaspace;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Java 16 - Elastic Metaspace (JEP 387)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que e: reimplementacao do alocador do Metaspace (memoria nativa onde ficam os
 * metadados de classes) usando um esquema "buddy allocation" e devolvendo memoria ao
 * sistema operacional mais cedo quando ClassLoaders sao descarregados. Ajuste:
 * {@code -XX:MetaspaceReclaimPolicy=balanced|aggressive|none} (a partir do Java 16; removida
 * depois, comportamento padrao mantido).</p>
 * <p>Beneficio: menor consumo de memoria em aplicacoes que criam e descartam muitas
 * classes (servidores de aplicacao, frameworks com proxies, scripts).</p>
 * <p>Antes: o Metaspace fragmentava e retinha memoria por muito tempo.</p>
 *
 * <p>Este exemplo observa o pool "Metaspace" via MXBean enquanto gera classes de proxy
 * em ClassLoaders descartaveis e depois os libera.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java16/metaspace/ElasticMetaspaceObservationExample.java
 * java -Xlog:metaspace -XX:+UnlockDiagnosticVMOptions -XX:+PrintMetaspaceStatisticsAtExit -version
 * jcmd &lt;pid&gt; VM.metaspace
 * </pre>
 */
public class ElasticMetaspaceObservationExample {

    public interface OrderService {
        String process(int id);
    }

    public static void main(String[] args) throws Exception {
        MemoryPoolMXBean metaspace = ManagementFactory.getMemoryPoolMXBeans().stream()
                .filter(p -> p.getName().equals("Metaspace"))
                .findFirst().orElseThrow();

        print("Inicio", metaspace);

        Object[] proxies = new Object[300];
        for (int i = 0; i < proxies.length; i++) {
            // cada ClassLoader novo gera uma nova classe de proxy
            ClassLoader loader = new URLClassLoader(new URL[0], OrderService.class.getClassLoader());
            proxies[i] = Proxy.newProxyInstance(loader, new Class<?>[]{OrderService.class},
                    (proxy, method, methodArgs) -> "ok");
        }
        print("Apos criar 300 classes de proxy", metaspace);

        proxies = null; // libera proxies -> classes -> ClassLoaders
        for (int i = 0; i < 3; i++) {
            System.gc();
            Thread.sleep(200);
        }
        print("Apos descarregar os ClassLoaders", metaspace);
        System.out.println("(committed cai quando o Metaspace devolve memoria ao SO; o valor exato depende da JVM)");
    }

    private static void print(String phase, MemoryPoolMXBean pool) {
        MemoryUsage usage = pool.getUsage();
        System.out.printf("%-34s usado=%6d KB  committed=%6d KB%n",
                phase, usage.getUsed() / 1024, usage.getCommitted() / 1024);
    }
}
