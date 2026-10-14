package br.com.javalab.java09.varhandles;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 9 - Variable Handles: VarHandle (JEP 193)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma referência tipada a uma variável (campo, elemento de array) que oferece
 * operações atômicas ({@code compareAndSet}, {@code getAndAdd}) e modos de acesso de
 * memória ({@code getVolatile}, {@code setRelease}, {@code getAcquire}, {@code getOpaque}).</p>
 * <p>Benefício: substituto seguro e suportado de {@code sun.misc.Unsafe} e mais
 * eficiente/flexível que {@code AtomicXxxFieldUpdater} (sem reflexão a cada chamada).</p>
 * <p>Antes: {@code AtomicIntegerFieldUpdater} (verificações em tempo de execução) ou
 * {@code sun.misc.Unsafe} (API interna, perigosa).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/varhandles/VarHandleExample.java
 * </pre>
 */
public class VarHandleExample {

    static class StockItem {
        volatile int quantity = 100;
    }

    private static final VarHandle QUANTITY;
    private static final VarHandle INT_ARRAY = MethodHandles.arrayElementVarHandle(int[].class);

    static {
        try {
            QUANTITY = MethodHandles.lookup().findVarHandle(StockItem.class, "quantity", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StockItem keyboard = new StockItem();

        System.out.println("=== compareAndSet: reserva só se o estoque não mudou ===");
        int current = (int) QUANTITY.getVolatile(keyboard);
        boolean reserved = QUANTITY.compareAndSet(keyboard, current, current - 10);
        System.out.println("Reservou 10? " + reserved + " -> estoque " + keyboard.quantity);
        boolean stale = QUANTITY.compareAndSet(keyboard, current, current - 10);
        System.out.println("Nova tentativa com valor antigo (" + current + ")? " + stale);

        System.out.println();
        System.out.println("=== getAndAdd concorrente: 4 threads x 1000 vendas ===");
        List<Thread> threads = new ArrayList<>();
        StockItem mouse = new StockItem();
        QUANTITY.setVolatile(mouse, 10_000);
        for (int t = 0; t < 4; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 1_000; i++) {
                    QUANTITY.getAndAdd(mouse, -1);
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Estoque final (esperado 6000): " + mouse.quantity);

        System.out.println();
        System.out.println("=== VarHandle para elementos de array ===");
        int[] dailySales = new int[7];
        INT_ARRAY.getAndAdd(dailySales, 2, 5);
        INT_ARRAY.setRelease(dailySales, 3, 42);
        System.out.println("Quarta-feira: " + (int) INT_ARRAY.getAcquire(dailySales, 2)
                + " | Quinta-feira: " + (int) INT_ARRAY.getAcquire(dailySales, 3));
    }
}
