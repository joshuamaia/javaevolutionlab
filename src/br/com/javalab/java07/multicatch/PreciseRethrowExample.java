package br.com.javalab.java07.multicatch;

import java.io.FileNotFoundException;
import java.sql.SQLException;

/**
 * Java 7 - Rethrow mais preciso (Project Coin, JSR 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: ao relançar uma exceção capturada como {@code catch (Exception e)} (sem
 * reatribuí-la), o compilador sabe quais exceções checadas o bloco try realmente pode
 * lançar e permite declarar apenas essas no {@code throws}.</p>
 * <p>Benefício: logar/limpar e relançar de forma genérica sem "alargar" a assinatura
 * do método para {@code throws Exception}.</p>
 * <p>Antes (Java 6): {@code throw e} com {@code e} do tipo Exception obrigava o método a
 * declarar {@code throws Exception}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/multicatch/PreciseRethrowExample.java
 * </pre>
 */
public class PreciseRethrowExample {

    public static void main(String[] args) {
        System.out.println("=== Rethrow preciso ===");
        for (int scenario = 1; scenario <= 2; scenario++) {
            try {
                importCustomers(scenario);
            } catch (FileNotFoundException e) {
                System.out.println("Chamador tratou FileNotFoundException: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Chamador tratou SQLException: " + e.getMessage());
            }
        }
    }

    /**
     * Captura {@code Exception}, mas declara apenas as exceções checadas reais.
     * No Java 6 este método precisaria declarar {@code throws Exception}.
     */
    private static void importCustomers(int scenario) throws FileNotFoundException, SQLException {
        try {
            if (scenario == 1) {
                throw new FileNotFoundException("customers.csv não encontrado");
            }
            throw new SQLException("Tabela CUSTOMERS bloqueada");
        } catch (Exception e) {
            System.out.println("Log genérico antes de relançar: " + e.getClass().getSimpleName());
            throw e; // o compilador sabe que só pode ser FileNotFoundException ou SQLException
        }
    }
}
