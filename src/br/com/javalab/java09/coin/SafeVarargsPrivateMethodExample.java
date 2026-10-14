package br.com.javalab.java09.coin;

import java.util.ArrayList;
import java.util.List;

/**
 * Java 9 - {@code @SafeVarargs} em métodos privados de instância (JEP 213 - Milling Project Coin)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a anotação {@code @SafeVarargs} (Java 7) agora pode ser usada também em
 * métodos {@code private} de instância.</p>
 * <p>Benefício: suprimir o aviso "possible heap pollution" em métodos varargs genéricos
 * privados sem torná-los {@code static} ou {@code final}.</p>
 * <p>Antes (Java 7/8): só era permitida em construtores e métodos {@code static} ou
 * {@code final} — métodos que não podem ser sobrescritos. Métodos privados também não
 * podem, mas a regra não os incluía.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/coin/SafeVarargsPrivateMethodExample.java
 * </pre>
 */
public class SafeVarargsPrivateMethodExample {

    private final List<String> history = new ArrayList<>();

    public void registerOrders() {
        addAll(history, "pedido-1", "pedido-2");
        addAll(history, "pedido-3");
    }

    // Java 9: @SafeVarargs permitido em método privado de instância
    @SafeVarargs
    private <T> void addAll(List<T> target, T... items) {
        for (T item : items) {
            target.add(item);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Método varargs genérico privado com @SafeVarargs ===");
        SafeVarargsPrivateMethodExample example = new SafeVarargsPrivateMethodExample();
        example.registerOrders();
        System.out.println("Histórico: " + example.history);
        System.out.println("Compile com -Xlint:unchecked: nenhum aviso de 'heap pollution' é emitido.");
    }
}
