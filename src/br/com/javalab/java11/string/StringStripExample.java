package br.com.javalab.java11.string;

/**
 * Java 11 - String.strip, stripLeading, stripTrailing
 *
 * <p>Status: Definitivo (API, sem JEP própria).</p>
 *
 * <p>O que é: {@code strip()} remove espaços em branco do início e do fim usando a
 * definição <b>Unicode</b> ({@code Character.isWhitespace}). {@code stripLeading()} e
 * {@code stripTrailing()} removem só de um lado.</p>
 * <p>Benefício: limpa corretamente textos vindos de web, PDFs e planilhas, que costumam
 * ter espaços Unicode (EM SPACE, IDEOGRAPHIC SPACE...).</p>
 * <p>Antes: {@code trim()} (Java 1.0) remove apenas caracteres com código {@code <= U+0020}
 * (espaço ASCII e controles). Não havia versão só de um lado.</p>
 *
 * <p>Observação: o NO-BREAK SPACE (U+00A0) NÃO é removido por nenhum dos dois,
 * pois {@code Character.isWhitespace(0x00A0)} é {@code false}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/string/StringStripExample.java
 * </pre>
 */
public class StringStripExample {

    public static void main(String[] args) {
        String asciiPadded = "   Teclado Mecânico   ";
        String unicodePadded = "\u2003\u3000Mouse Gamer\u2003"; // EM SPACE e IDEOGRAPHIC SPACE

        System.out.println("=== Espaços ASCII: trim e strip se comportam igual ===");
        System.out.println("trim : [" + asciiPadded.trim() + "]");
        System.out.println("strip: [" + asciiPadded.strip() + "]");

        System.out.println();
        System.out.println("=== Espaços Unicode: só strip remove ===");
        System.out.println("original : [" + show(unicodePadded) + "] length=" + unicodePadded.length());
        System.out.println("trim()   : [" + show(unicodePadded.trim()) + "] length=" + unicodePadded.trim().length());
        System.out.println("strip()  : [" + show(unicodePadded.strip()) + "] length=" + unicodePadded.strip().length());

        System.out.println();
        System.out.println("=== stripLeading / stripTrailing ===");
        String code = "   SKU-123   ";
        System.out.println("stripLeading : [" + code.stripLeading() + "]");
        System.out.println("stripTrailing: [" + code.stripTrailing() + "]");

        System.out.println();
        System.out.println("=== Caso especial: NO-BREAK SPACE (\\u00A0) ===");
        String nbsp = "\u00A0Monitor\u00A0";
        System.out.println("strip() remove \\u00A0? " + (nbsp.strip().length() != nbsp.length()));
    }

    private static String show(String value) {
        return value.replace("\u2003", "<EM>").replace("\u3000", "<IDEO>");
    }
}
