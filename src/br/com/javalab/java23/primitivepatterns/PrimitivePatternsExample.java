package br.com.javalab.java23.primitivepatterns;

/**
 * Java 23 - Primitive Types in Patterns, instanceof, and switch (JEP 455)
 *
 * <p>Status: Preview. Evolução: segunda Preview no Java 24 (JEP 488) e terceira no
 * Java 25 (JEP 507), ambas sem mudanças. Como ainda é Preview no JDK 25, este exemplo
 * precisa de {@code --enable-preview}.</p>
 *
 * <p>O que é: patterns passam a aceitar tipos primitivos em qualquer contexto: em
 * {@code switch} (inclusive {@code case int i when ...}), em {@code instanceof} e dentro
 * de record patterns, com conversões primitivas (alargamento/estreitamento) verificadas.</p>
 * <p>Benefício: unifica patterns para todos os tipos; acaba a limitação de que
 * {@code switch} com patterns só funcionava sobre referências e que o seletor primitivo só
 * aceitava constantes.</p>
 * <p>Antes: {@code switch (int)} só com constantes ({@code case 200 ->}); faixas de valores
 * exigiam cadeias de {@code if/else}; record patterns exigiam o tipo exato do componente.</p>
 *
 * <p>Foco deste exemplo: patterns primitivos com guardas em {@code switch} e dentro de
 * record patterns. O exemplo do Java 25 explora conversões exatas com {@code instanceof} e
 * {@code switch} sobre {@code long}, {@code float}, {@code double} e {@code boolean}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --enable-preview --source 25 src/br/com/javalab/java23/primitivepatterns/PrimitivePatternsExample.java
 * </pre>
 */
public class PrimitivePatternsExample {

    record HttpResponse(int status, String body) {}
    record Measurement(String sensor, int value) {}

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: if/else para faixas ===");
        int code = 503;
        String old;
        if (code >= 200 && code < 300) old = "sucesso";
        else if (code >= 400 && code < 500) old = "erro do cliente";
        else if (code >= 500) old = "erro do servidor";
        else old = "outro";
        System.out.println(code + " -> " + old);

        System.out.println();
        System.out.println("=== switch com pattern primitivo e guardas ===");
        for (int status : new int[] {200, 204, 301, 404, 503}) {
            String category = switch (status) {
                case 200 -> "OK";                                  // constante continua valendo
                case int s when s >= 200 && s < 300 -> "sucesso (" + s + ")";
                case int s when s >= 400 && s < 500 -> "erro do cliente (" + s + ")";
                case int s when s >= 500 -> "erro do servidor (" + s + ")";
                case int s -> "outro (" + s + ")";                 // pattern total: dispensa default
            };
            System.out.println(status + " -> " + category);
        }

        System.out.println();
        System.out.println("=== Record pattern com componente primitivo ===");
        Object[] responses = {new HttpResponse(201, "{id: 9}"), new HttpResponse(500, "")};
        for (Object obj : responses) {
            if (obj instanceof HttpResponse(int status, String body) && status < 300) {
                System.out.println("Sucesso com corpo " + body);
            } else if (obj instanceof HttpResponse(int status, _)) {
                System.out.println("Falha com status " + status);
            }
        }

        System.out.println();
        System.out.println("=== Conversão no record pattern: int casado como double ===");
        Measurement m = new Measurement("temperatura", 23);
        if (m instanceof Measurement(String sensor, double value)) { // alargamento int -> double
            System.out.println(sensor + " = " + value / 2 + " (metade, já em double)");
        }
    }
}
