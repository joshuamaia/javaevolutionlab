package br.com.javalab.java17.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Java 17 - Context-Specific Deserialization Filters (JEP 415)
 *
 * <p>Status: Definitivo. Histórico: filtros de desserialização surgiram no Java 9 (JEP 290),
 * com um único filtro global ou um filtro por stream.</p>
 *
 * <p>O que é: uma <b>fábrica de filtros</b> para toda a JVM
 * ({@link ObjectInputFilter.Config#setSerialFilterFactory}) que decide, para cada
 * {@link ObjectInputStream} criado, qual filtro aplicar - por exemplo com base no contexto
 * da thread atual. Também chegaram utilitários: {@code allowFilter}, {@code rejectFilter},
 * {@code merge} e {@code rejectUndecidedClass}.</p>
 * <p>Benefício: desserializar dados não confiáveis é uma fonte clássica de vulnerabilidades
 * (gadget chains). Com a fábrica, cada contexto (ex.: "mensagens de pedidos") usa uma lista
 * branca própria, mesmo quando a desserialização acontece dentro de uma biblioteca.</p>
 * <p>Antes: só o filtro global (propriedade {@code jdk.serialFilter}) ou chamar
 * {@code setObjectInputFilter} em cada stream - impossível quando o stream é criado por terceiros.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/security/DeserializationFilterExample.java
 * </pre>
 */
public class DeserializationFilterExample {

    record Order(String id, double total) implements Serializable { }

    /** Classe "perigosa" que um atacante tentaria injetar no stream. */
    static class SuspiciousPayload implements Serializable {
        String command = "rm -rf /";
    }

    /** Filtro do contexto atual (definido por quem chama, consultado pela fábrica). */
    private static final ThreadLocal<ObjectInputFilter> CONTEXT_FILTER = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        // Fábrica JVM-wide: combina o filtro pedido para o stream com o filtro do contexto da thread.
        ObjectInputFilter.Config.setSerialFilterFactory((current, requested) -> {
            ObjectInputFilter context = CONTEXT_FILTER.get();
            ObjectInputFilter base = requested != null ? requested : current;
            if (context == null) {
                return base;
            }
            ObjectInputFilter merged = base == null ? context : ObjectInputFilter.merge(context, base);
            return ObjectInputFilter.rejectUndecidedClass(merged); // lista branca: o resto é rejeitado
        });

        byte[] orderBytes = serialize(new Order("PED-17", 259.90));
        byte[] payloadBytes = serialize(new SuspiciousPayload());

        // Contexto "pedidos": só a classe Order é permitida.
        ObjectInputFilter onlyOrders = ObjectInputFilter.allowFilter(
                type -> type == Order.class, ObjectInputFilter.Status.UNDECIDED);

        System.out.println("=== Sem filtro (comportamento perigoso padrão) ===");
        System.out.println("Desserializado: " + deserialize(payloadBytes).getClass().getSimpleName());

        System.out.println();
        System.out.println("=== Contexto 'pedidos' com lista branca ===");
        CONTEXT_FILTER.set(onlyOrders);
        try {
            System.out.println("Pedido aceito: " + deserialize(orderBytes));
            try {
                deserialize(payloadBytes);
                System.out.println("ERRO: payload deveria ter sido rejeitado!");
            } catch (InvalidClassException e) {
                System.out.println("Payload rejeitado: " + e.getMessage());
            }
        } finally {
            CONTEXT_FILTER.remove();
        }
    }

    static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        return bytes.toByteArray();
    }

    /** Código "de biblioteca": não sabe nada de filtros; a fábrica aplica o filtro do contexto. */
    static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }
}
