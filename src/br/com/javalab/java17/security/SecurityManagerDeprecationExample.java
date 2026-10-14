package br.com.javalab.java17.security;

/**
 * Java 17 - Deprecate the Security Manager for Removal (JEP 411)
 *
 * <p>Status: Deprecated (para remoção) no Java 17. Depois: no Java 18 (JEP 411, fase 2) habilitar em
 * tempo de execução passou a exigir {@code -Djava.security.manager=allow}; no Java 24 (JEP 486)
 * o Security Manager foi desabilitado permanentemente - as APIs ainda existem, mas
 * {@code System.setSecurityManager} sempre lança {@link UnsupportedOperationException}.</p>
 *
 * <p>O que é: o Security Manager (desde o Java 1.0) aplicava políticas de permissão a código
 * não confiável (applets). Era raramente usado em servidores, caro de manter e não protegia
 * contra a maioria das ameaças atuais.</p>
 * <p>Benefício: simplifica o JDK; o isolamento hoje é feito por processos, contêineres e o
 * sistema operacional, e o encapsulamento forte (JEP 403) protege os internos do JDK.</p>
 * <p>Antes: {@code java -Djava.security.manager -Djava.security.policy=app.policy ...}</p>
 *
 * <p>Este é um exemplo de observação: mostra como a JVM atual reage às APIs legadas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/security/SecurityManagerDeprecationExample.java
 * </pre>
 */
@SuppressWarnings("removal") // as APIs do Security Manager estão marcadas forRemoval=true
public class SecurityManagerDeprecationExample {

    public static void main(String[] args) {
        System.out.println("=== Observando o Security Manager ===");
        System.out.println("java.version                 = " + System.getProperty("java.version"));
        System.out.println("System.getSecurityManager()  = " + System.getSecurityManager());
        System.out.println("-Djava.security.manager      = " + System.getProperty("java.security.manager"));

        System.out.println();
        System.out.println("=== Tentando instalar um Security Manager em tempo de execução ===");
        try {
            System.setSecurityManager(new SecurityManager());
            System.out.println("Instalado (JDK 17 sem restrições, ou 18-23 com -Djava.security.manager=allow).");
            System.setSecurityManager(null);
        } catch (UnsupportedOperationException e) {
            System.out.println("UnsupportedOperationException: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("Linha do tempo: 17 deprecated -> 18 desabilitado por padrão -> 24 desabilitado de vez.");
    }
}
