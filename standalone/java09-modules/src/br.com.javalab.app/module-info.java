/**
 * Java 9 - Módulo da aplicação (JEP 261).
 *
 * <p>Declara a dependência do módulo de estoque e que consome o serviço
 * {@code InventoryService} via {@code uses} (ServiceLoader).</p>
 */
module br.com.javalab.app {
    requires br.com.javalab.inventory;

    uses br.com.javalab.inventory.api.InventoryService;
}
