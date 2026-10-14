package br.com.javalab.java07.literals;

/**
 * Java 7 - Underscores em literais numéricos (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o caractere {@code _} pode separar dígitos em qualquer literal numérico
 * (decimal, hexadecimal, binário, octal, long, float, double). É ignorado pelo compilador.</p>
 * <p>Benefício: números grandes ficam legíveis (milhares, cartões, máscaras).</p>
 * <p>Antes: {@code 1000000} — fácil errar a quantidade de zeros.</p>
 * <p>Regras: não pode no início/fim do número, junto ao ponto decimal, antes de sufixo
 * ({@code L}, {@code F}) nem logo após o prefixo {@code 0x}/{@code 0b}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/literals/UnderscoreLiteralsExample.java
 * </pre>
 */
public class UnderscoreLiteralsExample {

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga ===");
        long oldBudget = 1500000000L;
        System.out.println("Orçamento: " + oldBudget + " (quantos zeros?)");

        System.out.println();
        System.out.println("=== Java 7: underscores ===");
        long budget = 1_500_000_000L;
        double price = 1_299.90;
        int hexColor = 0xFF_88_00;
        int mask = 0b1111_0000;
        long cardNumber = 4111_1111_1111_1111L;

        System.out.println("Orçamento:   " + budget);
        System.out.println("Preço:       " + price);
        System.out.println("Cor (hex):   " + Integer.toHexString(hexColor));
        System.out.println("Máscara:     " + Integer.toBinaryString(mask));
        System.out.println("Cartão:      " + cardNumber);
        System.out.println("Iguais? " + (oldBudget == budget));

        // Exemplos que NÃO compilam:
        // int a = _100;      // começa com underscore (vira identificador)
        // int b = 100_;      // termina com underscore
        // double c = 3_.14;  // junto ao ponto
        // long d = 100_L;    // antes do sufixo
        // int e = 0x_FF;     // logo após o prefixo
    }
}
