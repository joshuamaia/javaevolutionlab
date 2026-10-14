package br.com.javalab.java06.compilerapi;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Java 6 - Java Compiler API (JSR 199, javax.tools)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: acesso programático ao compilador ({@code ToolProvider.getSystemJavaCompiler()}).
 * Os arquivos de entrada/saída são abstraídos por {@code JavaFileObject} e
 * {@code JavaFileManager}, então é possível compilar código que está só em memória e
 * gravar o bytecode também em memória. Erros chegam como {@code Diagnostic}s.</p>
 * <p>Benefício: geração dinâmica de código (regras de negócio, templates, JSP, IDEs,
 * ferramentas de build) sem chamar o processo {@code javac} nem usar a classe interna
 * {@code com.sun.tools.javac.Main}.</p>
 * <p>Requisito: o compilador só existe em um JDK (módulo {@code jdk.compiler}); num JRE
 * puro {@code getSystemJavaCompiler()} retorna null.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/compilerapi/CompilerApiExample.java
 * </pre>
 */
public class CompilerApiExample {

    /** Código-fonte guardado numa String. */
    static class SourceInMemory extends SimpleJavaFileObject {
        private final String code;

        SourceInMemory(String className, String code) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    /** Destino do bytecode: um array de bytes em memória. */
    static class ClassInMemory extends SimpleJavaFileObject {
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        ClassInMemory(String className) {
            super(URI.create("bytes:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        }

        @Override
        public OutputStream openOutputStream() {
            return bytes;
        }
    }

    /** FileManager que intercepta a gravação dos .class e guarda tudo em um mapa. */
    static class InMemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        final Map<String, ClassInMemory> classes = new HashMap<String, ClassInMemory>();

        InMemoryFileManager(StandardJavaFileManager delegate) {
            super(delegate);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className,
                                                   JavaFileObject.Kind kind, FileObject sibling) {
            ClassInMemory out = new ClassInMemory(className);
            classes.put(className, out);
            return out;
        }
    }

    /** ClassLoader que define classes a partir dos bytes compilados em memória. */
    static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, ClassInMemory> classes;

        InMemoryClassLoader(Map<String, ClassInMemory> classes, ClassLoader parent) {
            super(parent);
            this.classes = classes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            ClassInMemory compiled = classes.get(name);
            if (compiled == null) {
                return super.findClass(name);
            }
            byte[] b = compiled.bytes.toByteArray();
            return defineClass(name, b, 0, b.length);
        }
    }

    static boolean compile(JavaCompiler compiler, InMemoryFileManager fm, String className, String code) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler.CompilationTask task = compiler.getTask(null, fm, diagnostics, null, null,
                Arrays.asList(new SourceInMemory(className, code)));
        boolean ok = task.call();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            System.out.println("  [" + d.getKind() + "] linha " + d.getLineNumber() + ": " + d.getMessage(null));
        }
        return ok;
    }

    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Compilador indisponível: rode com um JDK (não um JRE).");
            return;
        }
        // Regra de desconto que poderia vir de um banco/arquivo de configuração
        String className = "generated.DiscountRule";
        String source = "package generated;\n"
                + "public class DiscountRule {\n"
                + "    public static double apply(double total) {\n"
                + "        return total > 500 ? total * 0.9 : total;\n"
                + "    }\n"
                + "}\n";

        System.out.println("=== Compilando código-fonte em memória ===");
        InMemoryFileManager fileManager =
                new InMemoryFileManager(compiler.getStandardFileManager(null, null, null));
        boolean ok = compile(compiler, fileManager, className, source);
        System.out.println("Compilou? " + ok + "; classes geradas: " + fileManager.classes.keySet()
                + " (" + fileManager.classes.get(className).bytes.size() + " bytes)");

        System.out.println();
        System.out.println("=== Carregando e executando a classe gerada ===");
        ClassLoader loader = new InMemoryClassLoader(fileManager.classes,
                CompilerApiExample.class.getClassLoader());
        Class<?> rule = loader.loadClass(className);
        Method apply = rule.getMethod("apply", double.class);
        for (double total : new double[] {200, 800}) {
            System.out.println("Total " + total + " -> com regra: " + apply.invoke(null, total));
        }

        System.out.println();
        System.out.println("=== Código com erro: diagnósticos estruturados ===");
        boolean broken = compile(compiler, fileManager, "generated.Broken",
                "package generated;\npublic class Broken {\n  int x = \"texto\";\n}\n");
        System.out.println("Compilou? " + broken);
        fileManager.close();
    }
}
