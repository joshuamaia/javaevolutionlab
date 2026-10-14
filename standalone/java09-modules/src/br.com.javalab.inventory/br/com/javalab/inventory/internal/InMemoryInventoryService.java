package br.com.javalab.inventory.internal;

import java.util.HashMap;
import java.util.Map;

import br.com.javalab.inventory.api.InventoryService;

/**
 * Implementação interna (pacote NÃO exportado). Apesar de {@code public}, outros módulos
 * não conseguem referenciá-la: só a obtêm como {@link InventoryService} via ServiceLoader.
 */
public class InMemoryInventoryService implements InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();

    public InMemoryInventoryService() {
        stock.put("TECLADO", 10);
        stock.put("MOUSE", 3);
    }

    @Override
    public int availableQuantity(String sku) {
        return stock.getOrDefault(sku, 0);
    }

    @Override
    public boolean reserve(String sku, int quantity) {
        int available = availableQuantity(sku);
        if (available < quantity) {
            return false;
        }
        stock.put(sku, available - quantity);
        return true;
    }
}
