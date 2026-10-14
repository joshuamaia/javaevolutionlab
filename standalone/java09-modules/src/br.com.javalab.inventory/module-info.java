/**
 * Java 9 - Módulo de estoque (JEP 261).
 *
 * <p>Exporta apenas o pacote de API; o pacote {@code internal} fica encapsulado
 * (inacessível para outros módulos, mesmo com classes {@code public}).
 * Também fornece uma implementação de serviço via {@code provides ... with}.</p>
 */
module br.com.javalab.inventory {
    exports br.com.javalab.inventory.api;

    provides br.com.javalab.inventory.api.InventoryService
            with br.com.javalab.inventory.internal.InMemoryInventoryService;
}
