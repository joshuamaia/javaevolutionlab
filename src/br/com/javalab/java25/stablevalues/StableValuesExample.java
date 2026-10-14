package br.com.javalab.java25.stablevalues;

import java.util.List;
import java.util.function.Supplier;

/**
 * Java 25 - Stable Values (JEP 502)
 *
 * <p>Status: Preview (primeira). Exige {@code --enable-preview}. Evolução: no Java 26 a JEP 526
 * re-propõe o recurso como "Lazy Constants", com API diferente.</p>
 *
 * <p>O que é: {@code StableValue<T>} guarda um valor que é definido no MÁXIMO uma vez
 * ("imutabilidade adiada"). Depois de definido, a JVM pode tratá-lo como constante (igual a um
 * campo {@code final}) e aplicar constant folding. Formas principais:</p>
 * <ul>
 *   <li>{@code StableValue.of()} + {@code orElseSet(supplier)}: inicializa na 1ª leitura, uma vez só,
 *       mesmo com várias threads;</li>
 *   <li>{@code StableValue.supplier(supplier)}: um {@code Supplier} memoizado;</li>
 *   <li>{@code StableValue.list(n, i -> ...)}: lista imutável cujos elementos são calculados
 *       sob demanda, cada um uma única vez.</li>
 * </ul>
 * <p>Benefício: inicialização preguiçosa (startup mais rápido) com a mesma performance de
 * {@code final} e sem código de sincronização.</p>
 * <p>Antes: double-checked locking com {@code volatile} (fácil de errar e opaco para o JIT),
 * o idiom "holder class" (só para estáticos) ou inicialização ansiosa em campo {@code final}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java --enable-preview --source 25 src/br/com/javalab/java25/stablevalues/StableValuesExample.java
 * </pre>
 */
public class StableValuesExample {

    /** Recurso caro de criar (ex.: conexão, parser, configuração lida do disco). */
    static final class TaxTable {
        TaxTable() {
            System.out.println("  >> carregando tabela de impostos (caro)...");
        }

        double rateFor(String state) {
            return state.equals("SP") ? 0.18 : 0.12;
        }
    }

    // ---- Antes: double-checked locking ----
    static final class OldTaxService {
        private volatile TaxTable table;

        TaxTable table() {
            TaxTable result = table;
            if (result == null) {
                synchronized (this) {
                    result = table;
                    if (result == null) {
                        table = result = new TaxTable();
                    }
                }
            }
            return result;
        }
    }

    // ---- Java 25: StableValue ----
    static final class TaxService {
        private final StableValue<TaxTable> table = StableValue.of();

        TaxTable table() {
            return table.orElseSet(TaxTable::new); // no máximo uma execução, thread-safe
        }
    }

    // Supplier memoizado e lista preguiçosa
    static final Supplier<String> APP_VERSION = StableValue.supplier(() -> {
        System.out.println("  >> lendo versão do manifesto...");
        return "3.4.1";
    });

    static final List<String> INVOICE_SERIES = StableValue.list(5, index -> {
        System.out.println("  >> gerando série " + index);
        return "SERIE-" + (char) ('A' + index);
    });

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: double-checked locking ===");
        OldTaxService old = new OldTaxService();
        System.out.println("SP: " + old.table().rateFor("SP") + " | RJ: " + old.table().rateFor("RJ"));

        System.out.println();
        System.out.println("=== StableValue.of() + orElseSet ===");
        TaxService service = new TaxService();
        System.out.println("Definido antes do uso? " + service.table.isSet());
        System.out.println("SP: " + service.table().rateFor("SP") + " | RJ: " + service.table().rateFor("RJ"));
        System.out.println("Definido depois? " + service.table.isSet());
        System.out.println("Mesma instância sempre? " + (service.table() == service.table.orElseThrow()));

        System.out.println();
        System.out.println("=== StableValue.supplier: calculado na 1ª chamada ===");
        System.out.println("Versão: " + APP_VERSION.get());
        System.out.println("Versão: " + APP_VERSION.get());

        System.out.println();
        System.out.println("=== StableValue.list: só os elementos acessados são calculados ===");
        System.out.println("Tamanho: " + INVOICE_SERIES.size());
        System.out.println("Elemento 3: " + INVOICE_SERIES.get(3));
        System.out.println("Elemento 3 de novo: " + INVOICE_SERIES.get(3));
        System.out.println("Elemento 0: " + INVOICE_SERIES.get(0));
    }
}
