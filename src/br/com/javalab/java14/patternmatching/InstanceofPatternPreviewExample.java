package br.com.javalab.java14.patternmatching;

/**
 * Java 14 - Pattern Matching for instanceof (JEP 305)
 *
 * <p>Status: Preview. Segunda Preview no Java 15 (JEP 375, sem mudancas);
 * Definitivo no Java 16 (JEP 394). No Java 14 exigia {@code --enable-preview};
 * esta sintaxe permaneceu igual e compila no JDK 25 sem flags.</p>
 *
 * <p>O que e: {@code obj instanceof Tipo variavel} testa o tipo e, se verdadeiro, ja
 * declara uma variavel do tipo testado (pattern variable), sem cast explicito.</p>
 * <p>Beneficio: elimina o trio "testar / fazer cast / declarar variavel" e o risco de
 * fazer cast para o tipo errado.</p>
 * <p>Antes: {@code if (obj instanceof String) { String s = (String) obj; ... }}</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java14/patternmatching/InstanceofPatternPreviewExample.java
 * </pre>
 */
public class InstanceofPatternPreviewExample {

    public static void main(String[] args) {
        Object[] values = {"PEDIDO-1001", 42, 4500.0, java.time.LocalDate.of(2020, 3, 17)};

        System.out.println("=== Abordagem antiga: instanceof + cast ===");
        for (Object value : values) {
            if (value instanceof String) {
                String text = (String) value;
                System.out.println("String de tamanho " + text.length());
            } else if (value instanceof Integer) {
                Integer number = (Integer) value;
                System.out.println("Integer, dobro = " + number * 2);
            } else {
                System.out.println("Outro tipo: " + value.getClass().getSimpleName());
            }
        }

        System.out.println("=== Java 14: instanceof com pattern (Preview) ===");
        for (Object value : values) {
            if (value instanceof String text) {
                System.out.println("String de tamanho " + text.length());
            } else if (value instanceof Integer number) {
                System.out.println("Integer, dobro = " + number * 2);
            } else if (value instanceof Double price) {
                System.out.printf("Double formatado: R$ %.2f%n", price);
            } else {
                System.out.println("Outro tipo: " + value.getClass().getSimpleName());
            }
        }

        System.out.println("=== Pattern variable usada na mesma condicao ===");
        Object code = "PEDIDO-1001";
        if (code instanceof String s && s.startsWith("PEDIDO")) {
            System.out.println("Numero do pedido: " + s.substring(7));
        }
    }
}
