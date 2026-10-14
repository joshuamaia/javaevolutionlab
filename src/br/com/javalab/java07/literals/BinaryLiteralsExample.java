package br.com.javalab.java07.literals;

/**
 * Java 7 - Literais binários (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: literais inteiros podem ser escritos em base 2 com o prefixo {@code 0b} ou {@code 0B}.</p>
 * <p>Benefício: máscaras de bits, flags e protocolos ficam legíveis no próprio código.</p>
 * <p>Antes: usar hexadecimal ({@code 0x05}) ou {@code Integer.parseInt("101", 2)} em tempo de execução.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/literals/BinaryLiteralsExample.java
 * </pre>
 */
public class BinaryLiteralsExample {

    // Permissões de um usuário como flags de bits
    private static final int READ = 0b0001;
    private static final int WRITE = 0b0010;
    private static final int EXECUTE = 0b0100;
    private static final int ADMIN = 0b1000;

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga ===");
        int oldMask = Integer.parseInt("0101", 2); // conversão em tempo de execução
        int oldHex = 0x05;                         // exige conversão mental
        System.out.println("parseInt(\"0101\", 2) = " + oldMask + ", 0x05 = " + oldHex);

        System.out.println();
        System.out.println("=== Java 7: literais binários ===");
        int userPermissions = READ | EXECUTE;
        System.out.println("Permissões do usuário: " + toBits(userPermissions));
        System.out.println("Pode ler?      " + ((userPermissions & READ) != 0));
        System.out.println("Pode escrever? " + ((userPermissions & WRITE) != 0));
        System.out.println("É admin?       " + ((userPermissions & ADMIN) != 0));

        userPermissions |= WRITE;
        System.out.println("Após conceder WRITE: " + toBits(userPermissions));

        System.out.println();
        System.out.println("=== Outros tipos ===");
        byte header = (byte) 0b1010_0001; // pode combinar com underscores (também Java 7)
        long big = 0b1L << 40;
        System.out.println("byte 0b1010_0001 = " + header + " (com sinal)");
        System.out.println("0b1L << 40 = " + big);
    }

    private static String toBits(int value) {
        String bits = Integer.toBinaryString(value);
        while (bits.length() < 4) {
            bits = "0" + bits;
        }
        return "0b" + bits + " (" + value + ")";
    }
}
