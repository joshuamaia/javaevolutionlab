package br.com.javalab.java06.overrideinterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java 6 - @Override em métodos que implementam interface
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: no Java 5, {@code @Override} só podia marcar métodos que sobrescrevem um
 * método de SUPERCLASSE; usá-la num método que implementa interface era erro de
 * compilação ("method does not override a method from its superclass"). No Java 6 a
 * regra foi relaxada: vale também para métodos de interface.</p>
 * <p>Benefício: o compilador detecta erros de digitação ou de assinatura também nas
 * implementações de interfaces (ex.: {@code compare(Object, Object)} em vez de
 * {@code compare(Order, Order)}, {@code equal} em vez de {@code equals}).</p>
 * <p>Antes (Java 5): sem a anotação, um método com nome/assinatura errados virava apenas
 * um método novo, e o comportamento padrão era usado silenciosamente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/overrideinterface/OverrideInterfaceExample.java
 * </pre>
 */
public class OverrideInterfaceExample {

    interface PriceRule {
        double apply(double price);

        String description();
    }

    /** Estilo Java 5: sem @Override. O erro de digitação passa despercebido. */
    static class OldBlackFriday implements PriceRule {
        public double apply(double price) {
            return price * 0.7;
        }

        public String description() {
            return "Black Friday (30%)";
        }

        // Pretendia sobrescrever Object.toString(), mas digitou "tostring": compila sem aviso
        public String tostring() {
            return "Regra " + description();
        }
    }

    /** Estilo Java 6: @Override também nos métodos da interface. */
    static class BlackFriday implements PriceRule {
        @Override
        public double apply(double price) {
            return price * 0.7;
        }

        @Override
        public String description() {
            return "Black Friday (30%)";
        }

        @Override // se o nome estivesse errado ("tostring"), seria ERRO de compilação
        public String toString() {
            return "Regra " + description();
        }

        // @Override
        // public double aply(double price) { ... }  -> erro: método não sobrescreve nada
    }

    static class Order {
        final String id;
        final double total;

        Order(String id, double total) {
            this.id = id;
            this.total = total;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Java 5: sem @Override em interface ===");
        OldBlackFriday old = new OldBlackFriday();
        System.out.println("Preço 100 -> " + old.apply(100));
        System.out.println("toString(): " + old + "  <- o método 'tostring' nunca é chamado");

        System.out.println();
        System.out.println("=== Java 6: @Override em interface ===");
        BlackFriday rule = new BlackFriday();
        System.out.println("Preço 100 -> " + rule.apply(100));
        System.out.println("toString(): " + rule);

        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order("PED-2", 300));
        orders.add(new Order("PED-1", 80));
        Collections.sort(orders, new Comparator<Order>() {
            @Override // Java 6: permitido em classe anônima que implementa Comparator
            public int compare(Order a, Order b) {
                return Double.compare(a.total, b.total);
            }
        });
        System.out.print("Pedidos por total:");
        for (Order o : orders) {
            System.out.print(" " + o.id + "=" + o.total);
        }
        System.out.println();
    }
}
