# Java 25 — Compact Source Files and Instance Main Methods (JEP 512)

`HelloCompact.java` é um **arquivo de código compacto**: sem `package`, sem `class`, com um
`main` de instância (sem `public static`). Ele não pode ficar em `src/` porque não tem pacote
nem classe declarada — por isso está nesta pasta isolada.

## Executar (JDK 25)

```bash
cd standalone/java25-compact-source
java HelloCompact.java            # pede o nome se houver terminal interativo; senão usa "visitante"
java HelloCompact.java Maria      # usa o argumento, sem ler do teclado
```

Compilar explicitamente também funciona:

```bash
javac -d <dir-temporario> HelloCompact.java
java -cp <dir-temporario> HelloCompact
```

## O que observar

- Classe implícita `final` chamada `HelloCompact`, criada pelo compilador.
- `import module java.base;` implícito (`List`, `LocalDate` sem imports).
- `java.lang.IO` (`IO.println`, `IO.readln`). No Java 23/24 (Preview) era `java.io.IO` e os
  métodos eram importados estaticamente (`println(...)` sem `IO.`) — veja
  `legacy/java24/simplesourcefiles/SimpleSourceFileExample.java`.
- Classe normal, com pacote e `void main()` de instância:
  `src/br/com/javalab/java25/instancemain/InstanceMainExample.java`.
