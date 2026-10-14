package br.com.javalab.java23.moduleimport;

import module java.base;

/**
 * Java 23 - Module Import Declarations (JEP 476)
 *
 * <p>Status: Preview. Evolução: segunda Preview no Java 24 (JEP 494) e definitivo no
 * Java 25 (JEP 511). A forma usada aqui ({@code import module java.base;}) não mudou, por
 * isso compila no JDK 25 sem flags (no JDK 23 exigia {@code --enable-preview}).</p>
 *
 * <p>O que é: {@code import module M;} importa de uma vez todos os pacotes exportados pelo
 * módulo M (e pelos módulos que M exige de forma transitiva). A classe que usa isso NÃO
 * precisa ser um módulo - funciona no classpath.</p>
 * <p>Benefício: menos linhas de import em scripts, protótipos e no ensino; um único import
 * traz {@code java.util}, {@code java.util.stream}, {@code java.time}, {@code java.nio.file},
 * {@code java.util.function}, etc.</p>
 * <p>Antes: um import por pacote ou por classe ({@code import java.util.*;},
 * {@code import java.time.LocalDate;}, ...).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java23/moduleimport/ModuleImportExample.java
 * </pre>
 */
public class ModuleImportExample {

    public static void main(String[] args) throws IOException {
        System.out.println("=== Uma linha 'import module java.base;' e nenhum outro import ===");

        // java.util + java.util.stream
        List<String> products = List.of("teclado", "mouse", "monitor", "webcam");
        Map<Integer, List<String>> byLength = products.stream()
                .collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.toList()));
        System.out.println("Produtos por tamanho do nome: " + byLength);

        // java.time
        LocalDate delivery = LocalDate.of(2024, Month.SEPTEMBER, 17).plusDays(5);
        System.out.println("Entrega prevista: " + delivery.format(DateTimeFormatter.ISO_DATE));

        // java.util.function
        Function<Double, Double> withTax = price -> price * 1.1;
        System.out.printf("Preço com imposto: %.2f%n", withTax.apply(100.0));

        // java.nio.file + java.io
        Path temp = Files.createTempFile("pedido", ".txt");
        try {
            Files.writeString(temp, String.join(System.lineSeparator(), products));
            System.out.println("Linhas gravadas no arquivo temporário: " + Files.readAllLines(temp).size());
        } finally {
            Files.deleteIfExists(temp);
        }

        System.out.println("Pacotes exportados por java.base: "
                + Object.class.getModule().getDescriptor().exports().stream().filter(e -> !e.isQualified()).count());
    }
}
