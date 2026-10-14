package br.com.javalab.java22.ffm;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

/**
 * Java 22 - Foreign Function &amp; Memory API (JEP 454)
 *
 * <p>Status: Definitivo. Histórico: incubadora nos Java 14-16 (jdk.incubator.foreign,
 * JEPs 370, 383, 393, 389), Java 17-18 (JEPs 412, 419); Preview nos Java 19, 20 e 21
 * (JEPs 424, 434, 442).</p>
 *
 * <p>O que é: API padrão ({@code java.lang.foreign}) para (1) alocar e acessar memória
 * fora do heap com segurança ({@link Arena}, {@link MemorySegment}, {@link ValueLayout})
 * e (2) chamar funções nativas (downcall) e deixar código nativo chamar Java (upcall)
 * via {@link Linker}, sem escrever uma linha de C.</p>
 * <p>Benefício: substitui JNI (que exige código C "cola", headers gerados por {@code javac -h},
 * compilação por plataforma) e {@code ByteBuffer.allocateDirect} (limitado a 2 GB, índice int,
 * liberação não determinística via GC) e {@code sun.misc.Unsafe} (sem verificação de limites).
 * Com FFM a memória tem limites verificados, ciclo de vida explícito (a Arena fecha e libera
 * tudo) e acesso confinado à thread dona.</p>
 * <p>Antes: JNI = declarar {@code native int strlen(String s);}, gerar header, escrever
 * {@code Java_Pacote_Classe_strlen(JNIEnv*, ...)} em C, compilar uma DLL/.so e
 * {@code System.loadLibrary}. Memória off-heap = {@code ByteBuffer.allocateDirect}.</p>
 *
 * <p>Execução (JDK 25) - {@code --enable-native-access} evita o aviso de "restricted method":</p>
 * <pre>
 * java --enable-native-access=ALL-UNNAMED src/br/com/javalab/java22/ffm/ForeignFunctionMemoryExample.java
 * </pre>
 * <p>Testado no Windows: {@code strlen} e {@code qsort} vêm da C runtime (ucrtbase/msvcrt)
 * expostas por {@code Linker.nativeLinker().defaultLookup()}; no Linux/macOS vêm da libc.</p>
 */
public class ForeignFunctionMemoryExample {

    public static void main(String[] args) throws Throwable {
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();

        System.out.println("=== Memória off-heap com Arena e MemorySegment ===");
        try (Arena arena = Arena.ofConfined()) {
            // 5 preços (double) fora do heap; o segmento conhece seu tamanho -> acesso verificado
            MemorySegment prices = arena.allocate(ValueLayout.JAVA_DOUBLE, 5);
            double[] values = {19.90, 5.50, 120.00, 42.10, 7.25};
            for (int i = 0; i < values.length; i++) {
                prices.setAtIndex(ValueLayout.JAVA_DOUBLE, i, values[i]);
            }
            double total = 0;
            for (long i = 0; i < prices.byteSize() / Double.BYTES; i++) {
                total += prices.getAtIndex(ValueLayout.JAVA_DOUBLE, i);
            }
            System.out.printf("Segmento de %d bytes, total dos preços = %.2f%n", prices.byteSize(), total);
            try {
                prices.getAtIndex(ValueLayout.JAVA_DOUBLE, 5); // fora dos limites
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Acesso fora dos limites bloqueado: " + e.getClass().getSimpleName());
            }
        } // a Arena fecha aqui: memória liberada de forma determinística

        System.out.println();
        System.out.println("=== Downcall: chamando strlen da biblioteca C ===");
        MethodHandle strlen = linker.downcallHandle(
                stdlib.find("strlen").orElseThrow(),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));
        try (Arena arena = Arena.ofConfined()) {
            String productName = "Teclado mecânico";
            MemorySegment cString = arena.allocateFrom(productName); // UTF-8 terminado em '\0'
            long length = (long) strlen.invokeExact(cString);
            System.out.println("Texto: " + productName);
            System.out.println("strlen (bytes UTF-8) = " + length + " | String.length() = " + productName.length());
        }

        System.out.println();
        System.out.println("=== Upcall: qsort nativo chamando um comparador Java ===");
        MethodHandle qsort = linker.downcallHandle(
                stdlib.find("qsort").orElseThrow(),
                FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG,
                        ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));
        MethodHandle compareJava = MethodHandles.lookup().findStatic(ForeignFunctionMemoryExample.class,
                "compareInts", MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class));
        // ponteiros recebidos pelo comparador apontam para um int
        ValueLayout.OfInt intLayout = ValueLayout.JAVA_INT;
        FunctionDescriptor comparatorDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS.withTargetLayout(intLayout),
                ValueLayout.ADDRESS.withTargetLayout(intLayout));
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment comparator = linker.upcallStub(compareJava, comparatorDesc, arena);
            int[] stock = {42, 7, 19, 3, 88, 15};
            MemorySegment nativeArray = arena.allocateFrom(ValueLayout.JAVA_INT, stock);
            System.out.println("Estoques antes:  " + Arrays.toString(stock));
            qsort.invokeExact(nativeArray, (long) stock.length, intLayout.byteSize(), comparator);
            System.out.println("Estoques depois: " + Arrays.toString(nativeArray.toArray(ValueLayout.JAVA_INT)));
        }

        System.out.println();
        System.out.println("=== Layout estruturado (como uma struct C) ===");
        MemoryLayout pointLayout = MemoryLayout.structLayout(
                ValueLayout.JAVA_INT.withName("x"), ValueLayout.JAVA_INT.withName("y"));
        System.out.println("struct Point { int x; int y; } -> " + pointLayout.byteSize() + " bytes: " + pointLayout);
    }

    /** Comparador chamado pelo código nativo (upcall). */
    static int compareInts(MemorySegment a, MemorySegment b) {
        return Integer.compare(a.get(ValueLayout.JAVA_INT, 0), b.get(ValueLayout.JAVA_INT, 0));
    }
}
