package br.com.javalab.java24.scopedvalues;

import java.io.IOException;

/**
 * Java 24 - Scoped Values, quarta Preview (JEP 487)
 *
 * <p>Status: Quarta Preview. Histórico: incubadora no 20 (JEP 429), Preview no 21 (JEP 446),
 * 22 (JEP 464) e 23 (JEP 481); definitivo no Java 25 (JEP 506) com a mesma API deste
 * exemplo - por isso compila no JDK 25 sem flags (no 24 exigia {@code --enable-preview}).</p>
 *
 * <p>O que mudou no 24: os métodos estáticos {@code ScopedValue.runWhere(...)} e
 * {@code ScopedValue.callWhere(...)} foram REMOVIDOS (ver o legado do Java 23). A API ficou
 * totalmente fluente: {@code ScopedValue.where(k1, v1).where(k2, v2).run(...)} ou
 * {@code .call(...)}. O {@code call} recebe um {@code CallableOp<T, X>} (desde o 23), então
 * só a exceção checada realmente lançada precisa ser tratada.</p>
 * <p>Foco deste exemplo: vários valores no mesmo escopo (encadeando {@code where}),
 * exceção checada específica em {@code call} e REBINDING aninhado (um escopo interno
 * sobrepõe o valor temporariamente).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/scopedvalues/ScopedValuesFourthPreviewExample.java
 * </pre>
 */
public class ScopedValuesFourthPreviewExample {

    private static final ScopedValue<String> USER = ScopedValue.newInstance();
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) {
        System.out.println("=== Dois valores no mesmo escopo (where encadeado) ===");
        ScopedValue.where(USER, "alice")
                .where(REQUEST_ID, "req-001")
                .run(ScopedValuesFourthPreviewExample::log);

        System.out.println();
        System.out.println("=== call(...) com exceção checada específica (IOException) ===");
        try {
            String content = ScopedValue.where(USER, "bob").call(ScopedValuesFourthPreviewExample::exportReport);
            System.out.println(content);
        } catch (IOException e) { // não é preciso capturar Exception genérica
            System.out.println("Falha de I/O: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Rebinding aninhado: tarefa executada 'como sistema' ===");
        ScopedValue.where(USER, "carla").where(REQUEST_ID, "req-002").run(() -> {
            log();
            ScopedValue.where(USER, "system").run(ScopedValuesFourthPreviewExample::log); // sobrepõe só USER
            log(); // ao sair do escopo interno, volta para carla
        });
    }

    static void log() {
        System.out.println("[" + REQUEST_ID.orElse("sem-id") + "] usuário=" + USER.get());
    }

    static String exportReport() throws IOException {
        if (USER.get().isBlank()) throw new IOException("usuário vazio");
        return "relatório exportado por " + USER.get();
    }
}
