package br.com.javalab.app;

import java.util.ServiceLoader;

import br.com.javalab.inventory.api.InventoryService;

/**
 * Java 9 - Java Platform Module System (JEP 261 / JSR 376)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>Aplicação modular que usa a API exportada pelo módulo {@code br.com.javalab.inventory},
 * obtém a implementação por {@code ServiceLoader} ({@code uses}/{@code provides}) e prova que
 * o pacote interno está encapsulado. Comandos de compilação/execução no README.md.</p>
 */
public class OrderApplication {

    public static void main(String[] args) {
        System.out.println("=== Módulo atual ===");
        Module module = OrderApplication.class.getModule();
        System.out.println("Nome: " + module.getName() + " | nomeado? " + module.isNamed());
        System.out.println("requires: " + module.getDescriptor().requires());

        System.out.println();
        System.out.println("=== Serviço obtido via ServiceLoader (uses/provides) ===");
        InventoryService inventory = ServiceLoader.load(InventoryService.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("nenhum provedor de InventoryService"));
        System.out.println("Implementação: " + inventory.getClass().getName());
        System.out.println("TECLADO disponível: " + inventory.availableQuantity("TECLADO"));
        System.out.println("Reservar 5 MOUSE? " + inventory.reserve("MOUSE", 5));
        System.out.println("Reservar 2 MOUSE? " + inventory.reserve("MOUSE", 2));

        System.out.println();
        System.out.println("=== Encapsulamento forte ===");
        Module inventoryModule = InventoryService.class.getModule();
        System.out.println("exporta ...inventory.api?      " + inventoryModule.isExported("br.com.javalab.inventory.api"));
        System.out.println("exporta ...inventory.internal? " + inventoryModule.isExported("br.com.javalab.inventory.internal"));
        try {
            // Referenciar InMemoryInventoryService no código nem compila (pacote não exportado).
            // Via reflexão a classe é encontrada, mas o acesso é negado:
            Class<?> internal = Class.forName("br.com.javalab.inventory.internal.InMemoryInventoryService");
            internal.getConstructor().newInstance();
            System.out.println("Acesso permitido (só acontece com --add-exports na linha de comando)");
        } catch (ReflectiveOperationException e) {
            System.out.println("Acesso negado: " + e.getClass().getSimpleName());
        }
    }
}
