package br.com.javalab.java08.nashorn;

import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * Java 8 - Nashorn JavaScript Engine (JEP 174)
 *
 * <p>Status: Removido. Introduzido no Java 8 (substituindo o Rhino), deprecated no Java 11
 * (JEP 335) e removido do JDK no Java 15 (JEP 372). Continua disponível como projeto
 * independente ({@code org.openjdk.nashorn:nashorn-core}), fora do escopo deste laboratório.</p>
 *
 * <p>O que era: motor JavaScript de alto desempenho baseado em {@code invokedynamic},
 * acessível pela API {@code javax.script} (JSR 223) e pela ferramenta {@code jjs}.</p>
 * <p>No Java 8 o uso era:</p>
 * <pre>
 * ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
 * Object result = engine.eval("var total = 10 * 1.1; total");
 * </pre>
 * <p>Este exemplo NÃO simula o Nashorn: apenas consulta a API {@code javax.script}
 * (que ainda existe) e mostra que, no JDK atual, o motor "nashorn" não está presente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/nashorn/NashornAvailabilityCheck.java
 * </pre>
 */
public class NashornAvailabilityCheck {

    public static void main(String[] args) throws Exception {
        System.out.println("Versão da JVM: " + System.getProperty("java.version"));

        System.out.println();
        System.out.println("=== Motores registrados em javax.script (JSR 223) ===");
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        if (factories.isEmpty()) {
            System.out.println("(nenhum motor de script disponível)");
        }
        for (ScriptEngineFactory f : factories) {
            System.out.println("  " + f.getEngineName() + " " + f.getEngineVersion() + " -> " + f.getNames());
        }

        System.out.println();
        System.out.println("=== Procurando 'nashorn' ===");
        ScriptEngine engine = manager.getEngineByName("nashorn");
        if (engine == null) {
            System.out.println("Nashorn indisponível: removido do JDK no Java 15 (JEP 372).");
            System.out.println("Alternativas: GraalJS (GraalVM Polyglot) ou nashorn-core standalone.");
        } else {
            // Só acontece em JDK 8–14 (ou com nashorn-core no classpath)
            System.out.println("Nashorn encontrado! 10 * 1.1 = " + engine.eval("10 * 1.1"));
        }
    }
}
