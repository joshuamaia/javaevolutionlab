package br.com.javalab.java22.classfile;

import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Method;

/**
 * Java 22 - Class-File API (JEP 457)
 *
 * <p>Status: Preview. Evolução: segunda Preview no Java 23 (JEP 466) e definitivo no
 * Java 24 (JEP 484). ATENÇÃO: a API mudou entre as Previews (no 22 o pacote já era
 * {@code java.lang.classfile}, mas vários métodos/classes foram renomeados ou removidos no 23
 * e no 24, ex.: {@code ClassFile.Option}s, {@code CodeBuilder}s e o modelo de atributos).
 * Este exemplo usa a API final (JDK 24/25) para gerar uma classe - a ideia é a mesma da
 * Preview do 22, mas o código original da época não é garantido de compilar hoje.</p>
 *
 * <p>O que é: API padrão para ler, gerar e transformar arquivos {@code .class}, que
 * acompanha sempre o formato da versão atual da JVM.</p>
 * <p>Benefício: frameworks (proxies, mocks, agentes) e o próprio JDK deixam de depender
 * de bibliotecas como ASM (que precisam ser atualizadas a cada nova versão do class file)
 * e da cópia interna do ASM que o JDK carregava.</p>
 * <p>Antes: ASM, BCEL, Javassist ou ByteBuddy (bibliotecas externas).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java22/classfile/ClassFileGenerateExample.java
 * </pre>
 */
public class ClassFileGenerateExample {

    /** ClassLoader mínimo que define uma classe a partir de bytes em memória. */
    static class BytesClassLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {
        ClassDesc greeterDesc = ClassDesc.of("generated.Greeter");
        MethodTypeDesc stringToString = MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_String);

        System.out.println("=== Gerando a classe generated.Greeter em memória ===");
        // Equivalente em Java:
        // public class Greeter {
        //     public static String greet(String name) { return "Olá, ".concat(name); }
        // }
        byte[] bytes = ClassFile.of().build(greeterDesc, classBuilder -> classBuilder
                .withFlags(ClassFile.ACC_PUBLIC)
                .withMethodBody("greet", stringToString, ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code
                                .ldc("Olá, ")                       // empilha a constante
                                .aload(0)                            // empilha o parâmetro name
                                .invokevirtual(ConstantDescs.CD_String, "concat", stringToString)
                                .areturn()));
        System.out.println("Bytes gerados: " + bytes.length + " (magic = 0x"
                + Integer.toHexString(((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF)).toUpperCase() + ")");

        System.out.println();
        System.out.println("=== Carregando e chamando via reflexão ===");
        Class<?> greeterClass = new BytesClassLoader().define("generated.Greeter", bytes);
        Method greet = greeterClass.getMethod("greet", String.class);
        System.out.println("Classe carregada: " + greeterClass.getName());
        System.out.println("Resultado: " + greet.invoke(null, "Maria"));
    }
}
