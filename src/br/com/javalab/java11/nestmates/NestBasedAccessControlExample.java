package br.com.javalab.java11.nestmates;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Java 11 - Nest-Based Access Control (JEP 181)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a JVM passou a conhecer o conceito de "ninho" (nest): uma classe de nível
 * superior e suas classes aninhadas formam um grupo cujos membros podem acessar os
 * {@code private} uns dos outros diretamente. Novos atributos no bytecode
 * ({@code NestHost}/{@code NestMembers}) e métodos {@code Class.getNestHost()},
 * {@code getNestMembers()} e {@code isNestmateOf()}.</p>
 * <p>Benefício: fim dos métodos "ponte" sintéticos {@code access$000} que o compilador
 * gerava; reflexão sobre membros privados de colegas de ninho passa a funcionar sem
 * {@code setAccessible(true)}.</p>
 * <p>Antes: a linguagem permitia o acesso, mas a JVM não; o javac gerava métodos
 * package-private sintéticos (aumentando o bytecode e "abrindo" o encapsulamento) e a
 * reflexão lançava {@code IllegalAccessException}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java11/nestmates/NestBasedAccessControlExample.java
 * javap -p -v ...NestBasedAccessControlExample.class | grep -A3 NestMembers
 * </pre>
 */
public class NestBasedAccessControlExample {

    private double balance = 1_000.0;

    private String secretToken() {
        return "token-interno-123";
    }

    class Account {
        private String owner = "Ana";

        void withdraw(double amount) {
            balance -= amount; // acesso direto ao private da classe externa
            System.out.println(owner + " sacou " + amount + "; saldo = " + balance);
        }

        String tokenViaReflection() throws ReflectiveOperationException {
            Method method = NestBasedAccessControlExample.class.getDeclaredMethod("secretToken");
            // Java 11+: sem setAccessible(true), pois Account é nestmate da classe externa
            return (String) method.invoke(NestBasedAccessControlExample.this);
        }
    }

    static class Audit {
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        NestBasedAccessControlExample bank = new NestBasedAccessControlExample();
        Account account = bank.new Account();

        System.out.println("=== Acesso a membros privados entre nestmates ===");
        account.withdraw(250.0);

        System.out.println();
        System.out.println("=== API de reflexão do Java 11 ===");
        System.out.println("Nest host de Account: " + Account.class.getNestHost().getSimpleName());
        System.out.println("Membros do ninho: " + Arrays.stream(NestBasedAccessControlExample.class.getNestMembers())
                .map(Class::getSimpleName).collect(Collectors.toList()));
        System.out.println("Account é nestmate de Audit? " + Account.class.isNestmateOf(Audit.class));
        System.out.println("Account é nestmate de String? " + Account.class.isNestmateOf(String.class));

        System.out.println();
        System.out.println("=== Reflexão privada sem setAccessible ===");
        System.out.println("Token: " + account.tokenViaReflection());

        System.out.println();
        System.out.println("=== Métodos sintéticos (pontes access$NNN) ===");
        long bridges = Arrays.stream(NestBasedAccessControlExample.class.getDeclaredMethods())
                .filter(Method::isSynthetic)
                .filter(m -> m.getName().startsWith("access$"))
                .count();
        System.out.println("Métodos access$ gerados: " + bridges + " (compilado para Java 8 seriam vários)");
    }
}
