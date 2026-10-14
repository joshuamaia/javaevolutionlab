package br.com.javalab.java05.concurrentcollections;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java 5 - Coleções concorrentes (JSR 166, pacote java.util.concurrent)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: coleções projetadas para acesso por várias threads:
 * {@code ConcurrentHashMap} (travamento fino por segmento no Java 5; CAS desde o Java 8),
 * {@code CopyOnWriteArrayList} (cópia a cada escrita, ótima para muitas leituras e
 * poucas escritas, ex.: listeners) e {@code BlockingQueue} (filas que bloqueiam o
 * produtor quando cheias e o consumidor quando vazias).</p>
 * <p>Benefício: escalabilidade e iteradores que não lançam
 * {@code ConcurrentModificationException}; operações atômicas compostas
 * ({@code putIfAbsent}, {@code replace}).</p>
 * <p>Antes: {@code Hashtable}/{@code Collections.synchronizedMap} (um único lock
 * global, iteração ainda precisa de sincronização manual) e filas com
 * {@code wait/notify} escritas à mão.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/concurrentcollections/ConcurrentCollectionsExample.java
 * </pre>
 */
public class ConcurrentCollectionsExample {

    private static final String POISON_PILL = "FIM";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Abordagem antiga: HashMap modificado durante a iteração ===");
        Map<String, Integer> plain = new HashMap<String, Integer>();
        plain.put("home", 1);
        plain.put("produtos", 2);
        plain.put("carrinho", 3);
        try {
            for (String page : plain.keySet()) {
                plain.put(page + "-v2", 0); // modificação estrutural
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("HashMap: ConcurrentModificationException");
        }

        System.out.println();
        System.out.println("=== ConcurrentHashMap: contador de acessos com 4 threads ===");
        final ConcurrentMap<String, AtomicInteger> hits = new ConcurrentHashMap<String, AtomicInteger>();
        final String[] pages = {"home", "produtos", "carrinho"};
        Thread[] workers = new Thread[4];
        for (int t = 0; t < workers.length; t++) {
            workers[t] = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 3000; i++) {
                        String page = pages[i % pages.length];
                        AtomicInteger counter = hits.get(page);
                        if (counter == null) {
                            // putIfAbsent: check-then-act atômico (só um "vence" a corrida)
                            AtomicInteger created = new AtomicInteger();
                            counter = hits.putIfAbsent(page, created);
                            if (counter == null) {
                                counter = created;
                            }
                        }
                        counter.incrementAndGet();
                    }
                }
            });
            workers[t].start();
        }
        for (Thread w : workers) {
            w.join();
        }
        System.out.println("Acessos (esperado 4000 cada): " + hits);
        for (String key : hits.keySet()) {     // iterar e modificar: sem exceção
            hits.putIfAbsent(key + "-v2", new AtomicInteger());
        }
        System.out.println("Chaves após modificar durante a iteração: " + hits.size());

        System.out.println();
        System.out.println("=== CopyOnWriteArrayList: listeners ===");
        final List<String> listeners = new CopyOnWriteArrayList<String>();
        listeners.add("email");
        listeners.add("sms");
        for (String l : listeners) {           // iterador sobre um "snapshot"
            System.out.println("Notificando via " + l);
            if (l.equals("email")) {
                listeners.add("push");          // não afeta a iteração corrente
            }
        }
        System.out.println("Listeners agora: " + listeners);

        System.out.println();
        System.out.println("=== BlockingQueue: produtor/consumidor ===");
        final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(2); // capacidade pequena
        Thread producer = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 1; i <= 5; i++) {
                        queue.put("PED-" + i);  // bloqueia se a fila estiver cheia
                        System.out.println("Produtor enfileirou PED-" + i);
                    }
                    queue.put(POISON_PILL);     // sinaliza o fim
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "produtor");
        Thread consumer = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String order = queue.take(); // bloqueia se vazia
                        if (order.equals(POISON_PILL)) {
                            break;
                        }
                        System.out.println("  Consumidor processou " + order);
                        Thread.sleep(20);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "consumidor");
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("Fila vazia ao final? " + queue.isEmpty());
    }
}
