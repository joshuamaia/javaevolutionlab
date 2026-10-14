package br.com.javalab.java05.varargs;

import java.util.Arrays;
import java.util.List;

/**
 * Java 5 - Varargs (JSR 201)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o último parâmetro de um método pode ser declarado como {@code Tipo...},
 * aceitando zero ou mais argumentos. Internamente é um array: o compilador cria o
 * array na chamada.</p>
 * <p>Benefício: APIs mais naturais ({@code String.format}, {@code printf},
 * {@code Arrays.asList}, {@code Method.invoke}, todas do Java 5 em diante).</p>
 * <p>Antes: o chamador precisava criar o array à mão ({@code new Object[] {...}}) ou a
 * API oferecia várias sobrecargas (1, 2, 3 parâmetros...).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/varargs/VarargsExample.java
 * </pre>
 */
public class VarargsExample {

    /** Estilo antigo: o chamador monta o array. */
    static double sumOld(double[] values) {
        double total = 0;
        for (int i = 0; i < values.length; i++) {
            total += values[i];
        }
        return total;
    }

    /** Estilo novo: varargs. */
    static double sum(double... values) {
        double total = 0;
        for (double v : values) {
            total += v;
        }
        return total;
    }

    /** Parâmetros fixos antes do varargs (varargs sempre por último). */
    static void log(String level, String format, Object... args) {
        System.out.println("[" + level + "] " + String.format(format, args)
                + "   (args.length=" + args.length + ")");
    }

    static int count(Object... items) {
        return items == null ? -1 : items.length;
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: array explícito ===");
        System.out.println("Total: " + sumOld(new double[] {10.0, 20.5, 5.0}));

        System.out.println();
        System.out.println("=== Abordagem nova: varargs ===");
        System.out.println("Total: " + sum(10.0, 20.5, 5.0));
        System.out.println("Sem argumentos: " + sum());
        System.out.println("Array também é aceito: " + sum(new double[] {1, 2}));

        log("INFO", "Pedido %s criado para %s", "PED-77", "Ana");
        log("WARN", "Estoque baixo");
        log("INFO", "Pedido %s: %d itens, total R$ %.2f", "PED-78", 3, 149.9);

        List<String> skus = Arrays.asList("SKU-1", "SKU-2", "SKU-3"); // Arrays.asList(T...)
        System.out.println("Arrays.asList com varargs: " + skus);

        System.out.println();
        System.out.println("=== Armadilhas ===");
        System.out.println("count(null) -> " + count((Object[]) null) + " (o próprio array é null)");
        System.out.println("count((Object) null) -> " + count((Object) null) + " (array com 1 elemento null)");
        String[] names = {"a", "b"};
        System.out.println("count(String[]) -> " + count((Object[]) names) + " (array vira os argumentos)");
        System.out.println("count((Object) String[]) -> " + count((Object) names) + " (array vira 1 argumento)");

        int[] primitive = {1, 2, 3};
        // int[] não é Object[]: T vira int[] e a lista tem UM elemento
        System.out.println("Arrays.asList(int[]).size() = " + Arrays.asList(primitive).size());
        Integer[] boxed = {1, 2, 3};
        System.out.println("Arrays.asList(Integer[]).size() = " + Arrays.asList(boxed).size());
    }
}
