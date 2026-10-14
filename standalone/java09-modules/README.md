# Java 9 — Java Platform Module System (JEP 261 / JSR 376)

Menor estrutura funcional com dois módulos:

```
src/
├── br.com.javalab.inventory/          módulo de estoque
│   ├── module-info.java               exports ...api; provides InventoryService with ...internal.InMemoryInventoryService
│   └── br/com/javalab/inventory/
│       ├── api/InventoryService.java              (pacote exportado)
│       └── internal/InMemoryInventoryService.java (pacote encapsulado)
└── br.com.javalab.app/                módulo da aplicação
    ├── module-info.java               requires br.com.javalab.inventory; uses InventoryService
    └── br/com/javalab/app/OrderApplication.java
```

## Compilar e executar (a partir desta pasta)

```bash
javac -d out --module-source-path src -m br.com.javalab.app,br.com.javalab.inventory
java --module-path out -m br.com.javalab.app/br.com.javalab.app.OrderApplication
```

Saída esperada (resumo): o módulo `br.com.javalab.app` é nomeado, obtém o
`InMemoryInventoryService` via `ServiceLoader` e o acesso reflexivo à classe interna
termina em `IllegalAccessException` (pacote `internal` não exportado).

Para garantir a sintaxe/API do Java 9, adicione `--release 9` ao `javac`.

## Experimentos

```bash
# Descrever um módulo compilado
java --module-path out --describe-module br.com.javalab.inventory

# Importar InMemoryInventoryService dentro de OrderApplication NÃO compila:
#   error: package br.com.javalab.inventory.internal does not exist  (mensagem do JDK 25; varia entre versões)

# Abrir o pacote interno à força (escape hatch para migração)
java --module-path out --add-exports br.com.javalab.inventory/br.com.javalab.inventory.internal=br.com.javalab.app \
     -m br.com.javalab.app/br.com.javalab.app.OrderApplication

# jlink (JEP 282): runtime mínimo só com java.base + os dois módulos (~50 MB em vez do JDK inteiro)
jlink --module-path out --add-modules br.com.javalab.app,br.com.javalab.inventory \
      --launcher order=br.com.javalab.app/br.com.javalab.app.OrderApplication --output order-runtime
order-runtime/bin/order        # (order.bat no Windows)
```

Apague `out/` e `order-runtime/` depois de testar.
