package br.com.javalab.java24.securitymanager;

/**
 * Java 24 - Permanently Disable the Security Manager (JEP 486)
 *
 * <p>Status: Removido (funcionalmente). Histórico: o Security Manager existia desde o Java 1.0;
 * foi marcado Deprecated for Removal no Java 17 (JEP 411). No Java 24 não é mais possível
 * ativá-lo - nem na inicialização ({@code -Djava.security.manager=...}) nem em tempo de
 * execução. A API ({@code SecurityManager}, {@code System.getSecurityManager()},
 * {@code AccessController}...) continua existindo para compatibilidade binária, mas sem efeito.</p>
 *
 * <p>Por que: era caro de manter, quase ninguém usava, não protegia contra boa parte das
 * ameaças modernas e o modelo de permissões era complexo demais.</p>
 * <p>Alternativas: isolamento por processo/contêiner, módulos (encapsulamento forte),
 * políticas do sistema operacional, agentes que bloqueiam chamadas específicas.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/securitymanager/SecurityManagerDisabledExample.java
 * # tentar habilitar na inicialização falha logo ao subir a JVM:
 * java -Djava.security.manager=allow src/br/com/javalab/java24/securitymanager/SecurityManagerDisabledExample.java
 * </pre>
 */
public class SecurityManagerDisabledExample {

    @SuppressWarnings("removal") // as APIs existem, mas estão deprecated para remoção
    public static void main(String[] args) {
        System.out.println("=== Estado do Security Manager ===");
        System.out.println("System.getSecurityManager() = " + System.getSecurityManager());
        System.out.println("java.security.manager = " + System.getProperty("java.security.manager"));

        System.out.println();
        System.out.println("=== Tentando instalar um SecurityManager em tempo de execução ===");
        try {
            System.setSecurityManager(new SecurityManager());
            System.out.println("Instalado (comportamento do Java 17-23 com -Djava.security.manager=allow)");
        } catch (UnsupportedOperationException e) {
            System.out.println("UnsupportedOperationException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Antes (Java <= 23) ===");
        System.out.println("Com -Djava.security.manager e um arquivo .policy, este código poderia ser");
        System.out.println("impedido de ler arquivos, abrir sockets ou chamar System.exit().");
    }
}
