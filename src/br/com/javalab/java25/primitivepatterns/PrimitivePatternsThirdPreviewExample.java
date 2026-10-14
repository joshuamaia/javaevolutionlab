package br.com.javalab.java25.primitivepatterns;

/**
 * Java 25 - Primitive Types in Patterns, instanceof, and switch, terceira Preview (JEP 507)
 *
 * <p>Status: Terceira Preview. Histórico: Preview no Java 23 (JEP 455) e segunda Preview no
 * 24 (JEP 488). A especificação não mudou desde o 23; a JEP 507 re-propõe o recurso para
 * colher mais retorno. Exige {@code --enable-preview}.</p>
 *
 * <p>Foco (o exemplo do 23 mostrou guardas em switch e record patterns): <b>conversões
 * exatas</b>. {@code valor instanceof byte b} só é verdadeiro se o valor couber em
 * {@code byte} SEM perda de informação - o teste de faixa/precisão é feito pela linguagem.
 * E {@code switch} passa a aceitar seletores {@code long}, {@code float}, {@code double} e
 * {@code boolean} (antes só int e menores, String e enum).</p>
 * <p>Benefício: conversões seguras sem casts silenciosos que truncam ({@code (byte) 300 == 44}).</p>
 * <p>Antes: {@code if (x >= Byte.MIN_VALUE && x <= Byte.MAX_VALUE) { byte b = (byte) x; }} e
 * para double/float, comparar ida e volta manualmente.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --enable-preview --source 25 src/br/com/javalab/java25/primitivepatterns/PrimitivePatternsThirdPreviewExample.java
 * </pre>
 */
public class PrimitivePatternsThirdPreviewExample {

    public static void main(String[] args) {
        System.out.println("=== Cast tradicional: perda silenciosa ===");
        int quantity = 300;
        System.out.println("(byte) 300 = " + (byte) quantity + "  <- truncado sem aviso");

        System.out.println();
        System.out.println("=== instanceof com conversão exata ===");
        for (int value : new int[] {42, 300, -128}) {
            if (value instanceof byte b) {
                System.out.println(value + " cabe em byte: " + b);
            } else {
                System.out.println(value + " NÃO cabe em byte");
            }
        }
        double[] prices = {19.0, 19.5, 1e10};
        for (double price : prices) {
            String asInt = price instanceof int i ? "int exato " + i : "não é int exato";
            System.out.println(price + " -> " + asInt);
        }
        long bigId = 16_777_217L; // 2^24 + 1: não representável exatamente em float
        System.out.println(bigId + " instanceof float? " + (bigId instanceof float)
                + " | 16777216 instanceof float? " + (16_777_216L instanceof float));

        System.out.println();
        System.out.println("=== switch sobre long ===");
        long fileSize = 5_000_000_000L;
        String sizeClass = switch (fileSize) {
            case 0L -> "vazio";
            case long s when s < 1_024L -> s + " bytes";
            case long s when s < 1_048_576L -> (s / 1_024) + " KB";
            case long s -> (s / 1_048_576) + " MB";
        };
        System.out.println(fileSize + " -> " + sizeClass);

        System.out.println();
        System.out.println("=== switch sobre double e float ===");
        double rating = 4.5;
        System.out.println("Nota " + rating + ": " + switch (rating) {
            case 5.0 -> "perfeita";
            case double r when r >= 4.0 -> "ótima";
            case double r -> "regular (" + r + ")";
        });
        float discount = 0.1f;
        System.out.println("Desconto: " + switch (discount) {
            case 0f -> "sem desconto";
            case float d -> Math.round(d * 100) + "%";
        });

        System.out.println();
        System.out.println("=== switch sobre boolean ===");
        boolean premiumCustomer = true;
        String shipping = switch (premiumCustomer) {
            case true -> "frete grátis";
            case false -> "frete normal";
        };
        System.out.println("Cliente premium? " + premiumCustomer + " -> " + shipping);
    }
}
