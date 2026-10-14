package br.com.javalab.java05.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Java 5 - Interface Queue e PriorityQueue
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code java.util.Queue} (FIFO genérica, com {@code offer/poll/peek}, que
 * retornam false/null em vez de lançar exceção, e {@code add/remove/element}, que
 * lançam). {@code LinkedList} passou a implementar {@code Queue}.
 * {@code PriorityQueue} é um heap binário: {@code poll()} devolve sempre o menor
 * elemento segundo a ordem natural ou um {@code Comparator}, em O(log n).</p>
 * <p>Benefício: filas de prioridade prontas (tickets, agendamentos, algoritmos como
 * Dijkstra) sem reordenar a lista inteira a cada inserção.</p>
 * <p>Antes: {@code LinkedList}/{@code Vector} manipulados como fila e
 * {@code Collections.sort} a cada inserção.</p>
 * <p>Obs.: o iterador/toString de {@code PriorityQueue} NÃO segue a ordem de prioridade.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/queue/PriorityQueueExample.java
 * </pre>
 */
public class PriorityQueueExample {

    static class Ticket {
        final String id;
        final int priority; // 1 = mais urgente

        Ticket(String id, int priority) {
            this.id = id;
            this.priority = priority;
        }

        @Override
        public String toString() {
            return id + "(p" + priority + ")";
        }
    }

    static final Comparator<Ticket> BY_PRIORITY = new Comparator<Ticket>() {
        public int compare(Ticket a, Ticket b) { // sem @Override (interface, Java 5)
            return a.priority < b.priority ? -1 : (a.priority == b.priority ? 0 : 1);
        }
    };

    static Ticket[] incoming() {
        return new Ticket[] {
                new Ticket("T1-dúvida", 3), new Ticket("T2-site-fora", 1),
                new Ticket("T3-boleto", 2), new Ticket("T4-pagamento-falhou", 1)};
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: lista reordenada a cada inserção ===");
        List<Ticket> list = new ArrayList<Ticket>();
        for (Ticket t : incoming()) {
            list.add(t);
            Collections.sort(list, BY_PRIORITY); // O(n log n) a cada inserção
        }
        while (!list.isEmpty()) {
            System.out.println("Atendendo " + list.remove(0));
        }

        System.out.println();
        System.out.println("=== Queue FIFO (LinkedList) ===");
        Queue<String> fifo = new LinkedList<String>();
        fifo.offer("PED-1");
        fifo.offer("PED-2");
        System.out.println("peek=" + fifo.peek() + ", poll=" + fifo.poll() + ", poll=" + fifo.poll()
                + ", poll em fila vazia=" + fifo.poll());

        System.out.println();
        System.out.println("=== Abordagem nova: PriorityQueue com Comparator ===");
        Queue<Ticket> tickets = new PriorityQueue<Ticket>(11, BY_PRIORITY);
        for (Ticket t : incoming()) {
            tickets.offer(t); // O(log n)
        }
        System.out.println("toString (ordem do heap, NÃO de prioridade): " + tickets);
        while (!tickets.isEmpty()) {
            System.out.println("Atendendo " + tickets.poll());
        }

        System.out.println();
        System.out.println("=== PriorityQueue com ordem natural: próximos prazos (dias) ===");
        PriorityQueue<Integer> deadlines = new PriorityQueue<Integer>();
        Collections.addAll(deadlines, 15, 3, 30, 7);
        System.out.print("Prazos em ordem:");
        while (!deadlines.isEmpty()) {
            System.out.print(" " + deadlines.poll());
        }
        System.out.println();
    }
}
