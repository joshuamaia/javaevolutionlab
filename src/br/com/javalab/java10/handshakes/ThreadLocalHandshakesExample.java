package br.com.javalab.java10.handshakes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Java 10 - Thread-Local Handshakes (JEP 312)
 *
 * <p>Status: Produto (JVM).</p>
 *
 * <p>O que é: mecanismo interno da HotSpot para executar uma operação em <b>uma única
 * thread</b> (um "handshake") sem parar todas as threads da JVM num <i>safepoint global</i>.</p>
 * <p>Benefício: operações por thread — capturar a pilha de uma thread, revogar biased
 * locking (removido no JDK 18), suspender/retomar para depuração — deixam de causar pausas
 * "stop-the-world" em toda a aplicação. Base para coletores concorrentes como o ZGC.</p>
 * <p>Antes: qualquer operação de VM desse tipo exigia um safepoint global.</p>
 *
 * <p>Não há API Java: este é um exemplo de observação. Ele mantém threads "trabalhadoras"
 * ocupadas e pede repetidamente a pilha de UMA delas ({@code Thread.getStackTrace()}),
 * operação que hoje usa handshake; as outras threads continuam progredindo.</p>
 *
 * <p>Flags para experimentar:</p>
 * <pre>
 * java -Xlog:handshake=debug  src/br/com/javalab/java10/handshakes/ThreadLocalHandshakesExample.java
 * java -Xlog:safepoint        src/br/com/javalab/java10/handshakes/ThreadLocalHandshakesExample.java
 * (no JDK 10 existia -XX:-ThreadLocalHandshakes para voltar ao safepoint global em x64/SPARC;
 *  a opção foi removida em versões posteriores, quando o handshake virou obrigatório)
 * </pre>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/handshakes/ThreadLocalHandshakesExample.java
 * </pre>
 */
public class ThreadLocalHandshakesExample {

    public static void main(String[] args) throws InterruptedException {
        AtomicBoolean running = new AtomicBoolean(true);
        List<Thread> workers = new ArrayList<>();
        List<AtomicLong> progress = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AtomicLong counter = new AtomicLong();
            progress.add(counter);
            Thread worker = new Thread(() -> {
                while (running.get()) {
                    counter.incrementAndGet(); // processamento de pedidos simulado
                }
            }, "worker-" + i);
            workers.add(worker);
            worker.start();
        }
        Thread.sleep(100);

        System.out.println("=== Capturando a pilha de UMA thread repetidamente ===");
        Thread target = workers.get(0);
        long[] before = snapshot(progress);
        long start = System.nanoTime();
        int samples = 2_000;
        int frames = 0;
        for (int i = 0; i < samples; i++) {
            frames += target.getStackTrace().length;
        }
        long elapsedMicros = (System.nanoTime() - start) / 1_000;
        long[] after = snapshot(progress);

        System.out.printf("%d capturas da pilha de %s em %d ms (%.1f us cada, %d frames no total)%n",
                samples, target.getName(), elapsedMicros / 1_000, (double) elapsedMicros / samples, frames);
        System.out.println();
        System.out.println("=== As demais threads continuaram trabalhando durante as capturas ===");
        for (int i = 0; i < workers.size(); i++) {
            System.out.printf("  %s avançou %,d iterações%n", workers.get(i).getName(), after[i] - before[i]);
        }

        System.out.println("A thread alvo (" + target.getName() + ") avança menos: só ela é pausada em cada handshake;");
        System.out.println("com um safepoint global, TODAS seriam paradas a cada captura.");

        running.set(false);
        for (Thread worker : workers) {
            worker.join();
        }
        System.out.println("Workers encerrados.");
    }

    private static long[] snapshot(List<AtomicLong> progress) {
        long[] values = new long[progress.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = progress.get(i).get();
        }
        return values;
    }
}
