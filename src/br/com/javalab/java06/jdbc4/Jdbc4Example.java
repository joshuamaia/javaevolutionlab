package br.com.javalab.java06.jdbc4;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLNonTransientException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransientConnectionException;
import java.sql.SQLTransientException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Java 6 - JDBC 4.0 (JSR 221)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é (o que este exemplo demonstra SEM banco de dados):</p>
 * <ul>
 *   <li>Carregamento automático de drivers: o {@code DriverManager} usa o
 *   {@code ServiceLoader} para achar {@code META-INF/services/java.sql.Driver} nos JARs do
 *   classpath. Acabou o {@code Class.forName("org.postgresql.Driver")}.</li>
 *   <li>Hierarquia nova de exceções: {@code SQLTransientException} (tentar de novo pode
 *   funcionar), {@code SQLNonTransientException} (não adianta repetir sem corrigir a
 *   causa) e {@code SQLRecoverableException} (recuperável reabrindo a conexão), com
 *   subclasses por categoria de SQLState.</li>
 *   <li>{@code SQLException} implementa {@code Iterable<Throwable>}: percorre a cadeia de
 *   {@code getNextException()} e causas com for-each.</li>
 * </ul>
 * <p>Outras novidades (só documentadas): {@code Wrapper.unwrap/isWrapperFor}, tipos
 * {@code RowId}, {@code SQLXML}, {@code NClob}, {@code Connection.isValid},
 * {@code createBlob/createClob}.</p>
 * <p>Antes: {@code Class.forName(...)} obrigatório e apenas {@code SQLException} genérica
 * com códigos de erro específicos do fabricante.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/jdbc4/Jdbc4Example.java
 * </pre>
 */
public class Jdbc4Example {

    /** Driver mínimo (NÃO é um banco): aceita URLs "jdbc:exemplo:" e simula falha de rede. */
    public static class ExampleDriver implements Driver {
        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            if (!acceptsURL(url)) {
                return null; // contrato: null = "não é comigo", DriverManager tenta o próximo
            }
            SQLException timeout = new SQLTransientConnectionException(
                    "Servidor exemplo indisponível (timeout)", "08001");
            timeout.setNextException(new SQLRecoverableException("Socket fechado pelo servidor", "08006"));
            throw timeout;
        }

        @Override
        public boolean acceptsURL(String url) {
            return url != null && url.startsWith("jdbc:exemplo:");
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
            return new DriverPropertyInfo[0];
        }

        @Override
        public int getMajorVersion() { return 1; }

        @Override
        public int getMinorVersion() { return 0; }

        @Override
        public boolean jdbcCompliant() { return false; }

        // INEVITÁVEL: método adicionado no JDBC 4.1 (Java 7). Sem ele a classe não compila nos
        // JDKs atuais. Sem @Override de propósito, pois não existe na interface do Java 6.
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new SQLFeatureNotSupportedException("sem logging");
        }
    }

    static void listDrivers(String title) {
        System.out.println(title);
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        int count = 0;
        while (drivers.hasMoreElements()) {
            System.out.println("  - " + drivers.nextElement().getClass().getName());
            count++;
        }
        if (count == 0) {
            System.out.println("  (nenhum: o JDK não traz drivers; com um JAR JDBC 4 no classpath ele apareceria aqui)");
        }
    }

    static String classify(SQLException e) {
        if (e instanceof SQLTransientException) {
            return "TRANSITÓRIA -> tentar novamente (retry)";
        } else if (e instanceof SQLRecoverableException) {
            return "RECUPERÁVEL -> fechar e reabrir a conexão";
        } else if (e instanceof SQLNonTransientException) {
            return "NÃO TRANSITÓRIA -> corrigir o problema; repetir não resolve";
        }
        return "genérica";
    }

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga (JDBC 3) ===");
        System.out.println("Class.forName(\"com.fabricante.Driver\"); // registro manual obrigatório");

        System.out.println();
        listDrivers("=== JDBC 4: drivers carregados automaticamente via ServiceLoader ===");

        System.out.println();
        System.out.println("=== URL sem driver ===");
        try {
            DriverManager.getConnection("jdbc:exemplo:pedidos", "app", "secreta");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage() + " (SQLState=" + e.getSQLState() + ")");
        }

        System.out.println();
        System.out.println("=== Registrando um Driver mínimo (simulação, sem banco) ===");
        ExampleDriver driver = new ExampleDriver();
        try {
            DriverManager.registerDriver(driver);
            listDrivers("Drivers agora:");
            DriverManager.getConnection("jdbc:exemplo:pedidos", "app", "secreta");
        } catch (SQLException e) {
            System.out.println("Falha ao conectar: " + e.getClass().getSimpleName() + " - " + classify(e));
            System.out.println("Percorrendo a cadeia com for-each (SQLException é Iterable<Throwable>):");
            for (Throwable t : e) {
                System.out.println("  -> " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        } finally {
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException ignored) {
                // nada a fazer
            }
        }

        System.out.println();
        System.out.println("=== Hierarquia de exceções do JDBC 4 ===");
        SQLException[] samples = {
                new SQLTimeoutException("consulta excedeu 30s"),
                new SQLTransientConnectionException("conexão recusada temporariamente", "08001"),
                new SQLSyntaxErrorException("SELEC * FROM pedido", "42000"),
                new SQLIntegrityConstraintViolationException("pedido duplicado", "23000"),
                new SQLNonTransientConnectionException("URL inválida", "08001"),
                new SQLRecoverableException("conexão perdida", "08006"),
                new SQLFeatureNotSupportedException("savepoints não suportados")
        };
        for (SQLException e : samples) {
            System.out.printf("%-42s %s%n", e.getClass().getSimpleName(), classify(e));
        }
    }
}
