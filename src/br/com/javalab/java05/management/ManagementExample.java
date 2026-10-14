package br.com.javalab.java05.management;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 5 - Monitoring and Management API (JSR 174, java.lang.management)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: MXBeans padronizados, obtidos via {@code ManagementFactory}, que expõem
 * informações da JVM em execução: memória (heap/non-heap e pools), GC, threads,
 * carregamento de classes, runtime e sistema operacional. Os mesmos MXBeans são
 * publicados via JMX (JSR 3/160, incorporado ao Java 5) e lidos pelo {@code jconsole},
 * que estreou no JDK 5.</p>
 * <p>Benefício: observabilidade padronizada sem APIs proprietárias do fabricante da JVM.</p>
 * <p>Antes: apenas {@code Runtime.totalMemory()/freeMemory()} e ferramentas específicas.</p>
 * <p>Ver também: java06/monitoring (detecção de deadlock e load average do Java 6).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/management/ManagementExample.java
 * </pre>
 */
public class ManagementExample {

    static long mb(long bytes) {
        return bytes / (1024 * 1024);
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: Runtime ===");
        Runtime rt = Runtime.getRuntime();
        System.out.println("Heap total=" + mb(rt.totalMemory()) + " MB, livre=" + mb(rt.freeMemory())
                + " MB, máx=" + mb(rt.maxMemory()) + " MB, CPUs=" + rt.availableProcessors());

        System.out.println();
        System.out.println("=== RuntimeMXBean ===");
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM: " + runtime.getVmName() + " " + runtime.getVmVersion());
        System.out.println("Nome (pid@host): " + runtime.getName());
        System.out.println("Uptime: " + runtime.getUptime() + " ms; argumentos JVM: " + runtime.getInputArguments());

        System.out.println();
        System.out.println("=== OperatingSystemMXBean ===");
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        System.out.println(os.getName() + " " + os.getVersion() + " (" + os.getArch() + "), CPUs="
                + os.getAvailableProcessors());

        System.out.println();
        System.out.println("=== MemoryMXBean antes/depois de alocar ===");
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        MemoryUsage before = memory.getHeapMemoryUsage();
        List<byte[]> cache = new ArrayList<byte[]>();
        for (int i = 0; i < 20; i++) {
            cache.add(new byte[1024 * 1024]); // ~20 MB
        }
        MemoryUsage after = memory.getHeapMemoryUsage();
        System.out.println("Heap usado: " + mb(before.getUsed()) + " MB -> " + mb(after.getUsed())
                + " MB (" + cache.size() + " blocos alocados)");
        System.out.println("Non-heap usado: " + mb(memory.getNonHeapMemoryUsage().getUsed()) + " MB");
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            System.out.println("  pool " + pool.getName() + " (" + pool.getType() + "): "
                    + mb(pool.getUsage().getUsed()) + " MB");
        }

        System.out.println();
        System.out.println("=== GarbageCollectorMXBean ===");
        cache.clear();
        System.gc(); // apenas uma sugestão à JVM
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println(gc.getName() + ": coletas=" + gc.getCollectionCount()
                    + ", tempo=" + gc.getCollectionTime() + " ms");
        }

        System.out.println();
        System.out.println("=== ThreadMXBean e ClassLoadingMXBean ===");
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        System.out.println("Threads vivas=" + threads.getThreadCount() + ", pico=" + threads.getPeakThreadCount()
                + ", daemon=" + threads.getDaemonThreadCount());
        if (threads.isCurrentThreadCpuTimeSupported()) {
            System.out.println("CPU da thread main: " + threads.getCurrentThreadCpuTime() / 1000000 + " ms");
        }
        ClassLoadingMXBean classes = ManagementFactory.getClassLoadingMXBean();
        System.out.println("Classes carregadas=" + classes.getLoadedClassCount()
                + ", total desde o início=" + classes.getTotalLoadedClassCount());
        System.out.println();
        System.out.println("Dica: rode com uma pausa e conecte o jconsole (JDK) para ver esses MXBeans ao vivo.");
    }
}
