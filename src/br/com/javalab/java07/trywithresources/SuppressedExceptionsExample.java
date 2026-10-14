package br.com.javalab.java07.trywithresources;

/**
 * Java 7 - Throwable.addSuppressed / getSuppressed
 *
 * <p>Status: Definitivo (API adicionada junto com o try-with-resources, JSR 334).</p>
 *
 * <p>O que é: {@code Throwable} ganhou uma lista de exceções "suprimidas", usada
 * automaticamente pelo try-with-resources e disponível para uso manual.</p>
 * <p>Benefício: permite acumular várias falhas (ex.: fechar vários arquivos, validar
 * vários itens) sem perder nenhuma informação.</p>
 * <p>Antes: só havia {@code getCause()}, que representa UMA causa, não falhas paralelas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/trywithresources/SuppressedExceptionsExample.java
 * </pre>
 */
public class SuppressedExceptionsExample {

    public static void main(String[] args) {
        String[] invoiceFiles = {"invoice-01.xml", "invoice-02.xml", "invoice-03.xml"};

        System.out.println("=== Acumulando falhas manualmente com addSuppressed ===");
        IllegalStateException batchFailure = null;
        for (String file : invoiceFiles) {
            try {
                process(file);
            } catch (IllegalArgumentException e) {
                if (batchFailure == null) {
                    batchFailure = new IllegalStateException("Lote de notas fiscais com erros");
                }
                batchFailure.addSuppressed(e);
            }
        }

        if (batchFailure != null) {
            System.out.println("Exceção: " + batchFailure.getMessage());
            System.out.println("Quantidade de falhas suprimidas: " + batchFailure.getSuppressed().length);
            for (Throwable t : batchFailure.getSuppressed()) {
                System.out.println("  - " + t.getMessage());
            }
        }
    }

    private static void process(String file) {
        if (file.endsWith("02.xml") || file.endsWith("03.xml")) {
            throw new IllegalArgumentException("XML inválido em " + file);
        }
        System.out.println("Processado: " + file);
    }
}
