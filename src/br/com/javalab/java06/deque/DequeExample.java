package br.com.javalab.java06.deque;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Java 6 - Deque, ArrayDeque e BlockingDeque
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code Deque} ("deck", double ended queue) permite inserir e remover nas
 * duas pontas: {@code offerFirst/offerLast}, {@code pollFirst/pollLast},
 * {@code peekFirst/peekLast}, e também {@code push/pop/peek} (pilha).
 * {@code ArrayDeque} é a implementação baseada em array circular (sem sincronização,
 * não aceita null). {@code LinkedBlockingDeque} é a versão concorrente e bloqueante
 * ({@code BlockingDeque}). {@code LinkedList} também passou a implementar Deque.</p>
 * <p>Benefício: pilha e fila eficientes com a mesma API; {@code ArrayDeque} é mais
 * rápida que {@code Stack} (sincronizada, herda de Vector) e que {@code LinkedList}.</p>
 * <p>Antes: {@code java.util.Stack} (que expõe métodos de Vector, como
 * {@code add(index)}, quebrando o encapsulamento da pilha) e {@code LinkedList} como fila.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/deque/DequeExample.java
 * </pre>
 */
public class DequeExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Abordagem antiga: java.util.Stack (histórico de navegação) ===");
        Stack<String> oldHistory = new Stack<String>();
        oldHistory.push("/home");
        oldHistory.push("/produtos");
        oldHistory.push("/carrinho");
        oldHistory.add(0, "/??"); // Stack herda de Vector: permite inserir no "fundo" da pilha
        System.out.println("Voltar: " + oldHistory.pop() + "; pilha (de baixo para cima): " + oldHistory);

        System.out.println();
        System.out.println("=== Abordagem nova: ArrayDeque como pilha (LIFO) ===");
        Deque<String> history = new ArrayDeque<String>();
        history.push("/home");          // = addFirst
        history.push("/produtos");
        history.push("/carrinho");
        System.out.println("Topo (peek): " + history.peek());
        System.out.println("Voltar (pop): " + history.pop() + "; pilha (do topo): " + history);

        System.out.println();
        System.out.println("=== ArrayDeque como fila (FIFO) e nas duas pontas ===");
        Deque<String> orders = new ArrayDeque<String>();
        orders.offerLast("PED-1");
        orders.offerLast("PED-2");
        orders.offerFirst("PED-VIP");   // fura a fila
        System.out.println("Fila: " + orders + "; primeiro=" + orders.peekFirst() + ", último=" + orders.peekLast());
        System.out.println("pollFirst=" + orders.pollFirst() + ", pollLast=" + orders.pollLast() + ", resta=" + orders);
        System.out.print("descendingIterator: ");
        orders.offerLast("PED-3");
        for (Iterator<String> it = orders.descendingIterator(); it.hasNext();) {
            System.out.print(it.next() + " ");
        }
        System.out.println();

        System.out.println();
        System.out.println("=== Janela deslizante: últimas 3 leituras de temperatura ===");
        Deque<Integer> window = new ArrayDeque<Integer>();
        for (int reading : new int[] {21, 22, 25, 24, 30}) {
            window.offerLast(reading);
            if (window.size() > 3) {
                window.pollFirst();
            }
            System.out.println("leitura " + reading + " -> janela " + window);
        }

        System.out.println();
        System.out.println("=== LinkedBlockingDeque (concorrente, com capacidade) ===");
        final BlockingDeque<String> tasks = new LinkedBlockingDeque<String>(2);
        tasks.putLast("tarefa-normal");
        tasks.putFirst("tarefa-urgente");
        System.out.println("Cheia; offerLast com timeout de 100 ms: "
                + tasks.offerLast("tarefa-extra", 100, TimeUnit.MILLISECONDS));
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() { // Java 6: @Override vale para métodos de interface
                try {
                    String task;
                    while ((task = tasks.pollFirst(200, TimeUnit.MILLISECONDS)) != null) {
                        System.out.println("Worker executou " + task);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        worker.start();
        worker.join();
    }
}
