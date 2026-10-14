package br.com.javalab.java15.hiddenclasses;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Java 15 - Hidden Classes (JEP 371)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: {@code MethodHandles.Lookup.defineHiddenClass(bytes, ...)} define uma
 * classe a partir de bytecode gerado em tempo de execucao que:</p>
 * <ul>
 *   <li>nao pode ser encontrada por nome (Class.forName, outro bytecode, ClassLoader);</li>
 *   <li>so e acessivel pelo objeto Class/Lookup devolvido;</li>
 *   <li>pode ser descarregada assim que nao houver mais referencias, independentemente
 *       do ClassLoader (a menos que se use a opcao STRONG).</li>
 * </ul>
 * <p>Beneficio: base para frameworks e para o proprio JDK (lambdas, proxies, string
 * concat) gerarem classes de forma segura e descartavel.</p>
 * <p>Antes: {@code sun.misc.Unsafe.defineAnonymousClass} (API interna, removida no 17)
 * ou ClassLoaders customizados que so liberam classes junto com o loader inteiro.</p>
 *
 * <p>Os bytes da classe sao obtidos compilando codigo-fonte em memoria com
 * {@code javax.tools} (funciona compilado e via source launcher). Se o compilador
 * nao estiver disponivel, usa como alternativa o .class da classe aninhada
 * {@code Template}, lido como recurso.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java15/hiddenclasses/HiddenClassExample.java
 * </pre>
 */
public class HiddenClassExample {

    private static final String CLASS_NAME = "br.com.javalab.java15.hiddenclasses.BlackFridayDiscount";

    private static final String SOURCE = String.join("\n",
            "package br.com.javalab.java15.hiddenclasses;",
            "public class BlackFridayDiscount implements java.util.function.DoubleUnaryOperator {",
            "    public double applyAsDouble(double price) { return price * 0.7; }",
            "}");

    /** Alternativa: classe comum cujo bytecode pode ser lido como recurso. */
    public static class Template implements DoubleUnaryOperator {
        public double applyAsDouble(double price) {
            return price * 0.9;
        }
    }

    public static void main(String[] args) throws Throwable {
        byte[] bytes = compileInMemory();
        if (bytes == null) {
            System.out.println("(javax.tools indisponivel: usando bytes de Template.class)");
            bytes = readTemplateBytes();
        }
        System.out.println("=== Bytecode obtido: " + bytes.length + " bytes ===");

        MethodHandles.Lookup lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
        Class<?> hidden = lookup.lookupClass();
        System.out.println("Nome da classe oculta: " + hidden.getName());
        System.out.println("isHidden()? " + hidden.isHidden());

        DoubleUnaryOperator discount = (DoubleUnaryOperator) lookup
                .findConstructor(hidden, MethodType.methodType(void.class)).invoke();
        System.out.println("Preco com desconto (4500.0): " + discount.applyAsDouble(4500.0));

        System.out.println("=== Nao e encontravel por nome ===");
        for (String name : new String[]{hidden.getName(), CLASS_NAME}) {
            try {
                Class.forName(name);
                System.out.println("Encontrada: " + name);
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException: " + name);
            }
        }

        System.out.println("=== Pode ser descarregada ===");
        WeakReference<Class<?>> ref = new WeakReference<>(hidden);
        hidden = null;
        discount = null;
        lookup = null;
        for (int i = 0; i < 10 && ref.get() != null; i++) {
            System.gc();
            Thread.sleep(100);
        }
        System.out.println(ref.get() == null
                ? "Classe oculta descarregada pelo GC (sem referencias)"
                : "Ainda nao descarregada (o GC decide quando; tente -Xlog:class+unload)");
    }

    /** Compila SOURCE em memoria e devolve os bytes do .class, ou null sem compilador. */
    private static byte[] compileInMemory() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return null;
        }
        JavaFileObject source = new SimpleJavaFileObject(
                URI.create("string:///BlackFridayDiscount.java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return SOURCE;
            }
        };
        ByteArrayOutputStream classBytes = new ByteArrayOutputStream();
        try (JavaFileManager fileManager = new ForwardingJavaFileManager<>(
                compiler.getStandardFileManager(null, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                       JavaFileObject.Kind kind, FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("mem:///" + className + ".class"), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        return classBytes;
                    }
                };
            }
        }) {
            boolean ok = compiler.getTask(null, fileManager, null, null, null, List.of(source)).call();
            return ok ? classBytes.toByteArray() : null;
        }
    }

    private static byte[] readTemplateBytes() throws IOException {
        try (InputStream in = HiddenClassExample.class.getResourceAsStream("HiddenClassExample$Template.class")) {
            if (in == null) {
                throw new IllegalStateException("Template.class nao encontrado como recurso");
            }
            return in.readAllBytes();
        }
    }
}
