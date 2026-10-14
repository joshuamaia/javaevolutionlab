package br.com.javalab.java10.typeinference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java 10 - Inferência de tipo para variáveis locais: {@code var} (JEP 286)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code var} deixa o compilador inferir o tipo de uma variável local a partir
 * do inicializador. A tipagem continua <b>estática</b>: o tipo é fixado na compilação.
 * {@code var} é um "nome de tipo reservado", não uma palavra-chave (ainda é possível ter
 * uma variável chamada {@code var}).</p>
 * <p>Benefício: menos ruído com tipos genéricos longos, mantendo a legibilidade quando o
 * nome da variável e o inicializador já deixam o tipo claro.</p>
 * <p>Antes: o tipo era sempre repetido: {@code Map<String, List<Order>> m = new HashMap<>();}</p>
 *
 * <p>Onde pode: variáveis locais com inicializador, índice do {@code for}, variável do
 * for-each, recurso do try-with-resources. Onde NÃO pode: campos, parâmetros de método,
 * tipos de retorno, variável sem inicializador, inicializador {@code null}, lambda ou
 * referência de método sem tipo alvo, array initializer ({@code var a = {1, 2};}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/typeinference/LocalVariableTypeInferenceExample.java
 * </pre>
 */
public class LocalVariableTypeInferenceExample {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Antes (Java 9) ===");
        Map<String, List<Double>> oldOrdersByCustomer = new HashMap<>();
        oldOrdersByCustomer.put("Ana", new ArrayList<>(List.of(150.0, 89.9)));
        for (Map.Entry<String, List<Double>> entry : oldOrdersByCustomer.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println();
        System.out.println("=== Depois (Java 10): var ===");
        var ordersByCustomer = new HashMap<String, List<Double>>();
        ordersByCustomer.put("Bruno", new ArrayList<>(List.of(299.0)));
        for (var entry : ordersByCustomer.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println();
        System.out.println("=== var em for, try-with-resources ===");
        for (var i = 1; i <= 2; i++) {
            System.out.println("Parcela " + i);
        }
        try (var reader = new BufferedReader(new StringReader("linha-1\nlinha-2"))) {
            System.out.println("Primeira linha: " + reader.readLine());
        }

        System.out.println();
        System.out.println("=== O tipo inferido é estático ===");
        var total = 10;           // int
        // total = "dez";         // não compila: incompatible types: String cannot be converted to int
        var price = 10.0;         // double
        var items = new ArrayList<String>(); // ArrayList<String> (não List<String>!)
        items.add("Teclado");
        System.out.println("total é " + ((Object) total).getClass().getSimpleName()
                + ", price é " + ((Object) price).getClass().getSimpleName()
                + ", items é " + items.getClass().getSimpleName());

        System.out.println();
        System.out.println("=== Armadilhas (boas práticas) ===");
        var empty = new ArrayList<>(); // ArrayList<Object>: diamante + var perde o tipo!
        empty.add(42);
        empty.add("texto");
        System.out.println("new ArrayList<>() com var vira ArrayList<Object>: " + empty);
        var discount = 0;              // int — se a intenção era long/double, declare o tipo
        System.out.println("var discount = 0 infere int: " + discount);
        System.out.println("Prefira var quando o tipo é óbvio pelo lado direito (new, literal, factory clara).");
        System.out.println("Evite var com retorno de método pouco claro: var result = service.process();");

        System.out.println();
        System.out.println("=== Limitações (não compilam) ===");
        System.out.println("var x;                    -> cannot infer type: no initializer");
        System.out.println("var nothing = null;       -> variable initializer is 'null'");
        System.out.println("var f = () -> 1;          -> lambda expression needs an explicit target-type");
        System.out.println("var array = {1, 2};       -> array initializer needs an explicit target-type");
        System.out.println("private var field = 1;    -> 'var' is not allowed here (campos)");

        var var = "var ainda pode ser nome de variável";
        System.out.println(var);
    }
}
