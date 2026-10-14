package br.com.javalab.java18.utf8default;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 18 - UTF-8 by Default (JEP 400)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o charset padrão ({@link Charset#defaultCharset()}) passou a ser UTF-8 em todas as
 * plataformas, independentemente da localidade do sistema operacional. Afeta APIs que usam o
 * charset padrão quando nenhum é informado: {@code FileReader}, {@code FileWriter},
 * {@code InputStreamReader}, {@code PrintStream}, {@code String.getBytes()}, etc.</p>
 * <p>Benefício: um arquivo escrito no Windows (antes Cp1252) e lido no Linux (UTF-8) não
 * corrompe mais acentos. Código portável por padrão.</p>
 * <p>Antes: o padrão dependia do SO/localidade (Windows: Cp1252; Linux/macOS: geralmente UTF-8),
 * e era preciso sempre passar o charset explicitamente.</p>
 *
 * <p>Propriedades úteis:</p>
 * <ul>
 *   <li>{@code file.encoding}: agora "UTF-8" (ou "COMPAT" para o comportamento antigo:
 *       {@code java -Dfile.encoding=COMPAT ...}).</li>
 *   <li>{@code native.encoding} (Java 17+): o charset que a plataforma usaria (ex.: Cp1252).</li>
 *   <li>{@code stdout.encoding}/{@code stderr.encoding} (Java 19+): charset de {@code System.out};
 *       continua seguindo o console, por isso acentos podem aparecer errados num terminal Windows
 *       que não está em UTF-8 ({@code chcp 65001} resolve).</li>
 * </ul>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java18/utf8default/Utf8ByDefaultExample.java
 * java -Dfile.encoding=COMPAT src/br/com/javalab/java18/utf8default/Utf8ByDefaultExample.java
 * </pre>
 */
public class Utf8ByDefaultExample {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Charsets da JVM ===");
        System.out.println("Charset.defaultCharset() = " + Charset.defaultCharset());
        System.out.println("file.encoding            = " + System.getProperty("file.encoding"));
        System.out.println("native.encoding          = " + System.getProperty("native.encoding"));
        System.out.println("stdout.encoding          = " + System.getProperty("stdout.encoding")
                + " (propriedade do Java 19+; null no 18)");

        Path file = Files.createTempFile("clientes", ".txt");
        try {
            String customer = "João Conceição - São Paulo";

            System.out.println();
            System.out.println("=== Escrevendo SEM charset explícito (FileWriter) ===");
            try (Writer writer = new FileWriter(file.toFile())) {
                writer.write(customer);
            }
            byte[] bytes = Files.readAllBytes(file);
            System.out.println("Texto: " + customer.length() + " caracteres; arquivo: " + bytes.length + " bytes");
            System.out.println("Bytes iguais ao UTF-8? "
                    + java.util.Arrays.equals(bytes, customer.getBytes(StandardCharsets.UTF_8)));

            System.out.println();
            System.out.println("=== Lendo SEM charset explícito (FileReader) ===");
            try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                String read = reader.readLine();
                System.out.println("Ida e volta preservou o texto? " + customer.equals(read));
            }

            System.out.println();
            System.out.println("=== O que aconteceria com o charset nativo antigo ===");
            Charset nativeCharset = Charset.forName(System.getProperty("native.encoding"));
            String misread = new String(bytes, nativeCharset);
            System.out.println("Lendo os mesmos bytes como " + nativeCharset + " -> igual? " + customer.equals(misread)
                    + (customer.equals(misread) ? " (seu SO já usa UTF-8)" : " (acentos corrompidos: mojibake)"));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
