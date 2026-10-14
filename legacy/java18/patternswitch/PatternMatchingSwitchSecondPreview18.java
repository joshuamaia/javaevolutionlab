/**
 * Java 18 - Pattern Matching for switch, segunda Preview (JEP 420) - SINTAXE ORIGINAL
 *
 * <p>Status: Segunda Preview no Java 18. Histórico: 1a Preview no 17 (JEP 406); 3a no 19 (JEP 427,
 * troca {@code &&} por {@code when}); 4a no 20 (JEP 433); definitivo no 21 (JEP 441).</p>
 *
 * <p>O que mudou em relação ao Java 17 (a sintaxe com {@code &&} continuou a mesma):</p>
 * <ul>
 *   <li><b>Dominância mais rígida</b>: um rótulo constante ({@code case 0}) precisa aparecer ANTES
 *       de um guarded pattern do mesmo tipo ({@code case Integer i && i > 0}); o contrário é erro
 *       de compilação. (Na versão definitiva, Java 21, a regra foi relaxada: um guarded pattern não
 *       domina mais um rótulo constante; o JDK 25 aceita as duas ordens.)</li>
 *   <li><b>Exaustividade com sealed genérico mais precisa</b>: o compilador considera os argumentos
 *       de tipo; se um subtipo permitido não pode ser instância de {@code Coupon<Integer>}, ele não
 *       precisa de {@code case}.</li>
 * </ul>
 * <p>Este arquivo usa guarded patterns com {@code &&} e por isso NÃO compila no JDK 25 (fica em legacy/).</p>
 *
 * <p>Execução (requer JDK 18 - não validado: JDK 18 indisponível neste ambiente):</p>
 * <pre>
 * java --enable-preview --source 18 legacy/java18/patternswitch/PatternMatchingSwitchSecondPreview18.java
 * </pre>
 */
public class PatternMatchingSwitchSecondPreview18 {

    /** Cupom de desconto: o tipo do valor varia conforme o subtipo. */
    sealed interface Coupon<T> permits PercentCoupon, AmountCoupon { }
    /** Sempre Coupon<String>: nunca pode ser um Coupon<Integer>. */
    static final class PercentCoupon<X> implements Coupon<String> { }
    static final class AmountCoupon<Y> implements Coupon<Y> {
        final Y value;
        AmountCoupon(Y value) { this.value = value; }
    }

    public static void main(String[] args) {
        System.out.println("=== Dominância: constante antes do guarded pattern ===");
        for (Integer quantity : new Integer[] {0, 5, -2}) {
            System.out.println(quantity + " -> " + describeQuantity(quantity));
        }

        System.out.println();
        System.out.println("=== Exaustividade com sealed genérico ===");
        System.out.println(discount(new AmountCoupon<>(25)));
    }

    static String describeQuantity(Integer quantity) {
        return switch (quantity) {
            case 0 -> "Sem itens";                       // precisa vir antes (regra nova do Java 18)
            case Integer q && q > 0 -> q + " itens no carrinho";
            case Integer q -> "Quantidade inválida: " + q;
            // Se "case 0" viesse depois do guarded pattern, o Java 18 recusaria (rótulo dominado).
        };
    }

    /** Exaustivo sem default: PercentCoupon implementa Coupon<String>, então não cabe aqui. */
    static String discount(Coupon<Integer> coupon) {
        return switch (coupon) {
            case AmountCoupon<Integer> amount -> "Desconto de R$ " + amount.value;
        };
    }
}
