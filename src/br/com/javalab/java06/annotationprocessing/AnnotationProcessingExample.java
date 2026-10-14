package br.com.javalab.java06.annotationprocessing;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Java 6 - Pluggable Annotation Processing API (JSR 269)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: processadores de anotação que rodam DENTRO do javac
 * ({@code javax.annotation.processing.Processor}), em rodadas. Eles inspecionam o código
 * pelo modelo {@code javax.lang.model} (elementos e tipos, sem carregar classes), emitem
 * erros/avisos via {@code Messager} e geram novos arquivos via {@code Filer}, que são
 * compilados na rodada seguinte.</p>
 * <p>Benefício: geração de código e validações em tempo de compilação (Lombok, MapStruct,
 * Dagger, JPA metamodel, AutoValue, Immutables...).</p>
 * <p>Antes: a ferramenta separada {@code apt} do Java 5 (API {@code com.sun.mirror},
 * não padronizada; removida no Java 8).</p>
 * <p>O exemplo compila em memória (Compiler API, JSR 199) uma classe anotada com
 * {@code @Entity}; o processador avisa se a entidade não tem campo {@code id} e gera uma
 * classe {@code <Nome>Fields} com constantes dos nomes dos campos.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/annotationprocessing/AnnotationProcessingExample.java
 * </pre>
 */
public class AnnotationProcessingExample {

    /** O processador. Na vida real ficaria num JAR com META-INF/services/javax.annotation.processing.Processor. */
    static class EntityProcessor extends AbstractProcessor {

        @Override
        public Set<String> getSupportedAnnotationTypes() {
            return new HashSet<String>(Arrays.asList("model.Entity"));
        }

        @Override
        public SourceVersion getSupportedSourceVersion() {
            return SourceVersion.latestSupported(); // evita aviso em JDKs mais novos
        }

        @Override
        public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment round) {
            for (TypeElement annotation : annotations) {
                for (Element entity : round.getElementsAnnotatedWith(annotation)) {
                    generateFieldsClass((TypeElement) entity);
                }
            }
            return true; // anotação "consumida" por este processador
        }

        private void generateFieldsClass(TypeElement entity) {
            String name = entity.getSimpleName() + "Fields";
            StringBuilder code = new StringBuilder("package model;\npublic final class " + name + " {\n");
            boolean hasId = false;
            for (Element member : entity.getEnclosedElements()) {
                if (member.getKind() == ElementKind.FIELD) {
                    String field = member.getSimpleName().toString();
                    hasId |= field.equals("id");
                    code.append("    public static final String ").append(field.toUpperCase())
                            .append(" = \"").append(field).append("\";\n");
                }
            }
            code.append("}\n");
            if (!hasId) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Entidade sem campo 'id'", entity);
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Gerando model." + name);
            try {
                JavaFileObject file = processingEnv.getFiler().createSourceFile("model." + name, entity);
                Writer w = file.openWriter();
                try {
                    w.write(code.toString());
                } finally {
                    w.close();
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }
    }

    static JavaFileObject source(String path, final String code) {
        return new SimpleJavaFileObject(URI.create("string:///" + path), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return code;
            }
        };
    }

    static void deleteRecursively(File f) {
        File[] children = f.listFiles();
        if (children != null) {
            for (File c : children) {
                deleteRecursively(c);
            }
        }
        f.delete();
    }

    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Compilador indisponível: rode com um JDK.");
            return;
        }
        File out = File.createTempFile("javalab-apt", ""); // java.nio.file só no Java 7
        out.delete();
        out.mkdirs();
        try {
            JavaFileObject entityAnnotation = source("model/Entity.java",
                    "package model;\npublic @interface Entity {}\n");
            JavaFileObject customer = source("model/Customer.java",
                    "package model;\n@Entity\npublic class Customer {\n"
                            + "    private String name;\n    private String email;\n}\n");

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);
            JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diagnostics,
                    Arrays.asList("-d", out.getPath(), "-s", out.getPath()), null,
                    Arrays.asList(entityAnnotation, customer));
            task.setProcessors(Arrays.asList(new EntityProcessor())); // processador explícito

            System.out.println("=== Compilando com o processador de anotações ===");
            boolean ok = task.call();
            fm.close();
            for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
                System.out.println("[" + d.getKind() + "] " + d.getMessage(null));
            }
            System.out.println("Compilou? " + ok);

            System.out.println();
            System.out.println("=== Código gerado pelo Filer ===");
            File generated = new File(out, "model/CustomerFields.java");
            java.util.Scanner scanner = new java.util.Scanner(generated, "UTF-8");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
            scanner.close();
            System.out.println(".class gerado também? " + new File(out, "model/CustomerFields.class").exists());
        } finally {
            deleteRecursively(out);
        }
    }
}
