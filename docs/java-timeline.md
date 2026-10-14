# Linha do tempo do Java — do Java 5 ao Java 25

Resumo cronológico das principais novidades de cada versão, com os recursos que **não** puderam ser demonstrados em código e a justificativa técnica. Os exemplos executáveis de cada item estão listados em [feature-matrix.md](feature-matrix.md).

| Período | Modelo de lançamento |
|---|---|
| 2004–2014 (Java 5 → 8) | Grandes versões a cada 2–3 anos, guiadas por JSRs do Java Community Process. |
| 2017 (Java 9) | Última versão "grande"; introduz o sistema de módulos. |
| 2018 em diante (Java 10 → 25) | Uma versão a cada **6 meses** (março/setembro). LTS: 11, 17, 21, 25 (a cada 2 anos desde o 17). Recursos novos amadurecem como **Preview** / **Incubadora** antes de virarem definitivos. |

## Trajetória dos recursos com várias Previews

| Recurso | Trajetória |
|---|---|
| Switch Expressions | 12 Preview (JEP 325) → 13 2ª Preview (JEP 354, `yield`) → **14 definitivo** (JEP 361) |
| Text Blocks | 13 Preview (JEP 355) → 14 2ª Preview (JEP 368) → **15 definitivo** (JEP 378) |
| Records | 14 Preview (JEP 359) → 15 2ª Preview (JEP 384) → **16 definitivo** (JEP 395) |
| Pattern Matching para `instanceof` | 14 Preview (JEP 305) → 15 2ª Preview (JEP 375) → **16 definitivo** (JEP 394) |
| Sealed Classes | 15 Preview (JEP 360) → 16 2ª Preview (JEP 397) → **17 definitivo** (JEP 409) |
| Pattern Matching para `switch` | 17 (JEP 406) → 18 (JEP 420) → 19 (JEP 427) → 20 (JEP 433) → **21 definitivo** (JEP 441) |
| Record Patterns | 19 Preview (JEP 405) → 20 2ª Preview (JEP 432) → **21 definitivo** (JEP 440) |
| Virtual Threads | 19 Preview (JEP 425) → 20 2ª Preview (JEP 436) → **21 definitivo** (JEP 444) |
| Foreign Function & Memory API | 14–16 incubadoras (JEP 370/383/393/389) → 17–18 incubadoras (JEP 412/419) → 19–21 Previews (JEP 424/434/442) → **22 definitivo** (JEP 454) |
| Unnamed Variables & Patterns | 21 Preview (JEP 443) → **22 definitivo** (JEP 456) |
| String Templates | 21 Preview (JEP 430) → 22 2ª Preview (JEP 459) → **retirado no 23** |
| Stream Gatherers | 22 Preview (JEP 461) → 23 2ª Preview (JEP 473) → **24 definitivo** (JEP 485) |
| Class-File API | 22 Preview (JEP 457) → 23 2ª Preview (JEP 466) → **24 definitivo** (JEP 484) |
| Flexible Constructor Bodies | 22 Preview (JEP 447, "statements before super") → 23 (JEP 482) → 24 (JEP 492) → **25 definitivo** (JEP 513) |
| Module Import Declarations | 23 Preview (JEP 476) → 24 2ª Preview (JEP 494) → **25 definitivo** (JEP 511) |
| Compact Source Files & Instance Main | 21 (JEP 445) → 22 (JEP 463) → 23 (JEP 477) → 24 (JEP 495) → **25 definitivo** (JEP 512) |
| Scoped Values | 20 Incubadora (JEP 429) → 21–24 Previews (JEP 446/464/481/487) → **25 definitivo** (JEP 506) |
| Key Derivation Function API | 24 Preview (JEP 478) → **25 definitivo** (JEP 510) |
| Compact Object Headers | 24 Experimental (JEP 450) → **25 produto** (JEP 519) |
| Structured Concurrency | 19–20 Incubadoras (JEP 428/437) → 21–25 Previews (JEP 453/462/480/499/505) — **ainda Preview no 25** |
| Primitive Types in Patterns | 23 (JEP 455) → 24 (JEP 488) → 25 (JEP 507) — **ainda Preview no 25** |
| Vector API | 16 → 25: dez incubadoras (JEP 338 … JEP 508) — **ainda Incubadora no 25** |

## Java 5 (J2SE 5.0 "Tiger", setembro de 2004, JSR 176)

- **Generics** (JSR 14): coleções e métodos tipados, wildcards `? extends` / `? super`, type erasure; tipos de retorno covariantes.
- **Annotations / metadata** (JSR 175): `@Override`, `@Deprecated`, `@SuppressWarnings` e anotações próprias lidas por reflexão (base de JUnit 4, JPA, Spring). Ferramenta `apt` (substituída pela JSR 269 no Java 6).
- **Enums, autoboxing, enhanced for, varargs e static import** (JSR 201).
- **java.util.concurrent** (JSR 166, Doug Lea): Executor/ExecutorService, Callable/Future, coleções concorrentes, BlockingQueue, locks explícitos (ReentrantLock/Condition), atômicos, Semaphore, CountDownLatch, CyclicBarrier, TimeUnit.
- **Novo modelo de memória Java** (JSR 133): semântica correta de `volatile` e `final` (base para o double-checked locking com volatile).
- **Monitoramento** (JSR 174 + JMX JSR 3/160 no JDK): MXBeans em `java.lang.management` e `jconsole`.
- APIs: `StringBuilder`, `Formatter`/`printf`/`String.format`, `Scanner`, `Queue`/`PriorityQueue`, `Iterable`, `ProcessBuilder`, `java.util.UUID`.
- JVM: Class Data Sharing (CDS) e ergonomia automática de GC/heap.

**Apenas documentado / observações:**

- `apt` (Annotation Processing Tool): ferramenta e API `com.sun.mirror` removidas no Java 8; o equivalente atual está em `java06/annotationprocessing`.
- CDS e ergonomia de GC: recursos internos da JVM, sem API para demonstrar (as versões posteriores tratam AppCDS/CDS dinâmico).
- JSR 133 (modelo de memória): conceito, não API; aparece implicitamente nos exemplos de concorrência (volatile, happens-before de locks/latches/barreiras).

## Java 6 (Java SE 6 "Mustang", dezembro de 2006, JSR 270)

- **Compiler API** (JSR 199, `javax.tools`): compilar código Java programaticamente, inclusive em memória.
- **Scripting API** (JSR 223, `javax.script`) com o engine Rhino (JavaScript) embutido. Rhino foi trocado por Nashorn no Java 8 e o Nashorn foi removido no Java 15 (JEP 372); a API continua no JDK, mas sem engine.
- **Pluggable Annotation Processing** (JSR 269) e **Language Model API** (`javax.lang.model`).
- **JDBC 4.0** (JSR 221): auto-carregamento de drivers via ServiceLoader, hierarquia nova de `SQLException` (transient/non-transient/recoverable), `SQLException` iterável, `RowId`, `SQLXML`, `Wrapper`. Java DB (Derby) distribuído junto com o JDK (até o 8).
- **Collections**: `Deque`/`ArrayDeque`/`LinkedBlockingDeque`, `NavigableMap`/`NavigableSet`, `ConcurrentSkipListMap/Set`, `Arrays.copyOf`, `Collections.newSetFromMap`/`asLifoQueue`, `AbstractMap.SimpleEntry`.
- **`java.util.ServiceLoader`** público (antes só o interno `sun.misc.Service`).
- **`java.io.Console`**, `String.isEmpty`, `@Override` também em implementações de interface.
- **XML/Web Services**: StAX (JSR 173), JAXB 2.0 (JSR 222), JAX-WS 2.0 (JSR 224) e XML Digital Signature (JSR 105). JAXB/JAX-WS foram removidos no Java 11 (JEP 320).
- **Desktop**: `java.awt.Desktop`, `SystemTray`/`TrayIcon`, `SwingWorker`, splash screen, melhorias de look-and-feel.
- **Monitoramento**: `findDeadlockedThreads`, `LockInfo`/`MonitorInfo`, `dumpAllThreads`, `getSystemLoadAverage`, Attach API (jconsole/jmap/jstack sem flags na JVM alvo).

**Apenas documentado / observações:**

- **JAXB 2.0 / JAX-WS 2.0**: removidos do JDK no Java 11 (JEP 320); exigiriam dependências externas (Jakarta), o que o projeto não usa.
- **Rhino/JavaScript**: inexistente no JDK 25; `ScriptingApiExample` lista as engines (vazio) e implementa um engine mínimo próprio ("calc") com `AbstractScriptEngine` + `ScriptEngineFactory` para demonstrar `eval`, `Bindings`, escopos ENGINE/GLOBAL e `ScriptException`.
- **Java DB (Derby)**: removido do JDK 9; o exemplo JDBC não usa banco (driver mínimo que simula falha de conexão).
- **Attach API**: citada no `MonitoringExample` (PID impresso para jconsole/jcmd/jstack), sem código, pois exige o módulo `jdk.attach` e outra JVM alvo.
- **SwingWorker, splash screen, XML Digital Signature**: fora do escopo (GUI/criptografia), apenas citados.

## Java 7 (julho de 2011)

- **Project Coin (JSR 334)**: diamond operator, try-with-resources (`AutoCloseable` + suppressed exceptions), multi-catch e rethrow mais preciso, `switch` com `String`, literais binários (`0b`) e underscores em literais numéricos.
- **NIO.2 (JSR 203)**: `Path`/`Paths`, `Files`, `FileVisitor`/`walkFileTree`, `DirectoryStream` com glob, `WatchService`, atributos de arquivo, exceções descritivas.
- **Fork/Join Framework (JSR 166y)**: `ForkJoinPool`, `RecursiveTask`/`RecursiveAction`, work-stealing; também `Phaser`, `TransferQueue`/`LinkedTransferQueue` e `ThreadLocalRandom`.
- **JSR 292 (invokedynamic)**: nova instrução de bytecode e API `java.lang.invoke` (`MethodHandle`, `MethodHandles.Lookup`, `MethodType`); base para linguagens dinâmicas na JVM e, depois, lambdas do Java 8.
- **`java.util.Objects`**: `equals`, `hash`, `toString(o, default)`, `requireNonNull`, `compare`, `deepEquals`.
- **Unicode 6.0**: `Character.getName`, `Character.UnicodeScript`, `isAlphabetic`, `isIdeographic`.
- **G1 GC**: incluído como experimental no 7 GA; suportado oficialmente a partir do **7u4** (`-XX:+UseG1GC`). Tornou-se o GC padrão no Java 9 (JEP 248).
- Primeira versão lançada pela Oracle (após a aquisição da Sun) e primeira com o OpenJDK como implementação de referência.

**Apenas documentado / observações:**

- **G1 GC**: é uma opção de coletor da JVM, não uma API; não há como "demonstrá-lo" de forma específica ao Java 7. O exemplo `java08/metaspace/MetaspaceObservationExample` lista os memory pools (incluindo `G1 Eden Space`/`G1 Old Gen`) no JDK atual.
- **Mudanças menores não exemplificadas**: SDP/SCTP de rede, TLS 1.2 no JSSE, XRender pipeline do Java2D, `Path.toRealPath`, classloaders paralelos (`registerAsParallelCapable`), `URLClassLoader.close()` — consideradas de baixo valor didático.
- **invokedynamic gerado pelo javac**: o compilador Java 7 não emite `invokedynamic` para código Java; o exemplo usa a API `MethodHandles` diretamente (lambdas do Java 8 e concatenação de Strings do Java 9 passaram a emitir).

## Java 8 (março de 2014, LTS)

- **Lambdas e method references** (JEP 126 / JSR 335), interfaces funcionais em `java.util.function`, **default** e **static methods** em interfaces.
- **Stream API** (JEP 107 — Bulk Data Operations for Collections): `filter`/`map`/`flatMap`/`reduce`, `Collectors` (`groupingBy`, `partitioningBy`, `joining`...), parallel streams.
- **Optional**, `StringJoiner`/`String.join`, novos métodos default em `Map` (`computeIfAbsent`, `merge`, `getOrDefault`...), `Iterable.forEach`, `Collection.removeIf`, `Comparator.comparing().thenComparing()`.
- **Date and Time API** `java.time` (JEP 150 / JSR 310): tipos imutáveis, `ZonedDateTime`, `Period`, `Duration`, `DateTimeFormatter`.
- **Concurrency Updates** (JEP 155): `CompletableFuture`, `LongAdder`/`LongAccumulator`, `StampedLock`, `ConcurrentHashMap` reescrito com operações em massa, `ForkJoinPool.commonPool()`.
- **Base64** (JEP 135), **Arrays.parallelSort** (JEP 103), **Repeating Annotations** (JEP 120), **Type Annotations** (JEP 104 / JSR 308).
- **JVM**: remoção da PermGen e introdução do **Metaspace** (JEP 122); motor JavaScript **Nashorn** (JEP 174), depreciado no Java 11 (JEP 335) e removido no Java 15 (JEP 372); HashMap com buckets em árvore sob colisões (JEP 180).
- Compact Profiles (JEP 161) e `jdeps` também chegaram no Java 8 (precursores da modularização do Java 9).

**Apenas documentado / observações:**

- **Nashorn (JEP 174)**: removido do JDK no Java 15 (JEP 372). `NashornAvailabilityCheck` não simula o motor; apenas consulta `javax.script` e confirma que "nashorn" não existe no JDK atual (o uso no Java 8 está mostrado no Javadoc).
- **Metaspace (JEP 122)**: recurso da JVM, não API — `MetaspaceObservationExample` observa os memory pools via `MemoryPoolMXBean` (mostra "Metaspace", "Compressed Class Space" e ausência de "Perm Gen") e o crescimento do Metaspace ao gerar 500 classes de proxy.
- **Não exemplificados** (baixo valor didático ou ferramentas): Compact Profiles (JEP 161), `jdeps`, `jjs`, Unsigned Integer Arithmetic (`Integer.parseUnsignedInt`, `compareUnsigned`), `SecureRandom.getInstanceStrong`, parâmetros nomeados por reflexão (`-parameters`, JEP 118), JavaFX 8, HashMap com árvores (JEP 180 — apenas citado no Javadoc de `MapImprovementsExample`).

## Java 9 (setembro de 2017)

- Java Platform Module System — Project Jigsaw (JEP 261 / JSR 376) e JDK dividido em módulos (JEP 200); `jlink` para runtimes customizados (JEP 282).
- JShell, o REPL oficial do Java (JEP 222).
- Fábricas de coleções imutáveis `List.of`, `Set.of`, `Map.of`, `Map.ofEntries` (JEP 269).
- Streams: `takeWhile`, `dropWhile`, `iterate` com predicado, `ofNullable`; Optional: `ifPresentOrElse`, `or`, `stream`.
- Métodos privados em interfaces, try-with-resources com variável effectively final, diamante em classes anônimas, `@SafeVarargs` em métodos privados (JEP 213 — Milling Project Coin).
- `ProcessHandle` / Process API (JEP 102), Flow API reativa e melhorias no `CompletableFuture` (JEP 266), `StackWalker` (JEP 259), `VarHandle` (JEP 193).
- Multi-Release JARs (JEP 238).
- JVM: G1 vira o GC padrão (JEP 248), Compact Strings (JEP 254), Unified JVM Logging `-Xlog` (JEP 158/271).

**Apenas documentado / observações:**

- `jlink` (JEP 282): é ferramenta de linha de comando; documentada e **validada** no README de `standalone/java09-modules` (gera runtime de ~49 MB com `java.base` + 2 módulos).
- `jar --release 9` (criação real de MRJAR pela ferramenta `jar`): documentado no Javadoc de `MultiReleaseJarExample`; o exemplo constrói o MRJAR programaticamente com `JarOutputStream` para ser autocontido.
- Itens menores não cobertos: HTML5 Javadoc (JEP 224), `@Deprecated(forRemoval)` (JEP 277), HTTP/2 Client em incubadora (JEP 110 — o exemplo fica no Java 11, versão definitiva), deprecação do Applet API (JEP 289).

## Java 10 (março de 2018)

- `var` para variáveis locais (JEP 286).
- `List.copyOf`, `Set.copyOf`, `Map.copyOf`; `Collectors.toUnmodifiableList/Set/Map`; `Optional.orElseThrow()`.
- G1 com Full GC paralelo (JEP 307) e interface limpa de GC na HotSpot (JEP 304).
- Application Class-Data Sharing — AppCDS (JEP 310).
- Thread-Local Handshakes (JEP 312).
- JVM reconhece limites de contêiner Docker (cgroups) e ganha `-XX:ActiveProcessorCount`, `-XX:MaxRAMPercentage` etc.
- Versionamento baseado em tempo: uma versão feature a cada 6 meses (JEP 322).
- Graal JIT experimental (JEP 317); remoção do `javah` (JEP 313).

**Apenas documentado / observações:**

- Graal JIT experimental (JEP 317): era só Linux/x64 e foi removido do OpenJDK no 17 (JEP 410); apenas documentado.
- Remoção do `javah` (JEP 313): ferramenta removida, substituída por `javac -h`.
- Thread-Local Handshakes e GC Interface não têm API Java — exemplos de observação.
- Reconhecimento de contêiner só é observável no Linux; no Windows o exemplo mostra `UseContainerSupport` como inexistente e simula com `ActiveProcessorCount`/`MaxRAMPercentage`.
- Itens menores não cobertos: Root Certificates (JEP 319), Heap em dispositivos alternativos (JEP 316), consolidação do repositório (JEP 296).

## Java 11 (setembro de 2018, LTS)

- HTTP Client padronizado em `java.net.http` (JEP 321), com HTTP/2, WebSocket e API assíncrona.
- Novos métodos em `String`: `isBlank`, `lines`, `strip`/`stripLeading`/`stripTrailing`, `repeat`.
- `Files.readString`/`writeString`, `Predicate.not`, `Optional.isEmpty`, `Collection.toArray(IntFunction)`.
- `var` em parâmetros de lambda (JEP 323).
- Executar arquivo-fonte diretamente com `java Arquivo.java` e scripts com shebang (JEP 330).
- Java Flight Recorder aberto no OpenJDK (JEP 328); Epsilon GC (JEP 318) e ZGC (JEP 333) experimentais.
- Nest-Based Access Control (JEP 181), TLS 1.3 (JEP 332), ChaCha20-Poly1305 (JEP 329).
- Remoção dos módulos Java EE e CORBA (JEP 320); Nashorn deprecated (JEP 335).

**Apenas documentado / observações:**

- ZGC (JEP 333): no Java 11 era experimental e só Linux/x64; documentado no Javadoc do `EpsilonGcExample` (produto no 15 — JEP 377).
- Remoção de Java EE e CORBA (JEP 320): só documentação — `javax.xml.bind`, `javax.xml.ws`, `javax.activation`, `javax.annotation`, `javax.transaction`, `org.omg.CORBA` não existem mais no JDK; usar dependências Jakarta.
- Nashorn deprecated (JEP 335): removido no Java 15 (JEP 372); não há como executar no JDK 25 sem a biblioteca standalone `org.openjdk.nashorn`.
- Itens menores não cobertos: Dynamic Class-File Constants (JEP 309), Low-Overhead Heap Profiling (JEP 331), Unicode 10 (JEP 327), Key Agreement com Curve25519/448 (JEP 324), deprecação do Pack200 (JEP 336).

## Java 12 (março de 2019)

- **Switch Expressions (JEP 325, Preview)**: switch como expressão, rótulos múltiplos `case A, B` e forma com seta `->`. O valor de um bloco era devolvido com `break valor;` (trocado por `yield` no Java 13).
- **APIs**: `String.indent`/`String.transform`, `Files.mismatch`, `Collectors.teeing`, Compact Number Formatting (`NumberFormat.getCompactNumberInstance`), `CompletableFuture.exceptionallyCompose/exceptionallyAsync`.
- **JVM Constants API (JEP 334)**: `java.lang.constant` (`ClassDesc`, `MethodTypeDesc`, `ConstantDescs`, `describeConstable()`).
- **Shenandoah (JEP 189, Experimental)**: GC de pausas curtas com compactação concorrente.
- **Default CDS Archives (JEP 341)**: o JDK vem com arquivo CDS pronto e ligado por padrão → inicialização mais rápida.
- **G1**: coletas mistas abortáveis (JEP 344) e devolução de memória não usada ao SO (JEP 346, `-XX:G1PeriodicGCInterval`).
- Microbenchmark Suite (JEP 230) no código-fonte do JDK.

**Apenas documentado / observações:**

- **JEP 344/346 (G1)** e **JEP 189 (Shenandoah)**: comportamento interno do GC; o exemplo `GcAndCdsObservationExample` mostra o GC ativo, o valor de `G1PeriodicGCInterval`/`MaxGCPauseMillis` via `HotSpotDiagnosticMXBean`, mede o tempo de inicialização com/sem CDS relançando `java -version` e testa se o build atual inclui Shenandoah (o Oracle JDK 25.0.2 usado **não** inclui).
- **JEP 230 (Microbenchmark Suite)**: faz parte do build do JDK, não é API.
- O par `src/.../java12/switchexpressions/SwitchExpressionPreviewExample.java` mostra somente a parte da sintaxe de 2019 que sobreviveu (seta + rótulos múltiplos); a parte removida (`break valor`) fica só no legacy.

## Java 13 (setembro de 2019)

- **Text Blocks (JEP 355, Preview)**: literais multilinha com `"""`, remoção de indentação incidental; ainda sem os escapes `\s` e `\<nova-linha>` (vieram no 14).
- Métodos associados `String.stripIndent`, `translateEscapes` e `formatted` (APIs Preview, marcadas `@Deprecated(forRemoval=true)` no 13/14; definitivas no 15).
- **Switch Expressions (JEP 354, 2ª Preview)**: `yield valor;` substitui `break valor;`.
- **Reimplementação da Socket API legada (JEP 353)**: `Socket`/`ServerSocket` passam a usar `NioSocketImpl`.
- **Dynamic CDS Archives (JEP 350)**: `-XX:ArchiveClassesAtExit` grava as classes da aplicação ao final da execução.
- **ZGC: Uncommit Unused Memory (JEP 351)**.
- `FileSystems.newFileSystem(Path)` / `(Path, Map)` para abrir ZIP/JAR sem URI.

**Apenas documentado / observações:**

- **JEP 351 (ZGC uncommit)**: comportamento interno do GC; apenas documentado no Javadoc de `DynamicCdsArchiveExample`.
- `DynamicCdsArchiveExample` gera e compila (javax.tools) um pequeno programa num diretório temporário, empacota em JAR (CDS só arquiva classes vindas de JARs) e o executa três vezes: sem arquivo, gravando e usando o arquivo dinâmico.
- `SocketReimplementationExample` mostra a classe interna `sun.nio.ch.NioSocketImpl` inspecionando a pilha da thread bloqueada em `accept()`.

## Java 14 (março de 2020)

- **Switch Expressions definitivas (JEP 361)**: seta, rótulos múltiplos, `yield`, exaustividade.
- **Records (JEP 359, Preview)**: classes portadoras de dados imutáveis.
- **Pattern Matching for instanceof (JEP 305, Preview)**: `obj instanceof String s`.
- **Helpful NullPointerExceptions (JEP 358)**: mensagens dizem exatamente o que era nulo (ligado por padrão a partir do 15).
- **Text Blocks 2ª Preview (JEP 368)**: escapes `\s` e `\<nova-linha>`.
- **JFR Event Streaming (JEP 349)**: `RecordingStream` para consumir eventos em tempo real.
- **Foreign-Memory Access API (JEP 370, Incubadora)**: `jdk.incubator.foreign` (`MemorySegment`, `MemoryAddress`, `MemoryHandles`).
- **jpackage (JEP 343, Incubadora)**; **remoção do CMS (JEP 363)**; ZGC no Windows/macOS (JEP 365/364, experimental); G1 NUMA-aware (JEP 345); deprecação de Solaris/SPARC e ParallelScavenge+SerialOld.

**Apenas documentado / observações:**

- **jpackage (JEP 343)**: ferramenta de linha de comando (`jpackage --input lib --main-jar app.jar --type msi`); definitiva no 16 (JEP 392).
- **Remoção do CMS (JEP 363)**: `-XX:+UseConcMarkSweepGC` passou a ser ignorada com aviso; nada a demonstrar em código.
- **JEP 345/364/365**: opções de JVM (`-XX:+UseNUMA`, `-XX:+UseZGC`).
- **Foreign-Memory Access**: o código original não é "adaptado" para a FFM atual; a FFM definitiva (Java 22) é coberta nas pastas das versões correspondentes.
- `HelpfulNullPointerExample` usa campos estáticos para que os nomes apareçam na mensagem; variáveis locais só aparecem pelo nome se compiladas com `javac -g` (o exemplo mostra `<local2>`).

## Java 15 (setembro de 2020)

- **Text Blocks definitivos (JEP 378)**, junto com `String.stripIndent/translateEscapes/formatted` definitivos.
- **Sealed Classes (JEP 360, Preview)**: `sealed`, `permits`, `non-sealed`.
- **Records 2ª Preview (JEP 384)**: records locais (e enums/interfaces locais), records implementando interfaces seladas, anotações em componentes (`ElementType.RECORD_COMPONENT`).
- **Pattern Matching for instanceof 2ª Preview (JEP 375)**: sem mudanças.
- **Hidden Classes (JEP 371)**: `Lookup.defineHiddenClass` para classes geradas em tempo de execução, não encontráveis por nome e descarregáveis.
- **EdDSA (JEP 339)**: assinaturas Ed25519/Ed448.
- **ZGC (JEP 377) e Shenandoah (JEP 379) como produto**; **biased locking desativado (JEP 374)**; **Nashorn removido (JEP 372)**; **DatagramSocket reimplementado (JEP 373)**; `CharSequence.isEmpty`.
- Foreign-Memory Access 2ª incubadora (JEP 383); RMI Activation deprecado (JEP 385); Solaris/SPARC removidos (JEP 381).

**Apenas documentado / observações:**

- **JEP 377/379/374/372**: mudanças de JVM; `JvmChangesObservationExample` relança `java -version` com `-XX:+UseZGC` (aceita), `-XX:+UseShenandoahGC` (rejeitada no Oracle JDK 25.0.2, build sem Shenandoah) e `-XX:+UseBiasedLocking` (flag removida no 18) e mostra que o motor "nashorn" não existe.
- **JEP 383** (Foreign-Memory 2ª incubadora): API removida; ver `legacy/java14`.
- Reflexão de sealed no Java 15 era `Class.permittedSubclasses()` (retornando `ClassDesc[]`), que não existe mais; o exemplo usa apenas `isSealed()` e o comentário explica; `getPermittedSubclasses()` aparece no java16.

## Java 16 (março de 2021)

- **Records definitivos (JEP 395)**, com relaxamento: classes internas podem declarar membros estáticos (campos, métodos, records, enums, interfaces).
- **Pattern Matching for instanceof definitivo (JEP 394)**: variáveis de padrão deixam de ser implicitamente `final`; teste inútil (`String s; s instanceof String t`) vira erro.
- **Stream.toList()** e **Stream.mapMulti()**.
- **Sealed Classes 2ª Preview (JEP 397)**: `getPermittedSubclasses()` retornando `Class<?>[]`, palavras-chave contextuais, conversões mais rígidas.
- **Vector API (JEP 338, Incubadora)**.
- **Unix-Domain Socket Channels (JEP 380)**.
- **Strongly Encapsulate JDK Internals by Default (JEP 396)**: `--illegal-access=deny` como padrão.
- **Warnings for Value-Based Classes (JEP 390)**; **Elastic Metaspace (JEP 387)**; **jpackage definitivo (JEP 392)**.
- Outros: migração do OpenJDK para Git/GitHub (JEP 357/369), ZGC com processamento concorrente de pilhas (JEP 376), Foreign Linker API incubadora (JEP 389), Foreign-Memory 3ª incubadora (JEP 393), port Alpine/musl (JEP 386) e Windows/AArch64 (JEP 388), C++14 no código do JDK (JEP 347).

**Apenas documentado / observações:**

- **jpackage (JEP 392)**: ferramenta de linha de comando (`jpackage --name Loja --input target --main-jar app.jar --type msi|deb|dmg`).
- **Elastic Metaspace (JEP 387)**: interno da JVM; `ElasticMetaspaceObservationExample` observa o pool "Metaspace" via MXBean enquanto cria 300 classes de proxy em ClassLoaders descartáveis e as descarrega (committed cai de ~3,5 MB para ~1,8 MB na execução de teste).
- **JEP 389/393** (Foreign Linker/Memory incubadoras): APIs removidas; a FFM definitiva (Java 22) é coberta na pasta correspondente.
- **JEP 376, 357/369, 386, 388, 347**: infraestrutura/portes, sem API.
- Vector API: o mini-benchmark não mostra ganho porque a JIT auto-vetoriza o laço escalar simples; o exemplo explica isso e recomenda JMH.

## Java 17 (setembro de 2021, LTS)

- **Sealed Classes definitivas (JEP 409)**: `sealed`/`permits`/`non-sealed`; hierarquias fechadas, base para switch exaustivo.
- **Pattern Matching for switch (JEP 406, 1ª Preview)**: `case String s`, `case null`; guards com `&&` e parenthesized patterns (sintaxes removidas depois).
- **Enhanced PRNGs (JEP 356)**: `RandomGenerator`, `RandomGeneratorFactory`, algoritmos LXM/Xoshiro, geradores jumpable/splittable.
- **Strong encapsulation (JEP 403)**: `--illegal-access` removida; reflexão profunda no JDK exige `--add-opens`.
- **Context-Specific Deserialization Filters (JEP 415)**: fábrica de filtros JVM-wide (`setSerialFilterFactory`).
- **Security Manager deprecated para remoção (JEP 411)**.
- **Always-strict floating point (JEP 306)**: `strictfp` redundante.
- `java.util.HexFormat`; pipeline Metal no macOS (JEP 382); macOS/AArch64 (JEP 391); remoção de RMI Activation (JEP 407) e do AOT/JIT Graal experimental (JEP 410); Applet API deprecated para remoção (JEP 398); Vector API 2ª incubadora (JEP 414); FFM incubadora (JEP 412).

**Apenas documentado / observações:**

- **JEP 382 (Metal)**: pipeline de renderização interno do Java2D no macOS; nada observável por API (no JDK 17 ativado com `-Dsun.java2d.metal=true`).
- **JEP 407 (RMI Activation)** e **JEP 410 (AOT/JIT experimental)**: remoções; não há o que executar.
- **JEP 411**: só observação (`SecurityManagerDeprecationExample` mostra que no JDK 25 `setSecurityManager` lança `UnsupportedOperationException`).
- JEP 391, 398, 412, 414: não pedidos/sem exemplo próprio (FFM e Vector API cobertos em outras versões).
- `RegularVsSealedClassesComparison` usa, de propósito, o switch com type patterns (definitivo no 21) para mostrar a exaustividade — explicado no Javadoc; por isso compila com `--release 21+`, não com `--release 17`.

## Java 18 (março de 2022)

- **UTF-8 por padrão (JEP 400)**: `Charset.defaultCharset()` é UTF-8 em qualquer SO; `native.encoding` guarda o charset da plataforma.
- **Simple Web Server (JEP 408)**: ferramenta `jwebserver` e API `SimpleFileServer` para servir arquivos estáticos.
- **Code Snippets no JavaDoc (JEP 413)**: tag `{@snippet}` com regiões, destaque, substituição e links.
- **Internet-Address Resolution SPI (JEP 418)**: `InetAddressResolverProvider` para resolvedores de nomes customizados.
- **Pattern Matching for switch 2ª Preview (JEP 420)**: dominância de constantes x guarded patterns, exaustividade com sealed genérico.
- **Finalization deprecated para remoção (JEP 421)**: `--finalization=disabled`; usar try-with-resources/`Cleaner`.
- Reflexão reimplementada com method handles (JEP 416); Vector API 3ª incubadora (JEP 417); FFM 2ª incubadora (JEP 419).

**Apenas documentado / observações:**

- **JEP 416 (Core Reflection com Method Handles)**: mudança interna de implementação; a API pública não muda (diferenças só de desempenho/stack traces). Para comparar no 18: `-Djdk.reflect.useDirectMethodHandle=false` (opção removida depois).
- **JEP 419 (FFM 2ª incubadora)**: API intermediária; a evolução é mostrada em `legacy/java19` e na FFM definitiva (Java 22).
- `Utf8ByDefaultExample` lê `stdout.encoding`, propriedade que só existe a partir do Java 19 (o exemplo avisa que é `null` no 18).

## Java 19 (setembro de 2022)

- **Virtual Threads (JEP 425, Preview)**: `Thread.ofVirtual()`, `Executors.newVirtualThreadPerTaskExecutor()`, `ExecutorService` como `AutoCloseable` (Projeto Loom).
- **Record Patterns (JEP 405, Preview)**: desestruturação de records em `instanceof`/`switch`; tinha "named record patterns" (removidos no 20).
- **Pattern Matching for switch 3ª Preview (JEP 427)**: guards `when` substituem `&&`; patterns incondicionais deixam de casar com `null`.
- **Structured Concurrency (JEP 428, Incubadora)**: `jdk.incubator.concurrent.StructuredTaskScope`.
- **Foreign Function & Memory API (JEP 424, Preview)**: primeira vez em `java.lang.foreign` (`MemorySession`, `Linker`, `SymbolLookup`).
- Vector API 4ª incubadora (JEP 426); porte Linux/RISC-V (JEP 422).

**Apenas documentado / observações:**

- **JEP 426 (Vector API 4ª incubadora)**: sem mudança didática relevante em relação ao exemplo do Java 18 (máscaras/reduções).
- **JEP 422 (Linux/RISC-V)**: porte de plataforma.
- Record patterns na forma atual não ganharam par em `src/.../java19` para evitar duplicação: estão em `java20/recordpatterns` (inferência) e `java21/recordpatterns` (definitivo).

## Java 20 (março de 2023)

- **Virtual Threads 2ª Preview (JEP 436)**: poucas mudanças; APIs auxiliares (`Thread.join(Duration)`, `ExecutorService.close()`) tornadas permanentes.
- **Record Patterns 2ª Preview (JEP 432)**: inferência de argumentos de tipo; record patterns no enhanced `for` (removidos no 21); fim dos named record patterns.
- **Pattern Matching for switch 4ª Preview (JEP 433)**: `MatchException`, exaustividade com sealed + record patterns, inferência em patterns genéricos.
- **Scoped Values (JEP 429, Incubadora)**: `jdk.incubator.concurrent.ScopedValue`.
- **Structured Concurrency 2ª Incubadora (JEP 437)**: forks herdam Scoped Values.
- FFM 2ª Preview (JEP 434); Vector API 5ª incubadora (JEP 438).

**Apenas documentado / observações:**

- **JEP 434 (FFM 2ª Preview)**: API intermediária (`Arena` surge aqui, `MemorySession` some); coberta por `legacy/java19` + FFM definitiva no 22.
- **JEP 438 (Vector API 5ª incubadora)**: sem mudança didática relevante.
- `Thread.Builder.allowSetThreadLocals(false)` (existia no 20, removido no 21) citado só no Javadoc.

## Java 21 (setembro de 2023, LTS)

- **Virtual Threads definitivas (JEP 444)**: thread-per-request escalável sem código reativo.
- **Record Patterns (JEP 440)** e **Pattern Matching for switch (JEP 441)** definitivos: patterns aninhados, `case null`, guards `when`, exaustividade com sealed, constantes de enum qualificadas.
- **Sequenced Collections (JEP 431)**: `getFirst`/`getLast`/`reversed`/`addFirst`, `SequencedMap`.
- **String Templates (JEP 430, Preview)**: `STR."..."`, `FMT` — **retirados no JDK 23**.
- **Scoped Values (JEP 446, Preview)** e **Structured Concurrency (JEP 453, Preview)** em `java.lang`/`java.util.concurrent`.
- **Unnamed Patterns and Variables (JEP 443, Preview)**: `_`.
- **Unnamed Classes and Instance Main Methods (JEP 445, Preview)**: `void main()` sem classe.
- **KEM API (JEP 452)**; **Generational ZGC (JEP 439)**; novos métodos (`StringBuilder.repeat`, `Character.isEmoji`, `Math.clamp`, `String.indexOf(ch, from, to)`, `splitWithDelimiters`).
- Avisos ao carregar agentes dinamicamente (JEP 451); Windows 32-bit deprecated (JEP 449); FFM 3ª Preview (JEP 442); Vector API 6ª incubadora (JEP 448).

**Apenas documentado / observações:**

- **JEP 451**: só gera aviso da JVM quando um agente é anexado em tempo de execução (Attach API); permitir sem aviso com `-XX:+EnableDynamicAgentLoading`. Demonstrar exigiria um JAR de agente e um segundo processo.
- **JEP 449**: depreciação de porte (Windows x86 32 bits).
- **JEP 442 / JEP 448**: versões intermediárias de FFM/Vector API, sem exemplo próprio.
- String Templates: não existem no JDK 25; `src/.../StringTemplatesAlternativesExample` mostra apenas alternativas padrão (concatenação, `formatted`, `MessageFormat`, text block, `StringBuilder`), sem simular templates.
- Unnamed classes: o arquivo legacy também roda no JDK 25 como "compact source file" (JEP 512), mas fica em `legacy/` por não ter pacote. Diferença observada: `getClass().isSynthetic()` é `true` no JDK 21 e `false` no JDK 25.

## Java 22 (março de 2024)

- **Foreign Function & Memory API definitiva (JEP 454)**: `Arena`, `MemorySegment`, `ValueLayout`, `Linker` (downcall/upcall) — substitui JNI e `ByteBuffer.allocateDirect`.
- **Unnamed Variables & Patterns definitivos (JEP 456)**: `_` em catch, laços, lambdas e patterns.
- **Launch Multi-File Source-Code Programs (JEP 458)**: `java Main.java` compila sob demanda outros arquivos da árvore de código-fonte.
- Previews: **Stream Gatherers (JEP 461)**, **Class-File API (JEP 457)**, **Statements before super(...) (JEP 447)**, Scoped Values 2ª (JEP 464), Structured Concurrency 2ª (JEP 462), String Templates 2ª (JEP 459), Implicitly Declared Classes 2ª (JEP 463).
- **Region Pinning for G1 (JEP 423)**: o G1 não desativa mais o GC inteiro durante regiões críticas JNI. Vector API 7ª incubadora (JEP 460).

**Apenas documentado / observações:**

- **String Templates 2ª Preview (JEP 459)**: retirado no 23; exemplos ficam no Java 21.
- **Implicitly Declared Classes 2ª Preview (JEP 463)**: forma final mostrada no Java 25 (standalone) e forma da época de Preview no legado do Java 24.
- **Vector API 7ª incubadora (JEP 460)** e **Region Pinning for G1 (JEP 423)**: só matriz (GC interno; Vector API tem exemplo no 25).
- **Class-File API (JEP 457)**: a API mudou entre as Previews (22 → 23 → 24); o exemplo usa a API final (JDK 24/25) e o cabeçalho explica — não há legado porque não é possível validar a API exata do 22.
- **Scoped Values (JEP 464)**: só `where(...).run/call` (compatível com o 25) → ficou em `src/`; o `try/catch (Exception)` reflete que no 22 `call` recebia `Callable`.
- FFM no Windows: `strlen` e `qsort` encontrados via `Linker.nativeLinker().defaultLookup()` (CRT do Windows) — testado.

## Java 23 (setembro de 2024)

- **Markdown Documentation Comments (JEP 467)**: Javadoc em Markdown com `///`.
- **ZGC generacional por padrão (JEP 474)**; modo não generacional deprecated.
- **Deprecate sun.misc.Unsafe memory-access methods for removal (JEP 471)**.
- Previews: **Module Import Declarations (JEP 476)**, **Primitive Types in Patterns (JEP 455)**, Stream Gatherers 2ª (JEP 473), Class-File API 2ª (JEP 466), Flexible Constructor Bodies 2ª (JEP 482, campos antes de `super`), Scoped Values 3ª (JEP 481, `CallableOp`), Structured Concurrency 3ª (JEP 480), Implicitly Declared Classes 3ª (JEP 477, `java.io.IO` + import automático de `java.base`).
- **String Templates retirados** (sem JEP; removidos do JDK 23). Vector API 8ª incubadora (JEP 469).

**Apenas documentado / observações:**

- **JEP 471 (Unsafe)**: API interna; documentado. **String Templates**: retirados; exemplos no Java 21.
- **JEP 477** e **Vector API (JEP 469)**: só matriz (forma da época no legado do 24; Vector API no 25).
- Gatherers: o 23 mostra o que o 22 não mostrou (gatherer customizado com estado e short-circuit, `scan`, `mapConcurrent`).
- Class-File: o 22 gera, o 23 lê/analisa (`String.class.getResourceAsStream("String.class")`, funciona compilado e em modo código-fonte), o 24 transforma.
- Module import: `import module java.base;` compila no 25 sem preview → `src/`.

## Java 24 (março de 2025)

- **Stream Gatherers definitivos (JEP 485)** e **Class-File API definitiva (JEP 484)**.
- **Criptografia pós-quântica**: ML-KEM (JEP 496) e ML-DSA (JEP 497). **Key Derivation Function API** em Preview (JEP 478).
- **Virtual threads sem pinning em `synchronized` (JEP 491)**.
- **Ahead-of-Time Class Loading & Linking (JEP 483)** — primeiro entregável do Projeto Leyden.
- **Security Manager desativado permanentemente (JEP 486)**.
- Previews: Flexible Constructor Bodies 3ª (JEP 492), Module Import Declarations 2ª (JEP 494), Simple Source Files 4ª (JEP 495), Scoped Values 4ª (JEP 487 — remove `runWhere/callWhere`), Structured Concurrency 4ª (JEP 499), Primitive Patterns 2ª (JEP 488).
- Compact Object Headers experimental (JEP 450); Generational Shenandoah experimental (JEP 404); ZGC remove modo não generacional (JEP 490); avisos para JNI (JEP 472) e `sun.misc.Unsafe` (JEP 498); jlink sem JMODs (JEP 493); Vector API 9ª incubadora (JEP 489).

**Apenas documentado / observações:**

- **JEP 450** (Compact Object Headers experimental): exemplo no Java 25 (JEP 519). **JEP 488**: sem mudanças (exemplos no 23 e 25).
- **JEP 472, 498, 404, 490, 493**: documentados (avisos de runtime, GC indisponível no Oracle JDK — `-XX:+UseShenandoahGC` não suportado nesta build —, remoção de modo do ZGC, opção de build do JDK). **Vector API (JEP 489)**: exemplo no 25.
- KDF (JEP 478): API conferida com `javap` nos JDKs 24 e 25 — idêntica → `src/` sem preview.
- Module import: `import module java.se` exige `--add-modules java.se` no classpath (java.se não exporta pacotes, não é módulo raiz) — por isso o exemplo usa `java.sql` (transitivo para java.xml/java.logging) e explica `java.se` no Javadoc.
- AOT: classes da aplicação só entram no cache se vierem de JAR (de diretório: "Unsupported location"); medido no JDK 24: ~49 ms → ~30 ms até o fim do main.
- JEP 491 medido: JDK 25 → `synchronized` 111 ms x `ReentrantLock` 108 ms; JDK 21 → 448 ms x 108 ms (32 carriers, 128 virtual threads).

## Java 25 (setembro de 2025, LTS)

- **Definitivos**: Scoped Values (JEP 506), Compact Source Files and Instance Main Methods (JEP 512, `java.lang.IO`), Flexible Constructor Bodies (JEP 513), Module Import Declarations (JEP 511), Key Derivation Function API (JEP 510), JFR Method Timing & Tracing (JEP 520).
- **JVM/Leyden**: Compact Object Headers em produto (JEP 519), AOT Command-Line Ergonomics (JEP 514, `-XX:AOTCacheOutput`), AOT Method Profiling (JEP 515), Generational Shenandoah em produto (JEP 521), JFR Cooperative Sampling (JEP 518), JFR CPU-Time Profiling experimental (JEP 509).
- **Previews**: Structured Concurrency 5ª com API nova (JEP 505: `StructuredTaskScope.open` + `Joiner`), Stable Values (JEP 502), Primitive Types in Patterns 3ª (JEP 507), PEM Encodings (JEP 470). **Vector API 10ª incubadora (JEP 508)**.
- Remoção do port x86 32 bits (JEP 503).

**Apenas documentado / observações:**

- **Correção**: a Vector API no JDK 25 é a **10ª** incubadora (JEPs 338, 414, 417, 426, 438, 448, 460, 469, 489, 508), não a 11ª.
- **JEP 509 (CPU-Time Profiling)**: experimental e só Linux — documentado. **JEP 518**: mudança interna do JFR — documentado.
- **JEP 521 (Generational Shenandoah)**: a build Oracle JDK 25.0.2 usada no projeto não inclui Shenandoah (`-XX:+UseShenandoahGC` → "not supported"); documentado. Em builds OpenJDK/Temurin: `-XX:+UseShenandoahGC -XX:ShenandoahGCMode=generational`.
- **JEP 503**: remoção de plataforma — documentado.
- `StableValuesExample`: Preview do 25; no JDK 26 o recurso foi re-proposto como "Lazy Constants" (API diferente) — citado no Javadoc.
- `ScopedValuesExample` mostra a mudança final do 25 (`orElse(null)` → NPE); a herança de Scoped Values por subtarefas é demonstrada em `StructuredConcurrencyFifthPreviewExample` (o comparativo evita Preview para rodar sem flags).
- Medições observadas (Windows, JDK 25.0.2): Compact Object Headers ~24,2 → ~16,2 bytes por `record Point(int,int)` (heap +80 MB → +57 MB para 3 milhões); AOT (JEP 514) compilado ~42 → ~27 ms, código-fonte ~445 → ~224 ms; JEP 515 os 5 primeiros lotes ~40% mais rápidos, mas o total de 30 lotes não melhorou neste micro-exemplo (explicado no Javadoc); Vector API ~4–5x mais rápida que o laço escalar (AVX2, 8 floats).

