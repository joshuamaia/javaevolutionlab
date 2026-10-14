package br.com.javalab.java25.instancemain;

import java.util.List;

/**
 * Java 25 - Instance Main Methods (parte da JEP 512, "Compact Source Files and Instance Main Methods")
 *
 * <p>Status: Definitivo. Histórico: Preview nos Java 21 (JEP 445), 22 (JEP 463), 23 (JEP 477)
 * e 24 (JEP 495).</p>
 *
 * <p>O que é: uma classe COMUM (com pacote, declarada) pode ter como ponto de entrada um
 * {@code void main()} de instância - sem {@code public}, sem {@code static}, sem
 * {@code String[] args}. Exceção documentada ao padrão do projeto (que usa
 * {@code public static void main}), porque a nova forma do main é o próprio assunto.</p>
 *
 * <p>Protocolo de seleção do main (launcher {@code java}):</p>
 * <ol>
 *   <li>Procura {@code main(String[])} não privado (static ou de instância); se não houver,
 *       procura {@code main()} sem parâmetros, também não privado. Um {@code main(String[])}
 *       tem prioridade sobre {@code main()}.</li>
 *   <li>Se o método escolhido é {@code static}, é chamado direto.</li>
 *   <li>Se é de instância, a classe precisa ter um construtor sem parâmetros não privado;
 *       o launcher faz {@code new InstanceMainExample()} e chama {@code main()} no objeto.</li>
 * </ol>
 * <p>A classe {@code java.lang.IO} (nova no 25, antes {@code java.io.IO} nas Previews) oferece
 * {@code IO.println}, {@code IO.print} e {@code IO.readln} - como está em {@code java.lang}, não
 * precisa de import. Em classes declaradas, NÃO há import automático de {@code java.base}
 * (isso só vale para arquivos compactos - ver {@code standalone/java25-compact-source}).</p>
 * <p>Benefício: rampa de entrada suave para iniciantes, sem conceitos de {@code static},
 * arrays e modificadores de acesso logo no primeiro programa.</p>
 *
 * <p>Execução (JDK 25) - o IntelliJ também reconhece e roda este main normalmente:</p>
 * <pre>
 * java src/br/com/javalab/java25/instancemain/InstanceMainExample.java
 * </pre>
 */
public class InstanceMainExample {

    private final List<String> steps = List.of("receber pedido", "separar itens", "despachar");
    private int processed;

    void main() {
        IO.println("=== main de instância: campos e métodos sem 'static' ===");
        for (String step : steps) {
            process(step);
        }
        IO.println("Etapas processadas: " + processed);
        IO.println("Objeto criado pelo launcher: " + this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));

        IO.println("");
        IO.println("=== Antes (Java <= 24 sem preview) ===");
        IO.println("public static void main(String[] args) { new InstanceMainExample().run(); }");
    }

    private void process(String step) {
        processed++;
        IO.println(processed + ". " + step);
    }
}
