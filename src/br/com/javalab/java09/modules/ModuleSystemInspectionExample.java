package br.com.javalab.java09.modules;

import java.lang.module.ModuleDescriptor;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Java 9 - Java Platform Module System (JEP 261 / JSR 376) e JDK modular (JEP 200)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: o código passa a ser organizado em módulos ({@code module-info.java}) que
 * declaram explicitamente suas dependências ({@code requires}) e quais pacotes ficam
 * visíveis para fora ({@code exports}). O próprio JDK foi quebrado em ~70 módulos
 * ({@code java.base}, {@code java.sql}, {@code java.net.http}...).</p>
 * <p>Benefício: encapsulamento forte (pacote não exportado é inacessível, mesmo que
 * {@code public}), configuração confiável (dependência faltando = erro na inicialização,
 * não {@code NoClassDefFoundError} em produção) e runtimes menores com {@code jlink}.</p>
 * <p>Antes: tudo no classpath, sem fronteiras — qualquer classe pública de qualquer JAR
 * (inclusive {@code sun.misc.*}) podia ser usada.</p>
 *
 * <p>Este exemplo roda no classpath (módulo sem nome) e inspeciona o sistema de módulos em
 * tempo de execução. O exemplo completo com dois módulos e {@code module-info.java} está em
 * {@code standalone/java09-modules/} (ver README.md lá).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/modules/ModuleSystemInspectionExample.java
 * java --list-modules          (lista os módulos do JDK)
 * java --describe-module java.sql
 * </pre>
 */
public class ModuleSystemInspectionExample {

    public static void main(String[] args) {
        System.out.println("=== Módulos carregados na camada de boot (ModuleLayer.boot()) ===");
        Set<String> moduleNames = new TreeSet<>();
        for (Module module : ModuleLayer.boot().modules()) {
            moduleNames.add(module.getName());
        }
        System.out.println("Total: " + moduleNames.size() + " módulos");
        System.out.println("Alguns: " + moduleNames.stream().limit(8).collect(Collectors.toList()) + " ...");

        System.out.println();
        System.out.println("=== Class.getModule() ===");
        printModuleOf(String.class);
        printModuleOf(java.sql.Connection.class);
        printModuleOf(java.util.logging.Logger.class);
        printModuleOf(ModuleSystemInspectionExample.class);

        System.out.println();
        System.out.println("=== Módulo sem nome (classpath) ===");
        Module myModule = ModuleSystemInspectionExample.class.getModule();
        System.out.println("isNamed(): " + myModule.isNamed());
        System.out.println("Módulo sem nome lê java.sql? " + myModule.canRead(java.sql.Connection.class.getModule()));
        System.out.println("(o módulo sem nome lê todos os módulos e exporta todos os seus pacotes)");

        System.out.println();
        System.out.println("=== Descriptor do módulo java.sql ===");
        Optional<Module> javaSql = ModuleLayer.boot().findModule("java.sql");
        javaSql.ifPresent(module -> {
            ModuleDescriptor descriptor = module.getDescriptor();
            System.out.println("Nome: " + descriptor.name());
            System.out.println("requires: " + descriptor.requires().stream()
                    .map(r -> r.name() + (r.modifiers().isEmpty() ? "" : " " + r.modifiers()))
                    .sorted().collect(Collectors.toList()));
            System.out.println("exports: " + descriptor.exports().stream()
                    .map(ModuleDescriptor.Exports::source).sorted().collect(Collectors.toList()));
            System.out.println("uses: " + descriptor.uses());
        });

        System.out.println();
        System.out.println("=== Encapsulamento forte: isExported ===");
        Module javaBase = Object.class.getModule();
        List<String> packages = List.of("java.lang", "java.util", "jdk.internal.misc", "sun.nio.ch");
        for (String pkg : packages) {
            System.out.printf("java.base exporta %-18s? %s%n", pkg, javaBase.isExported(pkg));
        }
        System.out.println("Pacotes internos (jdk.internal.*) não são acessíveis sem --add-exports/--add-opens.");
    }

    private static void printModuleOf(Class<?> type) {
        Module module = type.getModule();
        String name = module.isNamed() ? module.getName() : "<módulo sem nome>";
        System.out.printf("%-45s -> %s%n", type.getName(), name);
    }
}
