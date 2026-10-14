package br.com.javalab.java07.character;

/**
 * Java 7 - Character.getName e suporte a Unicode 6.0
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o Java 7 atualizou o suporte para Unicode 6.0 e adicionou
 * {@code Character.getName(int codePoint)} (nome oficial Unicode do caractere),
 * {@code Character.UnicodeScript} (script/sistema de escrita) e
 * {@code Character.isAlphabetic}/{@code isIdeographic}.</p>
 * <p>Benefício: inspecionar e validar textos internacionais sem tabelas externas
 * (ex.: detectar caracteres de scripts diferentes num nome de usuário).</p>
 * <p>Antes: só havia {@code UnicodeBlock}, sem nomes nem scripts.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/character/CharacterGetNameExample.java
 * </pre>
 */
public class CharacterGetNameExample {

    public static void main(String[] args) {
        System.out.println("=== Character.getName ===");
        int[] codePoints = {'A', 'ç', '€', 'Ω', 0x4E2D, 0x1F600};
        for (int cp : codePoints) {
            System.out.printf("U+%04X -> %s%n", cp, Character.getName(cp));
        }

        System.out.println();
        System.out.println("=== Character.UnicodeScript: detectar mistura de scripts ===");
        String[] usernames = {"joshua", "jоshua"}; // o segundo usa 'о' cirílico (homógrafo)
        for (String username : usernames) {
            Character.UnicodeScript first = null;
            boolean mixed = false;
            for (int i = 0; i < username.length(); i++) {
                Character.UnicodeScript script = Character.UnicodeScript.of(username.charAt(i));
                if (first == null) {
                    first = script;
                } else if (script != first) {
                    mixed = true;
                    System.out.println("  posição " + i + ": " + Character.getName(username.charAt(i))
                            + " (" + script + ")");
                }
            }
            System.out.println("'" + username + "' mistura scripts? " + mixed);
        }

        System.out.println();
        System.out.println("=== isAlphabetic / isIdeographic ===");
        System.out.println("isAlphabetic('ç'): " + Character.isAlphabetic('ç'));
        System.out.println("isIdeographic(U+4E2D): " + Character.isIdeographic(0x4E2D));
    }
}
