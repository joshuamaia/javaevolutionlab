package br.com.javalab.java09.stackwalker;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Java 9 - Stack-Walking API: StackWalker (JEP 259)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: {@code StackWalker} percorre a pilha de chamadas da thread atual de forma
 * <b>preguiçosa</b> via {@code Stream<StackFrame>}, com filtros e acesso à {@code Class}
 * de cada frame.</p>
 * <p>Benefício: muito mais barato que capturar a pilha inteira; permite descobrir quem
 * chamou o método ({@code getCallerClass()}) sem APIs internas.</p>
 * <p>Antes: {@code new Throwable().getStackTrace()} (captura tudo, caro, só Strings) ou
 * {@code sun.reflect.Reflection.getCallerClass()} (API interna).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java09/stackwalker/StackWalkerExample.java
 * </pre>
 */
public class StackWalkerExample {

    static class OrderService {
        void placeOrder() {
            new PaymentService().charge();
        }
    }

    static class PaymentService {
        void charge() {
            AuditLogger.log("pagamento aprovado");
        }
    }

    static class AuditLogger {
        private static final StackWalker WALKER =
                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        static void log(String message) {
            System.out.println("=== Abordagem antiga: Throwable.getStackTrace() ===");
            StackTraceElement[] trace = new Throwable().getStackTrace();
            System.out.println("Chamador: " + trace[1].getClassName() + "." + trace[1].getMethodName()
                    + " (pilha inteira capturada: " + trace.length + " frames)");

            System.out.println();
            System.out.println("=== Java 9: getCallerClass() ===");
            Class<?> caller = WALKER.getCallerClass();
            System.out.println("[" + caller.getSimpleName() + "] " + message);

            System.out.println();
            System.out.println("=== Java 9: walk() preguiçoso (só os 3 primeiros frames) ===");
            List<String> frames = WALKER.walk(stream -> stream
                    .limit(3)
                    .map(f -> f.getClassName() + "." + f.getMethodName() + ":" + f.getLineNumber())
                    .collect(Collectors.toList()));
            frames.forEach(f -> System.out.println("  " + f));

            System.out.println();
            System.out.println("=== Filtrando: frames de classes *Service ===");
            WALKER.walk(stream -> stream
                    .filter(f -> f.getDeclaringClass().getSimpleName().endsWith("Service"))
                    .map(f -> f.getDeclaringClass().getSimpleName() + "." + f.getMethodName())
                    .collect(Collectors.toList()))
                    .forEach(f -> System.out.println("  " + f));
        }
    }

    public static void main(String[] args) {
        new OrderService().placeOrder();
    }
}
