package br.com.javalab.java18.inetaddressspi;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.spi.InetAddressResolver;
import java.net.spi.InetAddressResolverProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.tools.ToolProvider;

/**
 * Java 18 - Internet-Address Resolution SPI (JEP 418)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um Service Provider Interface ({@link InetAddressResolverProvider}) que permite
 * substituir o resolvedor de nomes usado por {@link InetAddress} (e, portanto, por sockets,
 * {@code URL}, {@code HttpClient}...). O JDK procura o provider com {@link ServiceLoader} no
 * <b>class loader do sistema</b>, uma única vez, na primeira resolução de nome da JVM.</p>
 * <p>Benefício: testes sem DNS real, resolução customizada (service discovery, DNS sobre HTTPS),
 * e integração com virtual threads/IO não bloqueante, sem hacks de reflexão.</p>
 * <p>Antes: editar o arquivo hosts, usar a propriedade interna {@code jdk.net.hosts.file}
 * ou reflexão no campo {@code InetAddress.nameService} (quebrava a cada versão).</p>
 *
 * <p>Como este exemplo se mantém autocontido: registrar um provider exige o arquivo
 * {@code META-INF/services/java.net.spi.InetAddressResolverProvider} no classpath. Para não deixar
 * esse arquivo em {@code src/} (ele afetaria todos os outros exemplos), o {@code main} verifica se
 * o provider está ativo; se não estiver, cria um diretório temporário com o arquivo de serviço e
 * relança a si mesmo num processo filho com classpath = classpath atual + diretório temporário.
 * Quando executado em modo código-fonte ({@code java Arquivo.java}), as classes vivem só em memória;
 * nesse caso o exemplo compila o próprio arquivo para o diretório temporário antes de relançar.</p>
 *
 * <p>O resolvedor customizado responde {@code *.javalab.local} com 127.0.0.1 e delega todo o resto
 * ao resolvedor embutido do JDK.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java18/inetaddressspi/InetAddressResolverSpiExample.java
 * </pre>
 */
public class InetAddressResolverSpiExample {

    private static final String CHILD_FLAG = "--child";
    private static final String FAKE_DOMAIN = ".javalab.local";

    /** Provider registrado via META-INF/services (precisa ser público com construtor sem argumentos). */
    public static class LabResolverProvider extends InetAddressResolverProvider {
        @Override
        public InetAddressResolver get(Configuration configuration) {
            return new LabResolver(configuration.builtinResolver());
        }

        @Override
        public String name() {
            return "Java Evolution Lab resolver";
        }
    }

    /** Resolve *.javalab.local localmente e delega o resto ao resolvedor embutido. */
    static class LabResolver implements InetAddressResolver {
        private final InetAddressResolver builtin;

        LabResolver(InetAddressResolver builtin) {
            this.builtin = builtin;
        }

        @Override
        public Stream<InetAddress> lookupByName(String host, LookupPolicy policy) throws UnknownHostException {
            if (host.endsWith(FAKE_DOMAIN)) {
                System.out.println("   [LabResolver] resolvendo " + host + " localmente");
                return Stream.of(InetAddress.getByAddress(host, new byte[] {127, 0, 0, 1}));
            }
            System.out.println("   [LabResolver] delegando " + host + " ao resolvedor embutido");
            return builtin.lookupByName(host, policy);
        }

        @Override
        public String lookupByAddress(byte[] addr) throws UnknownHostException {
            return builtin.lookupByAddress(addr);
        }
    }

    public static void main(String[] args) throws Exception {
        boolean providerActive = ServiceLoader.load(InetAddressResolverProvider.class,
                ClassLoader.getSystemClassLoader()).findFirst().isPresent();
        if (providerActive) {
            runDemo();
        } else if (List.of(args).contains(CHILD_FLAG)) {
            System.out.println("Provider não foi encontrado nem no processo filho; abortando.");
        } else {
            relaunchWithProvider();
        }
    }

    private static void runDemo() throws Exception {
        System.out.println("=== Processo filho: provider ativo ===");
        for (String host : List.of("pedidos.javalab.local", "estoque.javalab.local", "localhost")) {
            InetAddress address = InetAddress.getByName(host);
            System.out.println(host + " -> " + address.getHostAddress());
        }
        System.out.println("Literais IP não passam pelo resolvedor: " + InetAddress.getByName("127.0.0.1"));
    }

    private static void relaunchWithProvider() throws Exception {
        System.out.println("=== Processo pai: provider não registrado; preparando diretório temporário ===");
        Path tempDir = Files.createTempDirectory("javalab-resolver");
        try {
            Path services = tempDir.resolve("META-INF/services");
            Files.createDirectories(services);
            Files.writeString(services.resolve(InetAddressResolverProvider.class.getName()),
                    LabResolverProvider.class.getName() + System.lineSeparator());

            String classPath = buildChildClassPath(tempDir);
            List<String> command = new ArrayList<>();
            command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
            if (ManagementFactory.getRuntimeMXBean().getInputArguments().contains("--enable-preview")) {
                command.add("--enable-preview");
            }
            command.addAll(List.of("-cp", classPath, InetAddressResolverSpiExample.class.getName(), CHILD_FLAG));
            System.out.println("Relançando: java -cp <classpath + " + tempDir.getFileName() + "> ... " + CHILD_FLAG);
            System.out.println();

            Process child = new ProcessBuilder(command).inheritIO().start();
            if (!child.waitFor(30, TimeUnit.SECONDS)) {
                child.destroyForcibly();
                System.out.println("Processo filho excedeu o tempo limite.");
            }
            System.out.println();
            System.out.println("=== Processo pai: filho terminou com código " + child.exitValue() + " ===");
        } finally {
            deleteRecursively(tempDir);
        }
    }

    /** Compilado: classpath atual + tempDir. Modo código-fonte: compila este arquivo para tempDir. */
    private static String buildChildClassPath(Path tempDir) throws Exception {
        var location = InetAddressResolverSpiExample.class.getProtectionDomain().getCodeSource().getLocation();
        Path origin = Path.of(location.toURI());
        if (origin.toString().endsWith(".java")) {
            System.out.println("Modo código-fonte detectado: compilando " + origin.getFileName() + " para o diretório temporário");
            int result = ToolProvider.getSystemJavaCompiler()
                    .run(null, null, null, "-d", tempDir.toString(), origin.toString());
            if (result != 0) {
                throw new IllegalStateException("Falha ao compilar " + origin);
            }
            return tempDir.toString();
        }
        return System.getProperty("java.class.path") + File.pathSeparator + tempDir;
    }

    private static void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }
}
