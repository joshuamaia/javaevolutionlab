package br.com.javalab.java05.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Java 5 - Annotations / Metadata (JSR 175)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: metadados declarativos ({@code @Nome(elemento = valor)}) anexados a
 * classes, métodos, campos, parâmetros etc. A {@code @Retention} define até onde a
 * anotação sobrevive: SOURCE (só no compilador), CLASS (no .class, padrão) ou RUNTIME
 * (visível por reflexão).</p>
 * <p>Benefício: substitui convenções de nomes, arquivos XML e comentários "mágicos"
 * (XDoclet, tags javadoc) por metadados tipados e verificados pelo compilador. Base de
 * JUnit 4, JPA, Spring, Bean Validation...</p>
 * <p>Antes: convenções (métodos {@code testXxx} no JUnit 3), interfaces marcadoras
 * ({@code Serializable}) e a tag javadoc {@code @deprecated}.</p>
 * <p>Anotações padrão do Java 5: {@code @Override}, {@code @Deprecated},
 * {@code @SuppressWarnings}. Atenção: no Java 5, {@code @Override} NÃO podia ser usada
 * em métodos que implementam interface (só passou a valer no Java 6).</p>
 * <p>Ferramenta da época: {@code apt} (Annotation Processing Tool), substituída pela
 * JSR 269 no Java 6 e removida no Java 8.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/annotations/AnnotationExample.java
 * </pre>
 */
public class AnnotationExample {

    /** Anotação customizada, visível em runtime, aplicável a métodos. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Audited {
        String value();                  // elemento obrigatório

        String level() default "INFO";   // elemento com valor padrão
    }

    /** Anotação de classe. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Table {
        String name();
    }

    /** Retenção SOURCE: descartada pelo compilador, invisível na reflexão. */
    @Retention(RetentionPolicy.SOURCE)
    @interface Reviewed {
    }

    @Table(name = "TB_ORDER")
    static class OrderService {

        @Audited("Criação de pedido")
        public void createOrder() {
            System.out.println("    -> pedido criado");
        }

        @Audited(value = "Cancelamento de pedido", level = "WARN")
        public void cancelOrder() {
            System.out.println("    -> pedido cancelado");
        }

        @Reviewed
        public void listOrders() {
            System.out.println("    -> listando pedidos");
        }

        /** @deprecated use {@link #createOrder()} */
        @Deprecated
        public void legacyCreate() {
            createOrder();
        }

        @Override // garante em tempo de compilação que estamos sobrescrevendo Object.toString()
        public String toString() {
            return "OrderService";
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    static void suppressedWarnings() {
        List rawList = new ArrayList();   // raw type
        rawList.add("sem aviso unchecked");
        // Uso de método @Deprecated. Obs.: aqui o aviso nem seria emitido, pois a JLS isenta
        // usos dentro da mesma classe de topo; em outra classe, "deprecation" o suprimiria.
        new OrderService().legacyCreate();
        System.out.println("    " + rawList);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Lendo anotações por reflexão ===");
        Class<OrderService> type = OrderService.class;
        Table table = type.getAnnotation(Table.class);
        System.out.println("Classe " + type.getSimpleName() + " mapeada para tabela " + table.name());

        Method[] methods = type.getDeclaredMethods();
        // getDeclaredMethods não garante ordem: ordenamos (classe anônima, sem lambda)
        Arrays.sort(methods, new Comparator<Method>() {
            public int compare(Method a, Method b) { // sem @Override: interface no Java 5
                return a.getName().compareTo(b.getName());
            }
        });

        OrderService service = new OrderService();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Audited.class)) {
                Audited audited = m.getAnnotation(Audited.class);
                System.out.println("[AUDIT " + audited.level() + "] " + audited.value()
                        + " (método " + m.getName() + ")");
                m.invoke(service);
            } else {
                System.out.println("Método " + m.getName() + " sem @Audited; anotações em runtime: "
                        + m.getAnnotations().length
                        + (m.getName().equals("listOrders") ? " (@Reviewed é SOURCE)" : ""));
            }
        }

        System.out.println();
        System.out.println("=== Anotações padrão ===");
        Method legacy = type.getMethod("legacyCreate");
        System.out.println("legacyCreate é @Deprecated em runtime? " + legacy.isAnnotationPresent(Deprecated.class));
        System.out.println("@SuppressWarnings tem retenção SOURCE; executando método com avisos suprimidos:");
        suppressedWarnings();
    }
}
