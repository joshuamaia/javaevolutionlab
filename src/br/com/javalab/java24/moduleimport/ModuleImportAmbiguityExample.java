package br.com.javalab.java24.moduleimport;

import module java.base;  // java.util, java.time, ...
import module java.sql;   // java.sql + (transitivos) java.xml, java.logging, java.transaction.xa
import java.sql.*;        // Java 24: import sob demanda passa a SOMBREAR imports de módulo

/**
 * Java 24 - Module Import Declarations, segunda Preview (JEP 494)
 *
 * <p>Status: Segunda Preview. Histórico: Preview no Java 23 (JEP 476); definitivo no
 * Java 25 (JEP 511), igual a esta versão. Compila no JDK 25 sem flags.</p>
 *
 * <p>O que mudou no 24 (o exemplo do 23 usou só {@code java.base}):</p>
 * <ul>
 *   <li>Importar um módulo traz também o que ele exige de forma TRANSITIVA:
 *       {@code import module java.sql;} disponibiliza {@code java.xml} e {@code java.logging}.</li>
 *   <li>Declarações {@code import pacote.*;} (sob demanda) passam a ter precedência sobre
 *       {@code import module} - antes só o import de tipo único ({@code import java.sql.Date;})
 *       tinha. Isso resolve ambiguidades de nome simples.</li>
 *   <li>O módulo agregador {@code java.se} passou a exigir {@code java.base} de forma
 *       transitiva: {@code import module java.se;} importa TODA a API do Java SE. Porém
 *       {@code java.se} não exporta pacotes e por isso não é módulo raiz do classpath:
 *       exige {@code --add-modules java.se} para compilar/rodar (por isso não é usado aqui).</li>
 * </ul>
 * <p>Ambiguidade: {@code Date} existe em {@code java.util} (via java.base) e em {@code java.sql}.
 * Sem o {@code import java.sql.*;}, usar {@code Date} seria erro "reference to Date is
 * ambiguous". O mesmo aconteceria com {@code Document}/{@code Element} se importássemos
 * também {@code java.desktop} ({@code javax.swing.text}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/moduleimport/ModuleImportAmbiguityExample.java
 * </pre>
 */
public class ModuleImportAmbiguityExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Ambiguidade resolvida: Date -> java.sql.Date ===");
        Date invoiceDate = Date.valueOf(LocalDate.of(2025, 3, 18)); // LocalDate vem de java.base
        System.out.println(invoiceDate.getClass().getName() + " = " + invoiceDate);
        java.util.Date utilDate = new java.util.Date(invoiceDate.getTime()); // o outro: qualificado
        System.out.println("java.util.Date ainda acessível qualificado: " + (utilDate.getTime() == invoiceDate.getTime()));

        System.out.println();
        System.out.println("=== Módulos transitivos de java.sql ===");
        // java.logging
        Logger logger = Logger.getLogger("pedidos");
        logger.setLevel(Level.WARNING);
        System.out.println("Logger '" + logger.getName() + "' nível " + logger.getLevel());

        // java.xml
        Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element order = xml.createElement("pedido");
        order.setAttribute("id", "42");
        xml.appendChild(order);
        System.out.println("XML com raiz <" + xml.getDocumentElement().getTagName()
                + " id=\"" + order.getAttribute("id") + "\">");

        Set<String> transitive = ModuleLayer.boot().findModule("java.sql").orElseThrow()
                .getDescriptor().requires().stream()
                .filter(r -> r.modifiers().contains(ModuleDescriptor.Requires.Modifier.TRANSITIVE))
                .map(ModuleDescriptor.Requires::name)
                .collect(Collectors.toCollection(TreeSet::new));
        System.out.println("java.sql 'requires transitive': " + transitive);
    }
}
