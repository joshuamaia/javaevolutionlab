package br.com.javalab.java08.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Java 8 - Repeating Annotations (JEP 120)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: uma anotação marcada com {@link Repeatable} pode ser aplicada várias vezes
 * no mesmo elemento. O compilador as guarda automaticamente numa anotação "contêiner".</p>
 * <p>Benefício: declarações naturais como várias {@code @Schedule} ou {@code @Role}.</p>
 * <p>Antes: era obrigatório escrever o contêiner à mão: {@code @Schedules({@Schedule(..), @Schedule(..)})}.</p>
 * <p>Reflexão: {@code getAnnotationsByType(Schedule.class)} retorna todas; {@code getAnnotation(Schedule.class)}
 * retorna null quando há mais de uma (elas estão dentro do contêiner).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/annotations/RepeatingAnnotationsExample.java
 * </pre>
 */
public class RepeatingAnnotationsExample {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(Schedules.class)
    @interface Schedule {
        String dayOfWeek();

        String hour();
    }

    /** Anotação contêiner exigida por @Repeatable. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Schedules {
        Schedule[] value();
    }

    static class ReportJobs {
        // Java 8: repetição direta
        @Schedule(dayOfWeek = "MONDAY", hour = "08:00")
        @Schedule(dayOfWeek = "FRIDAY", hour = "18:00")
        public void salesReport() {
        }

        // Antes do Java 8: contêiner explícito (ainda válido)
        @Schedules({
                @Schedule(dayOfWeek = "SUNDAY", hour = "23:00")
        })
        public void backup() {
        }

        @Schedule(dayOfWeek = "WEDNESDAY", hour = "12:00")
        public void stockReport() {
        }
    }

    public static void main(String[] args) throws Exception {
        for (String name : new String[] {"salesReport", "backup", "stockReport"}) {
            Method method = ReportJobs.class.getMethod(name);
            System.out.println("=== " + name + " ===");
            for (Schedule s : method.getAnnotationsByType(Schedule.class)) {
                System.out.println("  agendado: " + s.dayOfWeek() + " às " + s.hour());
            }
            System.out.println("  getAnnotation(Schedule):  " + (method.getAnnotation(Schedule.class) != null));
            System.out.println("  getAnnotation(Schedules): " + (method.getAnnotation(Schedules.class) != null));
        }
        System.out.println();
        System.out.println("Obs.: com 2+ repetições, o compilador gera @Schedules automaticamente.");
    }
}
