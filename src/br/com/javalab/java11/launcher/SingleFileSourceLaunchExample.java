package br.com.javalab.java11.launcher;

import java.util.Arrays;

/**
 * Java 11 - Executar arquivo de código-fonte diretamente: {@code java Arquivo.java} (JEP 330)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o comando {@code java} aceita um arquivo {@code .java}; ele é compilado em
 * memória (nenhum {@code .class} é gravado em disco) e a primeira classe de nível superior
 * do arquivo é executada.</p>
 * <p>Benefício: scripts e experimentos em Java sem o passo {@code javac}; ideal para
 * aprender (todos os exemplos deste projeto podem ser rodados assim).</p>
 * <p>Antes: {@code javac Arquivo.java} e depois {@code java Arquivo}.</p>
 *
 * <p>Formas de uso:</p>
 * <pre>
 * java src/br/com/javalab/java11/launcher/SingleFileSourceLaunchExample.java pedido-1 pedido-2
 * java --source 11 MeuScript          (arquivo sem extensão .java exige --source)
 * java -cp lib/minha-lib.jar Script.java  (pode usar bibliotecas do classpath)
 * </pre>
 *
 * <p>Shebang (Linux/macOS): um arquivo sem extensão .java pode começar com
 * {@code #!/usr/bin/java --source 11} e ser executado com {@code ./script}. A linha shebang
 * NÃO é Java válido: o lançador a ignora, mas {@code javac} falharia — por isso este arquivo
 * não a contém. Ver {@code standalone/java11-shebang/}.</p>
 *
 * <p>Limitações no Java 11: só um arquivo (o Java 22 permitiu vários - JEP 458); a
 * classe não pode estar em conflito com uma classe do classpath.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/launcher/SingleFileSourceLaunchExample.java pedido-1 pedido-2
 * </pre>
 */
public class SingleFileSourceLaunchExample {

    public static void main(String[] args) {
        System.out.println("=== Como esta classe foi carregada? ===");
        ClassLoader loader = SingleFileSourceLaunchExample.class.getClassLoader();
        String loaderName = loader.getClass().getName();
        boolean fromSource = loaderName.contains("MemoryClassLoader");
        System.out.println("ClassLoader: " + loaderName);
        System.out.println(fromSource
                ? "Modo código-fonte: compilada em memória pelo lançador (JEP 330)."
                : "Modo tradicional: carregada de um .class compilado com javac.");

        System.out.println();
        System.out.println("=== Argumentos recebidos ===");
        System.out.println(args.length == 0 ? "(nenhum - tente passar 'pedido-1 pedido-2')" : Arrays.toString(args));
    }
}
