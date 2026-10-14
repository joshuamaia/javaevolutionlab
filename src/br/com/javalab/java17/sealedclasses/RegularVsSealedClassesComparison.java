package br.com.javalab.java17.sealedclasses;

import java.util.Arrays;
import java.util.List;

/**
 * Java 17 - Comparação: classes comuns (hierarquia aberta) vs sealed classes (JEP 409)
 *
 * <p>Status: Definitivo (sealed classes). Histórico: Preview no Java 15 (JEP 360) e 16 (JEP 397).</p>
 *
 * <p>O que é: o mesmo domínio (status de entrega de um pedido) modelado de duas formas.
 * Na hierarquia aberta qualquer código pode criar novos subtipos; na selada o conjunto é fechado.</p>
 * <p>Benefício: com a hierarquia selada o compilador sabe todos os casos possíveis, então um
 * {@code switch} pode ser exaustivo SEM {@code default}. Se alguém adicionar um novo subtipo
 * permitido, o código que não o trata deixa de compilar (em vez de cair silenciosamente no default).</p>
 * <p>Antes: hierarquia aberta + {@code default} defensivo (ou exceção em tempo de execução).</p>
 *
 * <p>Observação de sintaxe: o {@code switch} com type patterns usado em {@link #sealedLabel} era
 * Preview no Java 17 (JEP 406) e virou definitivo no Java 21 (JEP 441). Ele aparece aqui de forma
 * consciente, porque a exaustividade é justamente o maior ganho de sealed classes. Por isso este
 * arquivo compila com {@code --release 21} ou superior (não com {@code --release 17}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/sealedclasses/RegularVsSealedClassesComparison.java
 * </pre>
 */
public class RegularVsSealedClassesComparison {

    // ---------- Abordagem 1: classes comuns (hierarquia aberta) ----------
    abstract static class OpenDeliveryStatus { }
    static class OpenPending extends OpenDeliveryStatus { }
    static class OpenShipped extends OpenDeliveryStatus { }
    static class OpenDelivered extends OpenDeliveryStatus { }
    /** Qualquer pessoa pode criar isso, até em outro módulo/biblioteca. */
    static class OpenLostInTransit extends OpenDeliveryStatus { }

    // ---------- Abordagem 2: sealed (hierarquia fechada) ----------
    sealed interface DeliveryStatus permits Pending, Shipped, Delivered { }
    record Pending() implements DeliveryStatus { }
    record Shipped(String trackingCode) implements DeliveryStatus { }
    record Delivered(String receivedBy) implements DeliveryStatus { }
    // record Lost() implements DeliveryStatus { }  // erro: Lost não está em "permits"

    public static void main(String[] args) {
        System.out.println("=== Abordagem antiga: hierarquia aberta ===");
        System.out.println("OpenDeliveryStatus é sealed? " + OpenDeliveryStatus.class.isSealed());
        List<OpenDeliveryStatus> openStatuses = List.of(
                new OpenPending(), new OpenShipped(), new OpenDelivered(), new OpenLostInTransit());
        for (OpenDeliveryStatus status : openStatuses) {
            System.out.println(" - " + openLabel(status));
        }
        System.out.println("Problema: OpenLostInTransit caiu no 'default' e ninguém foi avisado.");

        System.out.println();
        System.out.println("=== Abordagem nova: hierarquia selada ===");
        System.out.println("DeliveryStatus é sealed? " + DeliveryStatus.class.isSealed());
        System.out.println("Permitidos: " + Arrays.stream(DeliveryStatus.class.getPermittedSubclasses())
                .map(Class::getSimpleName).toList());
        List<DeliveryStatus> statuses = List.of(
                new Pending(), new Shipped("BR123456789"), new Delivered("Maria"));
        for (DeliveryStatus status : statuses) {
            System.out.println(" - " + sealedLabel(status));
        }
        System.out.println("Ganho: o switch não tem 'default'. Se um novo subtipo for permitido,");
        System.out.println("o compilador acusa 'the switch expression does not cover all possible input values'.");

        System.out.println();
        System.out.println("=== Resumo ===");
        System.out.println("Aberta : extensível por qualquer um, switch precisa de default, erros só em runtime.");
        System.out.println("Selada : conjunto fechado e documentado no código, exaustividade checada na compilação.");
    }

    /** Hierarquia aberta: o compilador não sabe quantos subtipos existem, então o default é obrigatório. */
    static String openLabel(OpenDeliveryStatus status) {
        if (status instanceof OpenPending) {
            return "Aguardando envio";
        } else if (status instanceof OpenShipped) {
            return "Enviado";
        } else if (status instanceof OpenDelivered) {
            return "Entregue";
        } else {
            return "Status desconhecido (" + status.getClass().getSimpleName() + ")"; // "default" defensivo
        }
    }

    /** Hierarquia selada: switch exaustivo sem default (sintaxe definitiva no Java 21). */
    static String sealedLabel(DeliveryStatus status) {
        return switch (status) {
            case Pending p -> "Aguardando envio";
            case Shipped s -> "Enviado, rastreio " + s.trackingCode();
            case Delivered d -> "Entregue para " + d.receivedBy();
        };
    }
}
