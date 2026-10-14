package br.com.javalab.java07.nio2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

/**
 * Java 7 - NIO.2: DirectoryStream com glob (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@link DirectoryStream} itera o conteúdo de UM diretório de forma
 * preguiçosa (lazy) e aceita filtro por padrão glob ({@code *.csv}, {@code *.{csv,txt}}).
 * {@link PathMatcher} permite usar glob/regex sobre qualquer Path.</p>
 * <p>Benefício: escala melhor que {@code File.list()} (que carrega tudo num array) e
 * deve ser fechado com try-with-resources.</p>
 * <p>Antes: {@code File.listFiles(FilenameFilter)} com lógica de filtro manual.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/DirectoryStreamExample.java
 * </pre>
 */
public class DirectoryStreamExample {

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("javalab-dirstream-");
        String[] names = {"sales-2011.csv", "sales-2012.csv", "notes.txt", "photo.png", "readme.md"};
        for (String name : names) {
            Files.createFile(dir.resolve(name));
        }

        try {
            System.out.println("=== Todos os arquivos ===");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path p : stream) {
                    System.out.println("  " + p.getFileName());
                }
            }

            System.out.println();
            System.out.println("=== Glob: *.csv ===");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.csv")) {
                for (Path p : stream) {
                    System.out.println("  " + p.getFileName());
                }
            }

            System.out.println();
            System.out.println("=== Glob: *.{txt,md} ===");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{txt,md}")) {
                for (Path p : stream) {
                    System.out.println("  " + p.getFileName());
                }
            }

            System.out.println();
            System.out.println("=== Filtro customizado (DirectoryStream.Filter) ===");
            DirectoryStream.Filter<Path> bigNames = new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) {
                    return entry.getFileName().toString().startsWith("sales-");
                }
            };
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, bigNames)) {
                for (Path p : stream) {
                    System.out.println("  " + p.getFileName());
                }
            }

            System.out.println();
            System.out.println("=== PathMatcher (glob e regex) ===");
            PathMatcher glob = FileSystems.getDefault().getPathMatcher("glob:**/*.png");
            PathMatcher regex = FileSystems.getDefault().getPathMatcher("regex:.*-20\\d\\d\\.csv");
            Path sample = Paths.get("images", "photo.png");
            System.out.println("glob:**/*.png casa com images/photo.png? " + glob.matches(sample));
            System.out.println("regex casa com sales-2012.csv? " + regex.matches(Paths.get("sales-2012.csv")));
        } finally {
            for (String name : names) {
                Files.deleteIfExists(dir.resolve(name));
            }
            Files.delete(dir);
        }
    }
}
