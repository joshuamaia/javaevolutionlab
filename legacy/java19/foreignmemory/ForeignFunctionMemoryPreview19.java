import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;

/**
 * Java 19 - Foreign Function &amp; Memory API, Preview (JEP 424) - API ORIGINAL
 *
 * <p>Status: Preview no Java 19 (primeira vez em {@code java.lang.foreign}, após incubadoras no
 * 14-18). Depois: 2a Preview no 20 (JEP 434), 3a no 21 (JEP 442) e definitivo no 22 (JEP 454).</p>
 *
 * <p>O que é: chamar funções nativas (C) e acessar memória fora do heap sem JNI.</p>
 * <p>Benefício: código 100% Java, seguro (limites e tempo de vida verificados) e mais rápido que JNI.</p>
 * <p>Antes: JNI (código C + headers gerados) ou {@code sun.misc.Unsafe} / {@code ByteBuffer.allocateDirect}.</p>
 *
 * <p>O que é específico do Java 19 e não compila mais no JDK 25:</p>
 * <ul>
 *   <li>{@code MemorySession} controlava o tempo de vida (virou {@code Arena} no Java 20/21);</li>
 *   <li>{@code MemorySession} era um {@code SegmentAllocator}: {@code session.allocateUtf8String(...)}
 *       (hoje {@code arena.allocateFrom(...)});</li>
 *   <li>{@code SymbolLookup.lookup(String)} (renomeado para {@code find} no Java 20);</li>
 *   <li>{@code MemorySegment.allocateNative(layout, session)} e {@code getUtf8String} (hoje {@code getString}).</li>
 * </ul>
 *
 * <p>Execução (requer JDK 19 - não validado: JDK 19 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 19 legacy/java19/foreignmemory/ForeignFunctionMemoryPreview19.java
 * </pre>
 */
public class ForeignFunctionMemoryPreview19 {

    public static void main(String[] args) throws Throwable {
        System.out.println("=== Downcall: chamando strlen da biblioteca C ===");
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();
        MethodHandle strlen = linker.downcallHandle(
                stdlib.lookup("strlen").orElseThrow(),
                FunctionDescriptor.of(JAVA_LONG, ADDRESS));

        try (MemorySession session = MemorySession.openConfined()) {
            MemorySegment text = session.allocateUtf8String("Java Evolution Lab");
            long length = (long) strlen.invoke(text);
            System.out.println("strlen(\"Java Evolution Lab\") = " + length);

            System.out.println();
            System.out.println("=== Memória fora do heap com tempo de vida controlado ===");
            MemorySegment stock = MemorySegment.allocateNative(JAVA_INT.byteSize() * 5, session);
            for (int i = 0; i < 5; i++) {
                stock.setAtIndex(JAVA_INT, i, (i + 1) * 10);
            }
            int total = 0;
            for (int i = 0; i < 5; i++) {
                total += stock.getAtIndex(JAVA_INT, i);
            }
            System.out.println("Estoque total (5 posições nativas): " + total);
            System.out.println("Texto lido de volta: " + text.getUtf8String(0));
        } // ao fechar a sessão, toda a memória nativa é liberada

        System.out.println("Sessão fechada: acessar os segmentos agora lançaria IllegalStateException.");
    }
}
