package br.com.javalab.java10.versioning;

import java.util.List;

/**
 * Java 10 - Versionamento baseado em tempo (JEP 322)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: a partir do Java 10 o JDK tem uma versão <i>feature</i> a cada seis meses
 * (março e setembro). O formato é {@code $FEATURE.$INTERIM.$UPDATE.$PATCH}, por exemplo
 * {@code 11.0.2}. {@code Runtime.Version} ganhou {@code feature()}, {@code interim()},
 * {@code update()} e {@code patch()}; as propriedades {@code java.version.date} e
 * {@code java.vendor.version} foram adicionadas.</p>
 * <p>Benefício: lançamentos previsíveis e números que dizem algo (feature = "release").</p>
 * <p>Antes: {@code 1.8.0_181} (Java 8) e o formato do JEP 223 no Java 9
 * ({@code $MAJOR.$MINOR.$SECURITY}, com {@code major()}/{@code minor()}/{@code security()},
 * hoje deprecated).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java10/versioning/TimeBasedReleaseVersioningExample.java
 * java -version
 * </pre>
 */
public class TimeBasedReleaseVersioningExample {

    public static void main(String[] args) {
        System.out.println("=== Versão do runtime atual ===");
        Runtime.Version current = Runtime.version();
        print(current);
        System.out.println("java.version          = " + System.getProperty("java.version"));
        System.out.println("java.version.date     = " + System.getProperty("java.version.date"));
        System.out.println("java.vendor.version   = " + System.getProperty("java.vendor.version"));
        System.out.println("java.specification.version = " + System.getProperty("java.specification.version"));

        System.out.println();
        System.out.println("=== Interpretando strings de versão ===");
        for (String text : List.of("10.0.2+13", "11.0.21+9-LTS", "17.0.12", "21.0.4+7-LTS", "25-ea+30")) {
            print(Runtime.Version.parse(text));
        }

        System.out.println();
        System.out.println("=== Comparando versões ===");
        Runtime.Version minimum = Runtime.Version.parse("11");
        System.out.println("Runtime atual >= 11? " + (current.compareTo(minimum) >= 0));
        System.out.println("Recurso disponível? " + (current.feature() >= 10 ? "sim, var existe" : "não"));

        System.out.println();
        System.out.println("=== Antes x depois ===");
        System.out.println("Java 8 : 1.8.0_181       (1.MAJOR.0_UPDATE)");
        System.out.println("Java 9 : 9.0.4           (MAJOR.MINOR.SECURITY - JEP 223)");
        System.out.println("Java 10+: 10.0.2, 11.0.21 (FEATURE.INTERIM.UPDATE.PATCH - JEP 322)");
    }

    private static void print(Runtime.Version version) {
        System.out.printf("%-16s feature=%-3d interim=%d update=%-3d patch=%d pre=%-5s build=%-5s optional=%s%n",
                version, version.feature(), version.interim(), version.update(), version.patch(),
                version.pre().orElse("-"), version.build().map(String::valueOf).orElse("-"),
                version.optional().orElse("-"));
    }
}
