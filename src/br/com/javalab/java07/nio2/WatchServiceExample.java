package br.com.javalab.java07.nio2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Java 7 - NIO.2: WatchService (JSR 203)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: serviço que notifica eventos de criação, modificação e remoção de
 * entradas em diretórios registrados.</p>
 * <p>Benefício: monitorar pastas (ex.: diretório de importação de arquivos) sem polling
 * manual de {@code lastModified()}; usa mecanismos nativos do SO quando disponíveis.</p>
 * <p>Antes: thread fazendo {@code File.listFiles()} periodicamente e comparando resultados.</p>
 * <p>Este exemplo usa {@code poll(timeout)} para não bloquear para sempre: uma outra
 * thread cria/modifica/apaga arquivos e o programa termina sozinho.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java07/nio2/WatchServiceExample.java
 * </pre>
 */
public class WatchServiceExample {

    public static void main(String[] args) throws Exception {
        final Path inbox = Files.createTempDirectory("javalab-watch-");
        System.out.println("Monitorando: " + inbox);

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            inbox.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            Thread producer = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                        Path file = inbox.resolve("invoice-001.xml");
                        Files.write(file, Arrays.asList("<invoice/>"), StandardCharsets.UTF_8);
                        Thread.sleep(300);
                        Files.write(inbox.resolve("invoice-002.xml"),
                                Arrays.asList("<invoice/>"), StandardCharsets.UTF_8);
                        Thread.sleep(300);
                        Files.delete(file);
                    } catch (IOException | InterruptedException e) {
                        System.out.println("Produtor falhou: " + e);
                    }
                }
            }, "file-producer");
            producer.start();

            System.out.println("=== Aguardando eventos (poll com timeout de 2s) ===");
            while (true) {
                WatchKey key = watcher.poll(2, TimeUnit.SECONDS);
                if (key == null) {
                    System.out.println("Nenhum evento em 2s: encerrando monitoramento.");
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    Path name = (Path) event.context();
                    System.out.println("  " + event.kind().name() + " -> " + name);
                }
                if (!key.reset()) {
                    System.out.println("Diretório não está mais acessível.");
                    break;
                }
            }
            producer.join();
        } finally {
            Files.deleteIfExists(inbox.resolve("invoice-001.xml"));
            Files.deleteIfExists(inbox.resolve("invoice-002.xml"));
            Files.deleteIfExists(inbox);
        }
        System.out.println("Diretório temporário removido.");
    }
}
