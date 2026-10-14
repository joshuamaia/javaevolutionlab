package br.com.javalab.inventory.api;

/**
 * API pública (pacote exportado) do módulo de estoque.
 */
public interface InventoryService {

    int availableQuantity(String sku);

    boolean reserve(String sku, int quantity);
}
