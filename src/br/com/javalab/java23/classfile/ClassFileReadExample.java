package br.com.javalab.java23.classfile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.CodeModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.reflect.AccessFlag;
import java.util.Map;
import java.util.TreeMap;

/**
 * Java 23 - Class-File API, segunda Preview (JEP 466)
 *
 * <p>Status: Segunda Preview. Histórico: Preview no Java 22 (JEP 457); definitivo no Java 24
 * (JEP 484). A segunda Preview refinou a API (ex.: simplificou {@code CodeBuilder},
 * reorganizou {@code ClassSignature}/atributos e o tratamento de {@code ClassFile.Option}s).
 * Este exemplo usa a API final do JDK 24/25 e compila no JDK 25 sem flags.</p>
 *
 * <p>O que é: a mesma API serve para LER classes: {@code ClassFile.of().parse(bytes)} devolve
 * um {@link ClassModel} imutável e navegável (campos, métodos, atributos, instruções).
 * O exemplo do Java 22 gerou uma classe; aqui analisamos uma existente.</p>
 * <p>Benefício: analisadores (linters, ferramentas de segurança, agentes) sem ASM.</p>
 * <p>Antes: {@code javap} (ferramenta externa) ou ASM {@code ClassReader} + visitors.</p>
 *
 * <p>Fonte dos bytes: {@code String.class.getResourceAsStream("String.class")} - funciona
 * tanto compilado quanto pelo launcher de código-fonte (arquivos {@code .class} de módulos
 * nomeados não são encapsulados).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java23/classfile/ClassFileReadExample.java
 * </pre>
 */
public class ClassFileReadExample {

    public static void main(String[] args) throws IOException {
        byte[] bytes = readClassBytes(String.class);
        ClassModel model = ClassFile.of().parse(bytes);

        System.out.println("=== Informações gerais de java.lang.String ===");
        System.out.println("Nome: " + model.thisClass().asInternalName());
        System.out.println("Superclasse: " + model.superclass().map(c -> c.asInternalName()).orElse("-"));
        System.out.println("Versão do class file: " + model.majorVersion());
        System.out.println("Campos: " + model.fields().size() + " | Métodos: " + model.methods().size());

        System.out.println();
        System.out.println("=== Primeiros métodos públicos começando com 'to' ===");
        model.methods().stream()
                .filter(m -> m.flags().has(AccessFlag.PUBLIC))
                .filter(m -> m.methodName().stringValue().startsWith("to"))
                .limit(6)
                .forEach(m -> System.out.println("  " + m.methodName().stringValue()
                        + m.methodTypeSymbol().displayDescriptor()));

        System.out.println();
        System.out.println("=== Métodos com mais instruções (análise do bytecode) ===");
        Map<Integer, String> sizes = new TreeMap<>((a, b) -> b - a);
        for (MethodModel method : model.methods()) {
            method.code().ifPresent(code -> sizes.merge(countInstructions(code),
                    method.methodName().stringValue(), (x, y) -> x));
        }
        sizes.entrySet().stream().limit(3)
                .forEach(e -> System.out.println("  " + e.getValue() + ": " + e.getKey() + " instruções"));

        System.out.println();
        System.out.println("=== Quem 'equals' chama? ===");
        model.methods().stream()
                .filter(m -> m.methodName().equalsString("equals"))
                .flatMap(m -> m.code().stream())
                .flatMap(code -> code.elementStream())
                .filter(e -> e instanceof InvokeInstruction)
                .map(e -> (InvokeInstruction) e)
                .forEach(i -> System.out.println("  " + i.owner().asInternalName() + "." + i.name().stringValue()));
    }

    static int countInstructions(CodeModel code) {
        return (int) code.elementStream().filter(e -> e instanceof java.lang.classfile.Instruction).count();
    }

    static byte[] readClassBytes(Class<?> type) throws IOException {
        try (InputStream in = type.getResourceAsStream(type.getSimpleName() + ".class")) {
            if (in == null) throw new IOException("bytes de " + type + " não encontrados");
            return in.readAllBytes();
        }
    }
}
