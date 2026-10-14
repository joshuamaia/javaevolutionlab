package br.com.javalab.java12.constants;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Java 12 - JVM Constants API (JEP 334)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que e: o pacote {@code java.lang.constant} modela de forma NOMINAL (por nome e
 * descritor, sem carregar classes) as constantes do constant pool: ClassDesc,
 * MethodTypeDesc, MethodHandleDesc etc. Tipos como String, Integer e Class passam a
 * implementar {@code Constable} / {@code ConstantDesc} ({@code describeConstable()}).</p>
 * <p>Beneficio: base para geradores de bytecode e ferramentas (e, no Java 24, para a
 * Class-File API) manipularem descritores sem strings magicas como "(I)Ljava/lang/String;".</p>
 * <p>Antes: montar descritores JVM a mao ou usar bibliotecas como ASM.</p>
 *
 * <p>Execucao (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java12/constants/JvmConstantsApiExample.java
 * </pre>
 */
public class JvmConstantsApiExample {

    public static void main(String[] args) throws ReflectiveOperationException {
        System.out.println("=== Abordagem antiga: descritor escrito a mao ===");
        String handWritten = "(Ljava/lang/String;I)Ljava/lang/String;";
        System.out.println(handWritten);

        System.out.println("=== Java 12: descritores nominais ===");
        ClassDesc orderDesc = ClassDesc.of("br.com.shop", "Order");
        System.out.println("ClassDesc Order: " + orderDesc.descriptorString()
                + " (displayName=" + orderDesc.displayName() + ", sem carregar a classe)");
        ClassDesc orderArray = orderDesc.arrayType();
        System.out.println("Array de Order: " + orderArray.descriptorString());

        MethodTypeDesc repeat = MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_int);
        System.out.println("String.repeat(int): " + repeat.descriptorString());

        MethodTypeDesc substring = MethodTypeDesc.ofDescriptor(handWritten);
        System.out.println("Parametros do descritor manual: " + substring.parameterList());

        System.out.println("=== describeConstable() e resolucao ===");
        System.out.println("String.class -> " + String.class.describeConstable().orElseThrow());
        System.out.println("\"pedido\"     -> " + "pedido".describeConstable().orElseThrow());
        System.out.println("42 (Integer) -> " + Integer.valueOf(42).describeConstable().orElseThrow());

        ConstantDesc desc = ConstantDescs.CD_String;
        Object resolved = desc.resolveConstantDesc(MethodHandles.lookup());
        System.out.println("CD_String resolvido: " + resolved);

        MethodType mt = (MethodType) repeat.resolveConstantDesc(MethodHandles.lookup());
        System.out.println("MethodTypeDesc resolvido: " + mt);
    }
}
