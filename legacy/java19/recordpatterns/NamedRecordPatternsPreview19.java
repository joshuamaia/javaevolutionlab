import java.util.List;

/**
 * Java 19 - Record Patterns, Preview (JEP 405) - SINTAXE ORIGINAL
 *
 * <p>Status: Preview no Java 19. Depois: segunda Preview no 20 (JEP 432) e definitivo no 21 (JEP 440).</p>
 *
 * <p>O que é: desestruturar um record diretamente no pattern:
 * {@code if (obj instanceof Point(int x, int y))}, inclusive aninhado.</p>
 * <p>O que existia SÓ no Java 19: <b>named record patterns</b> - um identificador após o pattern,
 * que dava nome ao record inteiro além de desestruturá-lo:
 * {@code case Line(Point start, Point end) line}. Foi REMOVIDO no Java 20 (JEP 432) porque
 * duplicava o que já se consegue com um type pattern, e por isso este arquivo não compila no JDK 25.
 * Na forma atual, ou se desestrutura ({@code Line(Point s, Point e)}) ou se nomeia ({@code Line line}).</p>
 *
 * <p>Execução (requer JDK 19 - não validado: JDK 19 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 19 legacy/java19/recordpatterns/NamedRecordPatternsPreview19.java
 * </pre>
 */
public class NamedRecordPatternsPreview19 {

    record Point(int x, int y) { }
    record Route(Point origin, Point destination) { }

    public static void main(String[] args) {
        System.out.println("=== Record pattern com nome (named record pattern, só Java 19) ===");
        Object delivery = new Route(new Point(0, 0), new Point(3, 4));

        if (delivery instanceof Route(Point origin, Point destination) route) {
            System.out.println("Rota completa: " + route);
            System.out.println("Origem " + origin + " -> destino " + destination);
        }

        System.out.println();
        System.out.println("=== Aninhado em switch (Preview: ainda exigia --enable-preview) ===");
        for (Object o : List.of(new Route(new Point(1, 1), new Point(4, 5)), new Point(2, 2), "texto")) {
            System.out.println(describe(o));
        }
    }

    static String describe(Object o) {
        return switch (o) {
            case Route(Point(var x1, var y1) from, Point(var x2, var y2) to) route ->
                    "Rota de " + from + " até " + to + ", distância " + Math.hypot(x2 - x1, y2 - y1);
            case Point(int x, int y) p -> "Ponto isolado " + p + " (soma " + (x + y) + ")";
            default -> "Desconhecido: " + o;
        };
    }
}
