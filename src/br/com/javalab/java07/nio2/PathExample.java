package br.com.javalab.java07.nio2;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Java 7 - NIO.2: Path e Paths (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link Path} representa um caminho no sistema de arquivos de forma
 * independente de plataforma, com operações de manipulação puramente sintáticas
 * (não acessam o disco): resolve, relativize, normalize, getParent, getFileName...</p>
 * <p>Benefício: API rica e imutável, que substitui a manipulação de caminhos por String
 * e a classe {@code java.io.File}.</p>
 * <p>Antes: {@code new File(dir, "nome")}, concatenação de Strings com {@code File.separator}.</p>
 * <p>Obs.: {@code Path.of(...)} só existe a partir do Java 11; no Java 7 usa-se {@code Paths.get(...)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/PathExample.java
 * </pre>
 */
public class PathExample {

    public static void main(String[] args) {
        System.out.println("=== Criando Paths ===");
        Path base = Paths.get("data", "reports", "2011");
        Path report = base.resolve("sales.csv");
        System.out.println("base:     " + base);
        System.out.println("report:   " + report);
        System.out.println("separador do FS padrão: " + FileSystems.getDefault().getSeparator());

        System.out.println();
        System.out.println("=== Partes do caminho ===");
        System.out.println("fileName:  " + report.getFileName());
        System.out.println("parent:    " + report.getParent());
        System.out.println("root:      " + report.getRoot() + " (relativo não tem raiz)");
        System.out.println("nameCount: " + report.getNameCount());
        for (int i = 0; i < report.getNameCount(); i++) {
            System.out.println("  name(" + i + ") = " + report.getName(i));
        }
        System.out.println("subpath(1,3): " + report.subpath(1, 3));

        System.out.println();
        System.out.println("=== normalize, relativize, resolveSibling ===");
        Path messy = Paths.get("data", "reports", "..", "logs", ".", "app.log");
        System.out.println("messy:       " + messy);
        System.out.println("normalize(): " + messy.normalize());
        Path logs = Paths.get("data", "logs");
        System.out.println("relativize(report -> logs): " + report.relativize(logs));
        System.out.println("resolveSibling: " + report.resolveSibling("returns.csv"));

        System.out.println();
        System.out.println("=== Absoluto e conversões ===");
        Path absolute = report.toAbsolutePath();
        System.out.println("isAbsolute(): " + report.isAbsolute() + " -> " + absolute.isAbsolute());
        System.out.println("toUri():      " + absolute.toUri());
        System.out.println("startsWith(\"data\"): " + report.startsWith("data"));
        System.out.println("endsWith(\"sales.csv\"): " + report.endsWith("sales.csv"));
        System.out.println("toFile() (ponte com java.io): " + report.toFile().getName());
    }
}
