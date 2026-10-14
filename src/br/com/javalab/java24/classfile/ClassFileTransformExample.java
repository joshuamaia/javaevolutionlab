package br.com.javalab.java24.classfile;

import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.MethodModel;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Java 24 - Class-File API (JEP 484)
 *
 * <p>Status: Definitivo. Histórico: Preview no Java 22 (JEP 457) e segunda Preview no
 * Java 23 (JEP 466), com ajustes de API entre elas.</p>
 *
 * <p>O que é: API padrão {@code java.lang.classfile} para ler, gerar e TRANSFORMAR classes.
 * Uma transformação é declarativa: {@code ClassFile.transformClass(model, transform)}
 * percorre os elementos da classe original e, para cada um, o {@link ClassTransform}
 * decide se copia, descarta, altera ou acrescenta elementos.</p>
 * <p>Benefício: agentes e frameworks podem instrumentar/limpar classes com uma API do JDK,
 * sempre compatível com o formato de class file da versão atual.</p>
 * <p>Antes: ASM {@code ClassReader -> ClassVisitor -> ClassWriter}.</p>
 *
 * <p>Foco (o 22 gerou, o 23 leu): pegar uma classe {@code PaymentService} com um método
 * {@code debugDump()} que não deveria ir para produção, REMOVÊ-LO e ACRESCENTAR um método
 * {@code version()}, depois carregar as duas versões e comparar via reflexão.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java24/classfile/ClassFileTransformExample.java
 * </pre>
 */
public class ClassFileTransformExample {

    static final ClassDesc SERVICE = ClassDesc.of("generated.PaymentService");
    static final MethodTypeDesc NO_ARGS_STRING = MethodTypeDesc.of(ConstantDescs.CD_String);

    static class BytesClassLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] original = buildOriginal();

        System.out.println("=== Classe original ===");
        printMethods(new BytesClassLoader().define("generated.PaymentService", original));

        ClassFile classFile = ClassFile.of();
        ClassModel model = classFile.parse(original);

        // Transformação: descarta debugDump() e, ao final, acrescenta version()
        ClassTransform removeDebug = (classBuilder, element) -> {
            if (element instanceof MethodModel method && method.methodName().equalsString("debugDump")) {
                return; // não repassa o elemento -> método removido
            }
            classBuilder.with(element); // copia todo o resto como está
        };
        ClassTransform addVersion = ClassTransform.endHandler(classBuilder -> classBuilder
                .withMethodBody("version", NO_ARGS_STRING, ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code.ldc("2.0-prod").areturn()));
        byte[] transformed = classFile.transformClass(model, removeDebug.andThen(addVersion));

        System.out.println();
        System.out.println("=== Classe transformada ===");
        Class<?> newClass = new BytesClassLoader().define("generated.PaymentService", transformed);
        printMethods(newClass);
        System.out.println("pay() = " + newClass.getMethod("pay").invoke(null));
        System.out.println("version() = " + newClass.getMethod("version").invoke(null));
        System.out.println("Tamanho: " + original.length + " -> " + transformed.length + " bytes");

        System.out.println();
        System.out.println("=== Elementos da classe transformada (modelo) ===");
        for (ClassElement element : classFile.parse(transformed)) {
            if (element instanceof MethodModel m) System.out.println("  método " + m.methodName().stringValue());
        }
    }

    /** Gera a classe original: dois métodos estáticos pay() e debugDump(). */
    static byte[] buildOriginal() {
        return ClassFile.of().build(SERVICE, cb -> cb
                .withFlags(ClassFile.ACC_PUBLIC)
                .withMethodBody("pay", NO_ARGS_STRING, ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code.ldc("pagamento aprovado").areturn())
                .withMethodBody("debugDump", NO_ARGS_STRING, ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC,
                        code -> code.ldc("senha=123 (não deveria ir para produção!)").areturn()));
    }

    static void printMethods(Class<?> type) {
        System.out.println("Métodos de " + type.getName() + ": " + Arrays.stream(type.getDeclaredMethods())
                .map(Method::getName).sorted().toList());
    }
}
