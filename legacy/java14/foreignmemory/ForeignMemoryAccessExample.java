import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemoryHandles;
import jdk.incubator.foreign.MemoryLayout;
import jdk.incubator.foreign.MemoryLayout.PathElement;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.SequenceLayout;

import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

/**
 * Java 14 - Foreign-Memory Access API (JEP 370)
 *
 * <p>Status: Incubadora (modulo jdk.incubator.foreign). A API mudou a cada versao
 * (JEPs 383, 393, 412, 419, 424, 434, 442) e o modulo jdk.incubator.foreign foi
 * REMOVIDO; a forma final e a FFM API (java.lang.foreign, Definitiva no Java 22,
 * JEP 454). Este codigo NAO compila no JDK 25 e nao deve ser "adaptado".</p>
 *
 * <p>O que e: acesso seguro e eficiente a memoria fora do heap (off-heap):
 * {@code MemorySegment} (regiao de memoria com limites e ciclo de vida, liberada no
 * close), {@code MemoryAddress} (endereco dentro do segmento), {@code MemoryHandles}
 * (VarHandles para ler/escrever valores) e {@code MemoryLayout} (descricao da estrutura).</p>
 * <p>Beneficio: alternativa segura a sun.misc.Unsafe e mais flexivel que ByteBuffer
 * direto (limite de 2 GB, desalocacao nao deterministica).</p>
 * <p>Antes: ByteBuffer.allocateDirect ou sun.misc.Unsafe.</p>
 *
 * <p>Codigo baseado nos trechos da JEP 370 (API do JDK 14: baseAddress, addOffset,
 * withStride, MemoryLayout.ofValueBits).</p>
 *
 * <p>Validacao: NAO executado neste projeto (JDK 14 indisponivel no ambiente de
 * desenvolvimento); revisado contra os exemplos de codigo da JEP.</p>
 *
 * <p>Execucao (requer JDK 14 - nao roda no JDK 25):</p>
 * <pre>
 * java --add-modules jdk.incubator.foreign --source 14 legacy/java14/foreignmemory/ForeignMemoryAccessExample.java
 * </pre>
 */
public class ForeignMemoryAccessExample {

    public static void main(String[] args) {
        System.out.println("=== 1) VarHandle de int + deslocamento manual ===");
        VarHandle intHandle = MemoryHandles.varHandle(int.class, ByteOrder.nativeOrder());
        try (MemorySegment segment = MemorySegment.allocateNative(100)) {
            MemoryAddress base = segment.baseAddress();
            for (int i = 0; i < 25; i++) {
                intHandle.set(base.addOffset(i * 4), i * 10);
            }
            System.out.println("Elemento 3: " + (int) intHandle.get(base.addOffset(3 * 4)));
        } // memoria nativa liberada aqui, de forma deterministica

        System.out.println("=== 2) VarHandle indexado (withStride) ===");
        VarHandle intElemHandle = MemoryHandles.withStride(intHandle, 4);
        try (MemorySegment segment = MemorySegment.allocateNative(100)) {
            MemoryAddress base = segment.baseAddress();
            for (int i = 0; i < 25; i++) {
                intElemHandle.set(base, (long) i, i * i);
            }
            System.out.println("Elemento 5: " + (int) intElemHandle.get(base, 5L));
        }

        System.out.println("=== 3) Layout descrevendo um array de 25 ints ===");
        SequenceLayout intArrayLayout = MemoryLayout.ofSequence(25,
                MemoryLayout.ofValueBits(32, ByteOrder.nativeOrder()));
        VarHandle layoutHandle = intArrayLayout.varHandle(int.class, PathElement.sequenceElement());
        try (MemorySegment segment = MemorySegment.allocateNative(intArrayLayout)) {
            MemoryAddress base = segment.baseAddress();
            long count = intArrayLayout.elementCount().getAsLong();
            for (long i = 0; i < count; i++) {
                layoutHandle.set(base, i, (int) (i + 1000));
            }
            System.out.println("Tamanho do layout: " + intArrayLayout.byteSize() + " bytes");
            System.out.println("Ultimo elemento: " + (int) layoutHandle.get(base, count - 1));

            System.out.println("=== 4) Acesso fora dos limites e barrado ===");
            try {
                intHandle.set(base.addOffset(100), 1);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Bloqueado: " + e.getClass().getSimpleName());
            }
        }
    }
}
