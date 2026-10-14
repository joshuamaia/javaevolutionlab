package br.com.javalab.java08.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 8 - Type Annotations (JEP 104 / JSR 308)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: anotações podem ser aplicadas a qualquer USO de tipo (argumentos genéricos,
 * casts, {@code new}, {@code throws}, {@code implements}), com os novos alvos
 * {@code ElementType.TYPE_USE} e {@code TYPE_PARAMETER}.</p>
 * <p>Benefício: permite verificadores plugáveis (ex.: Checker Framework detectando
 * NullPointerException em tempo de compilação com {@code @NonNull}).</p>
 * <p>Antes: anotações só em declarações (classes, métodos, campos, parâmetros).</p>
 * <p>Obs.: o javac sozinho não valida a semântica; ele apenas registra as anotações.
 * Por reflexão elas são lidas via {@link AnnotatedType}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/annotations/TypeAnnotationsExample.java
 * </pre>
 */
public class TypeAnnotationsExample {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface NonNull {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface Email {
    }

    /** Anotações em argumento de tipo genérico e no tipo de retorno. */
    public static @NonNull List<@Email String> recipients() {
        List<@Email String> list = new ArrayList<>();
        list.add("alice@example.com");
        return list;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Usos possíveis de type annotations (compilam desde o Java 8) ===");
        Object raw = "pedido-123";
        String id = (@NonNull String) raw;                       // em cast
        List<@NonNull String> ids = new ArrayList<@NonNull String>(); // em argumento genérico e new
        ids.add(id);
        System.out.println("ids = " + ids);

        System.out.println();
        System.out.println("=== Lendo por reflexão (AnnotatedType) ===");
        Method method = TypeAnnotationsExample.class.getMethod("recipients");
        AnnotatedType returnType = method.getAnnotatedReturnType();
        System.out.println("Retorno anotado com @NonNull? " + returnType.isAnnotationPresent(NonNull.class));
        AnnotatedType typeArg = ((AnnotatedParameterizedType) returnType).getAnnotatedActualTypeArguments()[0];
        System.out.println("Argumento de tipo: " + typeArg.getType().getTypeName()
                + " | @Email? " + typeArg.isAnnotationPresent(Email.class));
        System.out.println("Destinatários: " + recipients());
    }
}
