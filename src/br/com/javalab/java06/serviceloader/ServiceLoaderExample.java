package br.com.javalab.java06.serviceloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

/**
 * Java 6 - java.util.ServiceLoader
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: mecanismo padrão de descoberta de implementações (Service Provider
 * Interface). Um JAR declara provedores em {@code META-INF/services/<nome binário da
 * interface>}, um nome de classe por linha; {@code ServiceLoader.load(Interface.class)}
 * encontra e instancia (preguiçosamente) os provedores visíveis no ClassLoader.</p>
 * <p>Benefício: plugins desacoplados; basta colocar um JAR no classpath. É como o JDBC 4
 * acha drivers, como o {@code javax.script} acha engines etc. No Java 9 ganhou
 * {@code provides ... with} em {@code module-info.java}.</p>
 * <p>Antes: a classe interna e não suportada {@code sun.misc.Service}, ou ler um
 * {@code .properties} com nomes de classes e usar {@code Class.forName().newInstance()}.</p>
 *
 * <p>Para não depender de arquivos externos, o exemplo cria em tempo de execução um
 * diretório temporário com o arquivo {@code META-INF/services/...} e um
 * {@code URLClassLoader} que o enxerga (o pai é o ClassLoader atual, que já conhece as
 * classes aninhadas). Atenção: classes aninhadas têm nome binário com {@code $}
 * ({@code Outer$Inner}), e é esse nome que vai no arquivo.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/serviceloader/ServiceLoaderExample.java
 * </pre>
 */
public class ServiceLoaderExample {

    /** O "serviço" (SPI): forma de pagamento. */
    public interface PaymentProvider {
        String name();

        double fee(double amount);
    }

    /** Provedores: public, static, com construtor público sem argumentos. */
    public static class PixProvider implements PaymentProvider {
        @Override
        public String name() { return "PIX"; }

        @Override
        public double fee(double amount) { return 0.0; }
    }

    public static class CreditCardProvider implements PaymentProvider {
        @Override
        public String name() { return "Cartão de crédito"; }

        @Override
        public double fee(double amount) { return amount * 0.035; }
    }

    static int countProviders(ServiceLoader<PaymentProvider> loader) {
        int count = 0;
        for (PaymentProvider p : loader) {
            count++;
        }
        return count;
    }

    /** Java 6 não tem java.nio.file (Java 7): diretório temporário à moda antiga. */
    static File createTempDir() throws IOException {
        File dir = File.createTempFile("javalab-spi", "");
        if (!dir.delete() || !dir.mkdir()) {
            throw new IOException("Não foi possível criar " + dir);
        }
        return dir;
    }

    static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            file.deleteOnExit();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Sem META-INF/services: nenhum provedor ===");
        ServiceLoader<PaymentProvider> empty = ServiceLoader.load(PaymentProvider.class);
        System.out.println("Provedores no classpath atual: " + countProviders(empty));

        File root = createTempDir();
        try {
            File services = new File(root, "META-INF/services");
            services.mkdirs();
            String serviceFileName = PaymentProvider.class.getName(); // nome BINÁRIO: ...$PaymentProvider
            File config = new File(services, serviceFileName);
            Writer writer = new OutputStreamWriter(new FileOutputStream(config), "UTF-8");
            try {
                writer.write("# provedores de pagamento (um por linha)\n");
                writer.write(PixProvider.class.getName() + "\n");
                writer.write(CreditCardProvider.class.getName() + "\n");
            } finally {
                writer.close(); // try-with-resources só no Java 7
            }

            System.out.println();
            System.out.println("=== Arquivo de configuração criado ===");
            System.out.println("META-INF/services/" + serviceFileName);
            System.out.println("  " + PixProvider.class.getName());
            System.out.println("  " + CreditCardProvider.class.getName());

            System.out.println();
            System.out.println("=== ServiceLoader.load(Interface, ClassLoader) ===");
            URLClassLoader pluginLoader = new URLClassLoader(new URL[] {root.toURI().toURL()},
                    ServiceLoaderExample.class.getClassLoader());
            ServiceLoader<PaymentProvider> providers = ServiceLoader.load(PaymentProvider.class, pluginLoader);
            double amount = 200.0;
            for (PaymentProvider provider : providers) { // instanciação preguiçosa, durante a iteração
                System.out.printf("%-18s taxa sobre R$ %.2f = R$ %.2f%n",
                        provider.name(), amount, provider.fee(amount));
            }
            providers.reload(); // limpa o cache; útil se novos JARs/plugins forem adicionados
            System.out.println("Provedores após reload(): " + countProviders(providers));
            // URLClassLoader.close() só existe a partir do Java 7; aqui deixamos para o GC.
        } finally {
            deleteRecursively(root);
            System.out.println("Diretório temporário removido: " + !root.exists());
        }
    }
}
