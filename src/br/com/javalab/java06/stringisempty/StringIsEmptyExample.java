package br.com.javalab.java06.stringisempty;

/**
 * Java 6 - String.isEmpty()
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: método {@code isEmpty()} em {@code String} (equivale a
 * {@code length() == 0}). O Java 6 também trouxe {@code String(byte[], Charset)} e
 * {@code getBytes(Charset)}, que evitam a {@code UnsupportedEncodingException} checada.</p>
 * <p>Benefício: intenção mais clara que {@code length() == 0} ou {@code "".equals(s)}.</p>
 * <p>Atenção: {@code isEmpty()} considera {@code "   "} NÃO vazia. Verificar espaços exigia
 * {@code trim().isEmpty()}; o Java 11 adicionou {@code isBlank()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/stringisempty/StringIsEmptyExample.java
 * </pre>
 */
public class StringIsEmptyExample {

    /** Java 5: validação com length() ou equals(""). */
    static boolean isMissingOld(String value) {
        return value == null || value.length() == 0;
    }

    /** Java 6: isEmpty(), ainda tratando null e espaços com trim(). */
    static boolean isMissing(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static void main(String[] args) throws Exception {
        String[] inputs = {"Ana", "", "   ", null};

        System.out.println("=== Abordagem antiga: length() == 0 ===");
        for (String in : inputs) {
            System.out.println("[" + in + "] ausente? " + isMissingOld(in));
        }

        System.out.println();
        System.out.println("=== Abordagem nova: isEmpty() (+ trim para espaços) ===");
        for (String in : inputs) {
            String shown = in == null ? "null" : "\"" + in + "\"";
            String empty = in == null ? "n/a (NPE)" : String.valueOf(in.isEmpty());
            System.out.println(shown + " isEmpty=" + empty + ", ausente (com trim)? " + isMissing(in));
        }

        System.out.println();
        System.out.println("=== Bônus Java 6: getBytes(Charset) sem exceção checada ===");
        java.nio.charset.Charset utf8 = java.nio.charset.Charset.forName("UTF-8"); // StandardCharsets só no Java 7
        byte[] bytes = "ação".getBytes(utf8);
        System.out.println("\"ação\" em UTF-8 tem " + bytes.length + " bytes -> " + new String(bytes, utf8));
    }
}
