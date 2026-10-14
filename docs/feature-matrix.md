# Matriz de funcionalidades — Java 5 ao Java 25

Tabela de consulta com cada funcionalidade coberta pelo projeto, a especificação correspondente (JEP do OpenJDK ou JSR do JCP), o status **na versão em que aparece** e a classe que a demonstra.

**Legenda de status**

| Status | Significado |
|---|---|
| Definitivo | Parte permanente da plataforma a partir daquela versão. |
| Preview (1ª, 2ª, ...) | Completo, mas sujeito a mudança; exige `--enable-preview` naquele JDK. |
| Incubadora | API em módulo `jdk.incubator.*`; exige `--add-modules`. |
| Experimental | Recurso da JVM desligado por padrão (`-XX:+UnlockExperimentalVMOptions`). |
| Produto (JVM) | Recurso da JVM pronto para produção (não muda o código-fonte). |
| Deprecated / Removido / Retirado | Marcado para remoção, removido do JDK, ou Preview abandonado. |

**Coluna Classe**

- Links para `src/` → exemplos que compilam e rodam no **JDK 25** (alguns com flags; veja o cabeçalho de cada arquivo).
- Links para `legacy/` → sintaxe/API **original** que não compila mais no JDK 25; o cabeçalho do arquivo indica o JDK e o comando.
- Links para `standalone/` → exemplos com estrutura própria (módulos, arquivos compactos, script).
- "Documentado" → recurso sem demonstração viável em código (detalhes na justificativa e em [java-timeline.md](java-timeline.md)).

> Observação histórica: Java 5 a 8 eram especificados via **JSRs** do Java Community Process; as **JEPs** passaram a ser usadas a partir do JDK 8 e são a referência para todas as versões seguintes.

## Java 5

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 5 | Generics (raw types x genéricos, wildcards, método genérico) | JSR 14 | Definitivo | [GenericsExample](../src/br/com/javalab/java05/generics/GenericsExample.java) |
| 5 | Tipos de retorno covariantes | JSR 14 | Definitivo | [CovariantReturnExample](../src/br/com/javalab/java05/covariantreturn/CovariantReturnExample.java) |
| 5 | Enums (EnumSet, EnumMap, switch; x constantes int) | JSR 201 | Definitivo | [EnumExample](../src/br/com/javalab/java05/enums/EnumExample.java) |
| 5 | Annotations (customizada RUNTIME, @Override, @Deprecated, @SuppressWarnings) | JSR 175 | Definitivo | [AnnotationExample](../src/br/com/javalab/java05/annotations/AnnotationExample.java) |
| 5 | Autoboxing e Unboxing (e armadilhas) | JSR 201 | Definitivo | [AutoboxingExample](../src/br/com/javalab/java05/autoboxing/AutoboxingExample.java) |
| 5 | Enhanced for loop (x índice/Iterator) | JSR 201 | Definitivo | [EnhancedForExample](../src/br/com/javalab/java05/foreach/EnhancedForExample.java) |
| 5 | Varargs | JSR 201 | Definitivo | [VarargsExample](../src/br/com/javalab/java05/varargs/VarargsExample.java) |
| 5 | Static Import | JSR 201 | Definitivo | [StaticImportExample](../src/br/com/javalab/java05/staticimport/StaticImportExample.java) |
| 5 | Coleções concorrentes (ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue) | JSR 166 | Definitivo | [ConcurrentCollectionsExample](../src/br/com/javalab/java05/concurrentcollections/ConcurrentCollectionsExample.java) |
| 5 | ExecutorService / ScheduledExecutorService (x Thread manual) | JSR 166 | Definitivo | [ExecutorServiceExample](../src/br/com/javalab/java05/executorservice/ExecutorServiceExample.java) |
| 5 | Callable e Future (invokeAll, CompletionService) | JSR 166 | Definitivo | [CallableFutureExample](../src/br/com/javalab/java05/callablefuture/CallableFutureExample.java) |
| 5 | Variáveis atômicas | JSR 166 | Definitivo | [AtomicVariablesExample](../src/br/com/javalab/java05/atomic/AtomicVariablesExample.java) |
| 5 | ReentrantLock, tryLock e Condition (x synchronized) | JSR 166 | Definitivo | [ReentrantLockExample](../src/br/com/javalab/java05/reentrantlock/ReentrantLockExample.java) |
| 5 | Semaphore | JSR 166 | Definitivo | [SemaphoreExample](../src/br/com/javalab/java05/semaphore/SemaphoreExample.java) |
| 5 | CountDownLatch | JSR 166 | Definitivo | [CountDownLatchExample](../src/br/com/javalab/java05/countdownlatch/CountDownLatchExample.java) |
| 5 | CyclicBarrier | JSR 166 | Definitivo | [CyclicBarrierExample](../src/br/com/javalab/java05/cyclicbarrier/CyclicBarrierExample.java) |
| 5 | Queue e PriorityQueue | — | Definitivo | [PriorityQueueExample](../src/br/com/javalab/java05/queue/PriorityQueueExample.java) |
| 5 | StringBuilder | — | Definitivo | [StringBuilderExample](../src/br/com/javalab/java05/stringbuilder/StringBuilderExample.java) |
| 5 | Formatter, printf e String.format | — | Definitivo | [FormatterExample](../src/br/com/javalab/java05/formatter/FormatterExample.java) |
| 5 | Scanner | — | Definitivo | [ScannerExample](../src/br/com/javalab/java05/scanner/ScannerExample.java) |
| 5 | Monitoring and Management API (MXBeans) | JSR 174 | Definitivo | [ManagementExample](../src/br/com/javalab/java05/management/ManagementExample.java) |
| 5 | Ferramenta apt (Annotation Processing Tool) | — | Removido | Documentado (substituída pela JSR 269 no Java 6, removida no Java 8; ver java06/annotationprocessing) |
| 5 | Class Data Sharing (CDS) na HotSpot | — | Produto (JVM) | Documentado (recurso da JVM, sem API; ver versões posteriores para AppCDS) |

## Java 6

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 6 | Java Compiler API (compilação em memória + ClassLoader) | JSR 199 | Definitivo | [CompilerApiExample](../src/br/com/javalab/java06/compilerapi/CompilerApiExample.java) |
| 6 | Scripting API (engine próprio, Bindings, escopos) | JSR 223 | Definitivo | [ScriptingApiExample](../src/br/com/javalab/java06/scripting/ScriptingApiExample.java) |
| 6 | JDBC 4.0 (auto-load de drivers, nova hierarquia de SQLException, SQLException iterável) | JSR 221 | Definitivo | [Jdbc4Example](../src/br/com/javalab/java06/jdbc4/Jdbc4Example.java) |
| 6 | Pluggable Annotation Processing API | JSR 269 | Definitivo | [AnnotationProcessingExample](../src/br/com/javalab/java06/annotationprocessing/AnnotationProcessingExample.java) |
| 6 | StAX (Streaming API for XML) | JSR 173 | Definitivo | [StaxExample](../src/br/com/javalab/java06/stax/StaxExample.java) |
| 6 | NavigableMap | — | Definitivo | [NavigableMapExample](../src/br/com/javalab/java06/navigablemap/NavigableMapExample.java) |
| 6 | NavigableSet | — | Definitivo | [NavigableSetExample](../src/br/com/javalab/java06/navigableset/NavigableSetExample.java) |
| 6 | Deque, ArrayDeque e LinkedBlockingDeque | — | Definitivo | [DequeExample](../src/br/com/javalab/java06/deque/DequeExample.java) |
| 6 | Melhorias em Collections (Arrays.copyOf, newSetFromMap, asLifoQueue, ConcurrentSkipList*, SimpleEntry) | — | Definitivo | [CollectionsImprovementsExample](../src/br/com/javalab/java06/collections/CollectionsImprovementsExample.java) |
| 6 | ServiceLoader | — | Definitivo | [ServiceLoaderExample](../src/br/com/javalab/java06/serviceloader/ServiceLoaderExample.java) |
| 6 | Console API (System.console) | — | Definitivo | [ConsoleExample](../src/br/com/javalab/java06/console/ConsoleExample.java) |
| 6 | Monitoramento: findDeadlockedThreads, LockInfo/MonitorInfo, getSystemLoadAverage, Attach API | — | Definitivo | [MonitoringExample](../src/br/com/javalab/java06/monitoring/MonitoringExample.java) |
| 6 | @Override em métodos que implementam interface | — | Definitivo | [OverrideInterfaceExample](../src/br/com/javalab/java06/overrideinterface/OverrideInterfaceExample.java) |
| 6 | String.isEmpty e String/getBytes com Charset | — | Definitivo | [StringIsEmptyExample](../src/br/com/javalab/java06/stringisempty/StringIsEmptyExample.java) |
| 6 | java.awt.Desktop e SystemTray | — | Definitivo | [DesktopIntegrationExample](../src/br/com/javalab/java06/desktop/DesktopIntegrationExample.java) |
| 6 | JAXB 2.0 (JSR 222) e JAX-WS 2.0 (JSR 224) | JEP 320 | Removido | Documentado (removidos do JDK no Java 11 pela JEP 320; hoje Jakarta XML Binding/Web Services externos) |
| 6 | Engine JavaScript Rhino (javax.script) | JEP 372 | Removido | Documentado (Rhino substituído por Nashorn no Java 8; Nashorn removido no Java 15, JEP 372) |
| 6 | Java DB (Apache Derby empacotado no JDK) | — | Removido | Documentado (distribuído com o JDK 6–8; removido no JDK 9) |

## Java 7

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 7 | Diamond Operator (Project Coin) | JSR 334 | Definitivo | [DiamondOperatorExample](../src/br/com/javalab/java07/diamond/DiamondOperatorExample.java) |
| 7 | Try-with-resources e AutoCloseable (Project Coin) | JSR 334 | Definitivo | [TryWithResourcesExample](../src/br/com/javalab/java07/trywithresources/TryWithResourcesExample.java) |
| 7 | Throwable.addSuppressed / getSuppressed | JSR 334 | Definitivo | [SuppressedExceptionsExample](../src/br/com/javalab/java07/trywithresources/SuppressedExceptionsExample.java) |
| 7 | Multi-catch (Project Coin) | JSR 334 | Definitivo | [MultiCatchExample](../src/br/com/javalab/java07/multicatch/MultiCatchExample.java) |
| 7 | Rethrow mais preciso (Project Coin) | JSR 334 | Definitivo | [PreciseRethrowExample](../src/br/com/javalab/java07/multicatch/PreciseRethrowExample.java) |
| 7 | Strings no switch (Project Coin) | JSR 334 | Definitivo | [StringSwitchExample](../src/br/com/javalab/java07/switchstrings/StringSwitchExample.java) |
| 7 | Literais binários (Project Coin) | JSR 334 | Definitivo | [BinaryLiteralsExample](../src/br/com/javalab/java07/literals/BinaryLiteralsExample.java) |
| 7 | Underscores em literais numéricos (Project Coin) | JSR 334 | Definitivo | [UnderscoreLiteralsExample](../src/br/com/javalab/java07/literals/UnderscoreLiteralsExample.java) |
| 7 | NIO.2: Path / Paths | JSR 203 | Definitivo | [PathExample](../src/br/com/javalab/java07/nio2/PathExample.java) |
| 7 | NIO.2: Files (ler, escrever, copiar, mover, atributos) | JSR 203 | Definitivo | [FilesExample](../src/br/com/javalab/java07/nio2/FilesExample.java) |
| 7 | NIO.2: walkFileTree / FileVisitor | JSR 203 | Definitivo | [WalkFileTreeExample](../src/br/com/javalab/java07/nio2/WalkFileTreeExample.java) |
| 7 | NIO.2: DirectoryStream com glob / PathMatcher | JSR 203 | Definitivo | [DirectoryStreamExample](../src/br/com/javalab/java07/nio2/DirectoryStreamExample.java) |
| 7 | NIO.2: WatchService | JSR 203 | Definitivo | [WatchServiceExample](../src/br/com/javalab/java07/nio2/WatchServiceExample.java) |
| 7 | Comparação java.io.File vs NIO.2 | JSR 203 | Definitivo | [FileVsNio2Comparison](../src/br/com/javalab/java07/nio2/FileVsNio2Comparison.java) |
| 7 | Fork/Join Framework (RecursiveTask) | JSR 166 | Definitivo | [ForkJoinSumExample](../src/br/com/javalab/java07/forkjoin/ForkJoinSumExample.java)<br>[ForkJoinWordCountExample](../src/br/com/javalab/java07/forkjoin/ForkJoinWordCountExample.java) |
| 7 | Classe utilitária java.util.Objects | — | Definitivo | [ObjectsExample](../src/br/com/javalab/java07/objects/ObjectsExample.java) |
| 7 | ThreadLocalRandom | JSR 166 | Definitivo | [ThreadLocalRandomExample](../src/br/com/javalab/java07/concurrency/ThreadLocalRandomExample.java) |
| 7 | Phaser | JSR 166 | Definitivo | [PhaserExample](../src/br/com/javalab/java07/concurrency/PhaserExample.java) |
| 7 | TransferQueue / LinkedTransferQueue | JSR 166 | Definitivo | [TransferQueueExample](../src/br/com/javalab/java07/concurrency/TransferQueueExample.java) |
| 7 | invokedynamic e MethodHandles | JSR 292 | Definitivo | [MethodHandlesExample](../src/br/com/javalab/java07/invokedynamic/MethodHandlesExample.java) |
| 7 | Unicode 6.0, Character.getName e UnicodeScript | — | Definitivo | [CharacterGetNameExample](../src/br/com/javalab/java07/character/CharacterGetNameExample.java) |
| 7 | G1 Garbage Collector (suportado a partir do 7u4) | — | Produto (JVM) | Documentado (opção de GC da JVM; ver notas) |

## Java 8

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 8 | Expressões Lambda | JEP 126 | Definitivo | [LambdaBasicExample](../src/br/com/javalab/java08/lambda/LambdaBasicExample.java)<br>[AnonymousClassVsLambdaComparison](../src/br/com/javalab/java08/lambda/AnonymousClassVsLambdaComparison.java) |
| 8 | Interfaces funcionais (java.util.function) | JSR 335 | Definitivo | [BuiltInFunctionalInterfacesExample](../src/br/com/javalab/java08/functionalinterfaces/BuiltInFunctionalInterfacesExample.java)<br>[CustomFunctionalInterfaceExample](../src/br/com/javalab/java08/functionalinterfaces/CustomFunctionalInterfaceExample.java)<br>[FunctionCompositionExample](../src/br/com/javalab/java08/functionalinterfaces/FunctionCompositionExample.java) |
| 8 | Method References | JSR 335 | Definitivo | [MethodReferenceExample](../src/br/com/javalab/java08/methodreferences/MethodReferenceExample.java) |
| 8 | Stream API (filter, map, flatMap, reduce) | JEP 107 | Definitivo | [StreamFilterExample](../src/br/com/javalab/java08/streams/StreamFilterExample.java)<br>[StreamMapExample](../src/br/com/javalab/java08/streams/StreamMapExample.java)<br>[StreamFlatMapExample](../src/br/com/javalab/java08/streams/StreamFlatMapExample.java)<br>[StreamReduceExample](../src/br/com/javalab/java08/streams/StreamReduceExample.java) |
| 8 | Comparação for tradicional vs Streams | JEP 107 | Definitivo | [ForLoopVsStreamComparison](../src/br/com/javalab/java08/streams/ForLoopVsStreamComparison.java) |
| 8 | Collectors (toList, toSet, toMap, joining, averaging, summarizing) | JEP 107 | Definitivo | [CollectorsBasicExample](../src/br/com/javalab/java08/collectors/CollectorsBasicExample.java) |
| 8 | Collectors.groupingBy | JEP 107 | Definitivo | [GroupingByExample](../src/br/com/javalab/java08/collectors/GroupingByExample.java) |
| 8 | Collectors.partitioningBy | JEP 107 | Definitivo | [PartitioningByExample](../src/br/com/javalab/java08/collectors/PartitioningByExample.java) |
| 8 | Optional | — | Definitivo | [OptionalExample](../src/br/com/javalab/java08/optional/OptionalExample.java) |
| 8 | Default methods em interfaces | JSR 335 | Definitivo | [DefaultMethodsExample](../src/br/com/javalab/java08/interfaces/DefaultMethodsExample.java) |
| 8 | Métodos estáticos em interfaces | JSR 335 | Definitivo | [StaticInterfaceMethodsExample](../src/br/com/javalab/java08/interfaces/StaticInterfaceMethodsExample.java) |
| 8 | Date and Time API (java.time) | JEP 150 | Definitivo | [DateTimeApiExample](../src/br/com/javalab/java08/datetime/DateTimeApiExample.java)<br>[DateVsDateTimeApiComparison](../src/br/com/javalab/java08/datetime/DateVsDateTimeApiComparison.java) |
| 8 | CompletableFuture | JEP 155 | Definitivo | [CompletableFutureExample](../src/br/com/javalab/java08/completablefuture/CompletableFutureExample.java)<br>[FutureVsCompletableFutureComparison](../src/br/com/javalab/java08/completablefuture/FutureVsCompletableFutureComparison.java) |
| 8 | LongAdder / LongAccumulator | JEP 155 | Definitivo | [LongAdderExample](../src/br/com/javalab/java08/concurrency/LongAdderExample.java) |
| 8 | StampedLock | JEP 155 | Definitivo | [StampedLockExample](../src/br/com/javalab/java08/concurrency/StampedLockExample.java) |
| 8 | ConcurrentHashMap: operações em massa (forEach/search/reduce) | JEP 155 | Definitivo | [ConcurrentHashMapBulkExample](../src/br/com/javalab/java08/concurrency/ConcurrentHashMapBulkExample.java) |
| 8 | Base64 API | JEP 135 | Definitivo | [Base64Example](../src/br/com/javalab/java08/base64/Base64Example.java) |
| 8 | Parallel Streams | JEP 107 | Definitivo | [ParallelStreamExample](../src/br/com/javalab/java08/parallel/ParallelStreamExample.java) |
| 8 | Arrays.parallelSort / parallelPrefix / parallelSetAll | JEP 103 | Definitivo | [ArraysParallelSortExample](../src/br/com/javalab/java08/parallel/ArraysParallelSortExample.java) |
| 8 | Repeating Annotations | JEP 120 | Definitivo | [RepeatingAnnotationsExample](../src/br/com/javalab/java08/annotations/RepeatingAnnotationsExample.java) |
| 8 | Type Annotations | JEP 104 | Definitivo | [TypeAnnotationsExample](../src/br/com/javalab/java08/annotations/TypeAnnotationsExample.java) |
| 8 | StringJoiner e String.join | — | Definitivo | [StringJoinerExample](../src/br/com/javalab/java08/stringjoiner/StringJoinerExample.java) |
| 8 | Map.computeIfAbsent / computeIfPresent / compute | — | Definitivo | [ComputeIfAbsentExample](../src/br/com/javalab/java08/maps/ComputeIfAbsentExample.java) |
| 8 | Map.merge | — | Definitivo | [MergeExample](../src/br/com/javalab/java08/maps/MergeExample.java) |
| 8 | Melhorias em Map (getOrDefault, putIfAbsent, forEach, replaceAll...) | — | Definitivo | [MapImprovementsExample](../src/br/com/javalab/java08/maps/MapImprovementsExample.java) |
| 8 | Iterable.forEach, Collection.removeIf, List.replaceAll/sort | — | Definitivo | [IterableForEachRemoveIfExample](../src/br/com/javalab/java08/collections/IterableForEachRemoveIfExample.java) |
| 8 | Comparator.comparing().thenComparing() | — | Definitivo | [ComparatorComparingExample](../src/br/com/javalab/java08/collections/ComparatorComparingExample.java) |
| 8 | Remoção da PermGen / Metaspace | JEP 122 | Produto (JVM) | [MetaspaceObservationExample](../src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java) |
| 8 | Nashorn JavaScript Engine (removido no Java 15, JEP 372) | JEP 174 | Removido | [NashornAvailabilityCheck](../src/br/com/javalab/java08/nashorn/NashornAvailabilityCheck.java) |

## Java 9

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 9 | Java Platform Module System (module-info, exports, requires, provides/uses) e JDK modular | JEP 261 / JSR 376 / JEP 200 | Definitivo | [java09-modules (standalone)](../standalone/java09-modules/README.md)<br>[ModuleSystemInspectionExample](../src/br/com/javalab/java09/modules/ModuleSystemInspectionExample.java) |
| 9 | JShell (REPL e API jdk.jshell) | JEP 222 | Definitivo | [JShellApiExample](../src/br/com/javalab/java09/jshell/JShellApiExample.java) |
| 9 | List.of, Set.of, Map.of / Map.ofEntries (coleções imutáveis) | JEP 269 | Definitivo | [ListOfExample](../src/br/com/javalab/java09/collections/ListOfExample.java)<br>[SetOfExample](../src/br/com/javalab/java09/collections/SetOfExample.java)<br>[MapOfExample](../src/br/com/javalab/java09/collections/MapOfExample.java) |
| 9 | Comparação: ArrayList x Collections.unmodifiableList (view) x List.of | JEP 269 | Definitivo | [MutableVsImmutableCollectionsComparison](../src/br/com/javalab/java09/collections/MutableVsImmutableCollectionsComparison.java) |
| 9 | Stream.takeWhile | — | Definitivo | [StreamTakeWhileExample](../src/br/com/javalab/java09/streams/StreamTakeWhileExample.java) |
| 9 | Stream.dropWhile | — | Definitivo | [StreamDropWhileExample](../src/br/com/javalab/java09/streams/StreamDropWhileExample.java) |
| 9 | Stream.iterate com predicado | — | Definitivo | [StreamIterateWithPredicateExample](../src/br/com/javalab/java09/streams/StreamIterateWithPredicateExample.java) |
| 9 | Stream.ofNullable | — | Definitivo | [StreamOfNullableExample](../src/br/com/javalab/java09/streams/StreamOfNullableExample.java) |
| 9 | Optional.ifPresentOrElse | — | Definitivo | [OptionalIfPresentOrElseExample](../src/br/com/javalab/java09/optional/OptionalIfPresentOrElseExample.java) |
| 9 | Optional.or | — | Definitivo | [OptionalOrExample](../src/br/com/javalab/java09/optional/OptionalOrExample.java) |
| 9 | Optional.stream | — | Definitivo | [OptionalStreamExample](../src/br/com/javalab/java09/optional/OptionalStreamExample.java) |
| 9 | Métodos privados em interfaces | JEP 213 | Definitivo | [PrivateInterfaceMethodsExample](../src/br/com/javalab/java09/interfaces/PrivateInterfaceMethodsExample.java) |
| 9 | try-with-resources com variável effectively final | JEP 213 | Definitivo | [TryWithResourcesEffectivelyFinalExample](../src/br/com/javalab/java09/coin/TryWithResourcesEffectivelyFinalExample.java) |
| 9 | Diamante com classes anônimas | JEP 213 | Definitivo | [DiamondAnonymousClassExample](../src/br/com/javalab/java09/coin/DiamondAnonymousClassExample.java) |
| 9 | @SafeVarargs em métodos privados | JEP 213 | Definitivo | [SafeVarargsPrivateMethodExample](../src/br/com/javalab/java09/coin/SafeVarargsPrivateMethodExample.java) |
| 9 | Process API: ProcessHandle, pid, info, processo filho, onExit | JEP 102 | Definitivo | [ProcessHandleExample](../src/br/com/javalab/java09/processapi/ProcessHandleExample.java) |
| 9 | Reactive Streams: Flow API e SubmissionPublisher (backpressure) | JEP 266 | Definitivo | [FlowApiExample](../src/br/com/javalab/java09/flow/FlowApiExample.java) |
| 9 | CompletableFuture.orTimeout / completeOnTimeout / delayedExecutor | JEP 266 | Definitivo | [CompletableFutureTimeoutExample](../src/br/com/javalab/java09/completablefuture/CompletableFutureTimeoutExample.java) |
| 9 | Stack-Walking API (StackWalker) | JEP 259 | Definitivo | [StackWalkerExample](../src/br/com/javalab/java09/stackwalker/StackWalkerExample.java) |
| 9 | Multi-Release JAR Files | JEP 238 | Definitivo | [MultiReleaseJarExample](../src/br/com/javalab/java09/multirelease/MultiReleaseJarExample.java) |
| 9 | Variable Handles (VarHandle) | JEP 193 | Definitivo | [VarHandleExample](../src/br/com/javalab/java09/varhandles/VarHandleExample.java) |
| 9 | Compact Strings | JEP 254 | Produto (JVM) | [CompactStringsExample](../src/br/com/javalab/java09/compactstrings/CompactStringsExample.java) |
| 9 | G1 como GC padrão | JEP 248 | Produto (JVM) | [G1DefaultGcExample](../src/br/com/javalab/java09/gc/G1DefaultGcExample.java) |
| 9 | Unified JVM Logging (-Xlog) e Unified GC Logging | JEP 158 / JEP 271 | Produto (JVM) | [UnifiedJvmLoggingExample](../src/br/com/javalab/java09/logging/UnifiedJvmLoggingExample.java) |
| 9 | Objects.requireNonNullElse / requireNonNullElseGet / checkIndex | — | Definitivo | [ObjectsRequireNonNullElseExample](../src/br/com/javalab/java09/objects/ObjectsRequireNonNullElseExample.java) |
| 9 | InputStream.transferTo / readAllBytes | — | Definitivo | [InputStreamTransferToExample](../src/br/com/javalab/java09/io/InputStreamTransferToExample.java) |
| 9 | jlink: runtime customizado | JEP 282 | Definitivo | Documentado (ferramenta de linha de comando; comando validado no [README do java09-modules](../standalone/java09-modules/README.md)) |

## Java 10

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 10 | Inferência de tipo para variáveis locais (var): antes/depois, boas práticas e limitações | JEP 286 | Definitivo | [LocalVariableTypeInferenceExample](../src/br/com/javalab/java10/typeinference/LocalVariableTypeInferenceExample.java) |
| 10 | List.copyOf | — | Definitivo | [ListCopyOfExample](../src/br/com/javalab/java10/collections/ListCopyOfExample.java) |
| 10 | Set.copyOf | — | Definitivo | [SetCopyOfExample](../src/br/com/javalab/java10/collections/SetCopyOfExample.java) |
| 10 | Map.copyOf | — | Definitivo | [MapCopyOfExample](../src/br/com/javalab/java10/collections/MapCopyOfExample.java) |
| 10 | Collectors.toUnmodifiableList / toUnmodifiableSet / toUnmodifiableMap | — | Definitivo | [ToUnmodifiableCollectorsExample](../src/br/com/javalab/java10/collectors/ToUnmodifiableCollectorsExample.java) |
| 10 | Optional.orElseThrow() | — | Definitivo | [OptionalOrElseThrowExample](../src/br/com/javalab/java10/optional/OptionalOrElseThrowExample.java) |
| 10 | Parallel Full GC para G1 e GC Interface | JEP 307 / JEP 304 | Produto (JVM) | [G1ParallelFullGcExample](../src/br/com/javalab/java10/gc/G1ParallelFullGcExample.java) |
| 10 | Application Class-Data Sharing (AppCDS) | JEP 310 | Produto (JVM) | [ApplicationClassDataSharingExample](../src/br/com/javalab/java10/cds/ApplicationClassDataSharingExample.java) |
| 10 | Thread-Local Handshakes | JEP 312 | Produto (JVM) | [ThreadLocalHandshakesExample](../src/br/com/javalab/java10/handshakes/ThreadLocalHandshakesExample.java) |
| 10 | Reconhecimento de contêineres (cgroups, ActiveProcessorCount, MaxRAMPercentage) | — | Produto (JVM) | [ContainerAwarenessExample](../src/br/com/javalab/java10/container/ContainerAwarenessExample.java) |
| 10 | Versionamento baseado em tempo (Runtime.Version.feature/interim/update/patch) | JEP 322 | Definitivo | [TimeBasedReleaseVersioningExample](../src/br/com/javalab/java10/versioning/TimeBasedReleaseVersioningExample.java) |
| 10 | Compilador JIT experimental baseado em Graal | JEP 317 | Experimental | Documentado (flag de JVM só em Linux/x64; removido do OpenJDK no 17) |
| 10 | Remoção da ferramenta javah | JEP 313 | Removido | Documentado (substituída por javac -h desde o Java 8) |

## Java 11

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 11 | HTTP Client (GET síncrono, POST, sendAsync) | JEP 321 | Definitivo | [HttpClientExample](../src/br/com/javalab/java11/httpclient/HttpClientExample.java) |
| 11 | Comparação: HttpURLConnection x HttpClient | JEP 321 | Definitivo | [HttpUrlConnectionVsHttpClientComparison](../src/br/com/javalab/java11/httpclient/HttpUrlConnectionVsHttpClientComparison.java) |
| 11 | String.isBlank | — | Definitivo | [StringIsBlankExample](../src/br/com/javalab/java11/string/StringIsBlankExample.java) |
| 11 | String.lines | — | Definitivo | [StringLinesExample](../src/br/com/javalab/java11/string/StringLinesExample.java) |
| 11 | String.strip / stripLeading / stripTrailing (x trim com espaços Unicode) | — | Definitivo | [StringStripExample](../src/br/com/javalab/java11/string/StringStripExample.java) |
| 11 | String.repeat | — | Definitivo | [StringRepeatExample](../src/br/com/javalab/java11/string/StringRepeatExample.java) |
| 11 | Files.readString / Files.writeString | — | Definitivo | [FilesReadWriteStringExample](../src/br/com/javalab/java11/files/FilesReadWriteStringExample.java) |
| 11 | Predicate.not | — | Definitivo | [PredicateNotExample](../src/br/com/javalab/java11/predicate/PredicateNotExample.java) |
| 11 | Optional.isEmpty | — | Definitivo | [OptionalIsEmptyExample](../src/br/com/javalab/java11/optional/OptionalIsEmptyExample.java) |
| 11 | var em parâmetros de lambda (com anotações) | JEP 323 | Definitivo | [VarInLambdaParametersExample](../src/br/com/javalab/java11/lambda/VarInLambdaParametersExample.java) |
| 11 | Execução direta de arquivo-fonte (java Arquivo.java) e scripts com shebang | JEP 330 | Definitivo | [SingleFileSourceLaunchExample](../src/br/com/javalab/java11/launcher/SingleFileSourceLaunchExample.java)<br>[java11-shebang (standalone)](../standalone/java11-shebang/README.md) |
| 11 | Collection.toArray(IntFunction) | — | Definitivo | [CollectionToArrayExample](../src/br/com/javalab/java11/collections/CollectionToArrayExample.java) |
| 11 | Epsilon: GC no-op | JEP 318 | Experimental | [EpsilonGcExample](../src/br/com/javalab/java11/gc/EpsilonGcExample.java) |
| 11 | ZGC (coletor de baixa latência) | JEP 333 | Experimental | Documentado (só Linux/x64 no Java 11; flags no Javadoc do EpsilonGcExample) |
| 11 | Java Flight Recorder (Recording, evento customizado, RecordingFile) | JEP 328 | Produto (JVM) | [FlightRecorderExample](../src/br/com/javalab/java11/jfr/FlightRecorderExample.java) |
| 11 | Nest-Based Access Control (getNestHost, getNestMembers, isNestmateOf) | JEP 181 | Definitivo | [NestBasedAccessControlExample](../src/br/com/javalab/java11/nestmates/NestBasedAccessControlExample.java) |
| 11 | TLS 1.3 | JEP 332 | Definitivo | [Tls13Example](../src/br/com/javalab/java11/tls/Tls13Example.java) |
| 11 | ChaCha20 e ChaCha20-Poly1305 | JEP 329 | Definitivo | [ChaCha20Poly1305Example](../src/br/com/javalab/java11/crypto/ChaCha20Poly1305Example.java) |
| 11 | Remoção dos módulos Java EE e CORBA (JAX-WS, JAXB, JAF, Common Annotations, JTA, CORBA) | JEP 320 | Removido | Documentado (remoção de API; não há o que executar) |
| 11 | Nashorn JavaScript Engine deprecated | JEP 335 | Deprecated | Documentado (removido no Java 15 - JEP 372; não existe no JDK 25) |

## Java 12

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 12 | Switch Expressions | JEP 325 | Preview | [SwitchExpressionPreviewExample](../src/br/com/javalab/java12/switchexpressions/SwitchExpressionPreviewExample.java)<br>[SwitchBreakValueExample (legacy, JDK 12)](../legacy/java12/switchexpressions/SwitchBreakValueExample.java) |
| 12 | String.indent e String.transform | — | Definitivo | [StringIndentTransformExample](../src/br/com/javalab/java12/string/StringIndentTransformExample.java) |
| 12 | Files.mismatch | — | Definitivo | [FilesMismatchExample](../src/br/com/javalab/java12/files/FilesMismatchExample.java) |
| 12 | Collectors.teeing | — | Definitivo | [CollectorsTeeingExample](../src/br/com/javalab/java12/collectors/CollectorsTeeingExample.java) |
| 12 | Compact Number Formatting | — | Definitivo | [CompactNumberFormatExample](../src/br/com/javalab/java12/text/CompactNumberFormatExample.java) |
| 12 | JVM Constants API | JEP 334 | Definitivo | [JvmConstantsApiExample](../src/br/com/javalab/java12/constants/JvmConstantsApiExample.java) |
| 12 | CompletableFuture.exceptionallyCompose / exceptionallyAsync | — | Definitivo | [CompletableFutureExceptionallyExample](../src/br/com/javalab/java12/concurrency/CompletableFutureExceptionallyExample.java) |
| 12 | Shenandoah GC | JEP 189 | Experimental | [GcAndCdsObservationExample](../src/br/com/javalab/java12/jvm/GcAndCdsObservationExample.java) |
| 12 | Default CDS Archives | JEP 341 | Produto (JVM) | [GcAndCdsObservationExample](../src/br/com/javalab/java12/jvm/GcAndCdsObservationExample.java) |
| 12 | G1: Abortable Mixed Collections / Promptly Return Unused Memory | JEP 344 / JEP 346 | Produto (JVM) | [GcAndCdsObservationExample](../src/br/com/javalab/java12/jvm/GcAndCdsObservationExample.java) |
| 12 | Microbenchmark Suite | JEP 230 | Definitivo | Documentado (suíte JMH no build do JDK, não é API) |

## Java 13

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 13 | Text Blocks | JEP 355 | Preview | [TextBlockPreviewExample](../src/br/com/javalab/java13/textblocks/TextBlockPreviewExample.java) |
| 13 | String.stripIndent / translateEscapes / formatted | JEP 355 | Preview | [TextBlockStringMethodsExample](../src/br/com/javalab/java13/textblocks/TextBlockStringMethodsExample.java) |
| 13 | Switch Expressions (yield) | JEP 354 | Segunda Preview | [SwitchYieldExample](../src/br/com/javalab/java13/switchexpressions/SwitchYieldExample.java) |
| 13 | Reimplementação da Socket API legada | JEP 353 | Produto (JVM) | [SocketReimplementationExample](../src/br/com/javalab/java13/sockets/SocketReimplementationExample.java) |
| 13 | Dynamic CDS Archives | JEP 350 | Produto (JVM) | [DynamicCdsArchiveExample](../src/br/com/javalab/java13/jvm/DynamicCdsArchiveExample.java) |
| 13 | ZGC: Uncommit Unused Memory | JEP 351 | Produto (JVM) | Documentado (comportamento interno do ZGC; flags no Javadoc de DynamicCdsArchiveExample) |
| 13 | FileSystems.newFileSystem(Path) para ZIP | — | Definitivo | [ZipFileSystemExample](../src/br/com/javalab/java13/zipfs/ZipFileSystemExample.java) |

## Java 14

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 14 | Switch Expressions | JEP 361 | Definitivo | [SwitchExpressionExample](../src/br/com/javalab/java14/switchexpressions/SwitchExpressionExample.java)<br>[SwitchStatementVsExpressionComparison](../src/br/com/javalab/java14/switchexpressions/SwitchStatementVsExpressionComparison.java) |
| 14 | Records | JEP 359 | Preview | [RecordPreviewExample](../src/br/com/javalab/java14/records/RecordPreviewExample.java) |
| 14 | Pattern Matching for instanceof | JEP 305 | Preview | [InstanceofPatternPreviewExample](../src/br/com/javalab/java14/patternmatching/InstanceofPatternPreviewExample.java) |
| 14 | Helpful NullPointerExceptions | JEP 358 | Produto (JVM) | [HelpfulNullPointerExample](../src/br/com/javalab/java14/npe/HelpfulNullPointerExample.java) |
| 14 | Text Blocks (escapes `\s` e `\<nova-linha>`) | JEP 368 | Segunda Preview | [TextBlockEscapesExample](../src/br/com/javalab/java14/textblocks/TextBlockEscapesExample.java) |
| 14 | JFR Event Streaming | JEP 349 | Definitivo | [JfrEventStreamingExample](../src/br/com/javalab/java14/jfr/JfrEventStreamingExample.java) |
| 14 | Foreign-Memory Access API | JEP 370 | Incubadora | [ForeignMemoryAccessExample (legacy, JDK 14)](../legacy/java14/foreignmemory/ForeignMemoryAccessExample.java) |
| 14 | Packaging Tool (jpackage) | JEP 343 | Incubadora | Documentado (ferramenta de linha de comando, não API) |
| 14 | Remoção do CMS Garbage Collector | JEP 363 | Removido | Documentado (remoção de coletor; -XX:+UseConcMarkSweepGC passa a ser ignorada com aviso) |
| 14 | G1 NUMA-aware / ZGC no Windows e macOS | JEP 345 / JEP 365 / JEP 364 | Experimental | Documentado (opções de JVM: -XX:+UseNUMA, -XX:+UseZGC) |

## Java 15

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 15 | Text Blocks | JEP 378 | Definitivo | [TextBlockExample](../src/br/com/javalab/java15/textblocks/TextBlockExample.java)<br>[ConcatenationVsTextBlockComparison](../src/br/com/javalab/java15/textblocks/ConcatenationVsTextBlockComparison.java) |
| 15 | Sealed Classes | JEP 360 | Preview | [SealedClassesPreviewExample](../src/br/com/javalab/java15/sealed/SealedClassesPreviewExample.java) |
| 15 | Records (locais, interfaces seladas, anotações em componentes) | JEP 384 | Segunda Preview | [RecordSecondPreviewExample](../src/br/com/javalab/java15/records/RecordSecondPreviewExample.java) |
| 15 | Pattern Matching for instanceof (escopo das variáveis) | JEP 375 | Segunda Preview | [InstanceofPatternSecondPreviewExample](../src/br/com/javalab/java15/patternmatching/InstanceofPatternSecondPreviewExample.java) |
| 15 | Hidden Classes | JEP 371 | Definitivo | [HiddenClassExample](../src/br/com/javalab/java15/hiddenclasses/HiddenClassExample.java) |
| 15 | EdDSA (Ed25519 / Ed448) | JEP 339 | Definitivo | [EdDsaSignatureExample](../src/br/com/javalab/java15/security/EdDsaSignatureExample.java) |
| 15 | Reimplementação da DatagramSocket API | JEP 373 | Produto (JVM) | [DatagramSocketReimplementationExample](../src/br/com/javalab/java15/datagram/DatagramSocketReimplementationExample.java) |
| 15 | CharSequence.isEmpty / String.formatted definitivo | — | Definitivo | [CharSequenceIsEmptyExample](../src/br/com/javalab/java15/string/CharSequenceIsEmptyExample.java) |
| 15 | ZGC e Shenandoah como produto | JEP 377 / JEP 379 | Produto (JVM) | [JvmChangesObservationExample](../src/br/com/javalab/java15/jvm/JvmChangesObservationExample.java) |
| 15 | Desativação do Biased Locking | JEP 374 | Deprecated | [JvmChangesObservationExample](../src/br/com/javalab/java15/jvm/JvmChangesObservationExample.java) |
| 15 | Remoção do Nashorn | JEP 372 | Removido | [JvmChangesObservationExample](../src/br/com/javalab/java15/jvm/JvmChangesObservationExample.java) |
| 15 | Foreign-Memory Access API (2ª incubadora) | JEP 383 | Incubadora | Documentado (API removida; ver legacy/java14) |

## Java 16

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 16 | Records | JEP 395 | Definitivo | [RecordExample](../src/br/com/javalab/java16/records/RecordExample.java)<br>[ClassVsRecordComparison](../src/br/com/javalab/java16/records/ClassVsRecordComparison.java)<br>[StaticMembersInInnerClassesExample](../src/br/com/javalab/java16/records/StaticMembersInInnerClassesExample.java) |
| 16 | Pattern Matching for instanceof | JEP 394 | Definitivo | [InstanceofPatternExample](../src/br/com/javalab/java16/patternmatching/InstanceofPatternExample.java)<br>[InstanceofVsPatternMatchingComparison](../src/br/com/javalab/java16/patternmatching/InstanceofVsPatternMatchingComparison.java) |
| 16 | Stream.toList | — | Definitivo | [StreamToListExample](../src/br/com/javalab/java16/streams/StreamToListExample.java) |
| 16 | Stream.mapMulti | — | Definitivo | [StreamMapMultiExample](../src/br/com/javalab/java16/streams/StreamMapMultiExample.java) |
| 16 | Sealed Classes | JEP 397 | Segunda Preview | [SealedClassesSecondPreviewExample](../src/br/com/javalab/java16/sealed/SealedClassesSecondPreviewExample.java) |
| 16 | Vector API | JEP 338 | Incubadora | [VectorApiExample](../src/br/com/javalab/java16/vector/VectorApiExample.java) |
| 16 | Unix-Domain Socket Channels | JEP 380 | Definitivo | [UnixDomainSocketExample](../src/br/com/javalab/java16/unixsockets/UnixDomainSocketExample.java) |
| 16 | Strongly Encapsulate JDK Internals by Default | JEP 396 | Definitivo | [StrongEncapsulationExample](../src/br/com/javalab/java16/encapsulation/StrongEncapsulationExample.java) |
| 16 | Warnings for Value-Based Classes | JEP 390 | Definitivo | [ValueBasedClassesWarningExample](../src/br/com/javalab/java16/valuebased/ValueBasedClassesWarningExample.java) |
| 16 | Elastic Metaspace | JEP 387 | Produto (JVM) | [ElasticMetaspaceObservationExample](../src/br/com/javalab/java16/metaspace/ElasticMetaspaceObservationExample.java) |
| 16 | Packaging Tool (jpackage) | JEP 392 | Definitivo | Documentado (ferramenta de linha de comando, não API) |
| 16 | Foreign Linker API / Foreign-Memory Access (3ª incubadora) | JEP 389 / JEP 393 | Incubadora | Documentado (APIs removidas; evoluíram para a FFM API definitiva no Java 22) |

## Java 17

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 17 | Sealed Classes (+ comparação hierarquia aberta x selada) | JEP 409 | Definitivo | [SealedClassesExample](../src/br/com/javalab/java17/sealedclasses/SealedClassesExample.java)<br>[RegularVsSealedClassesComparison](../src/br/com/javalab/java17/sealedclasses/RegularVsSealedClassesComparison.java) |
| 17 | Pattern Matching for switch (guards `&&` e parenthesized patterns) | JEP 406 | Preview | [PatternMatchingSwitchPreview17 (legacy, JDK 17)](../legacy/java17/patternswitch/PatternMatchingSwitchPreview17.java) |
| 17 | Enhanced Pseudo-Random Number Generators | JEP 356 | Definitivo | [EnhancedPseudoRandomGeneratorsExample](../src/br/com/javalab/java17/prng/EnhancedPseudoRandomGeneratorsExample.java) |
| 17 | Strongly Encapsulate JDK Internals | JEP 403 | Definitivo | [StrongEncapsulationExample](../src/br/com/javalab/java17/encapsulation/StrongEncapsulationExample.java) |
| 17 | Context-Specific Deserialization Filters | JEP 415 | Definitivo | [DeserializationFilterExample](../src/br/com/javalab/java17/security/DeserializationFilterExample.java) |
| 17 | Deprecate the Security Manager for Removal | JEP 411 | Deprecated | [SecurityManagerDeprecationExample](../src/br/com/javalab/java17/security/SecurityManagerDeprecationExample.java) |
| 17 | HexFormat | — | Definitivo | [HexFormatExample](../src/br/com/javalab/java17/hexformat/HexFormatExample.java) |
| 17 | Restore Always-Strict Floating-Point Semantics | JEP 306 | Definitivo | [AlwaysStrictFloatingPointExample](../src/br/com/javalab/java17/strictmath/AlwaysStrictFloatingPointExample.java) |
| 17 | macOS Rendering Pipeline (Metal) | JEP 382 | Produto (JVM) | Documentado (pipeline gráfico interno do Java2D no macOS) |
| 17 | Remove RMI Activation | JEP 407 | Removido | Documentado (API removida; nada a executar) |
| 17 | Remove the Experimental AOT and JIT Compiler | JEP 410 | Removido | Documentado (jaotc e Graal JIT removidos do JDK) |

## Java 18

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 18 | Simple Web Server (`jwebserver` / SimpleFileServer) | JEP 408 | Definitivo | [SimpleWebServerExample](../src/br/com/javalab/java18/simplewebserver/SimpleWebServerExample.java) |
| 18 | UTF-8 by Default | JEP 400 | Definitivo | [Utf8ByDefaultExample](../src/br/com/javalab/java18/utf8default/Utf8ByDefaultExample.java) |
| 18 | Code Snippets in Java API Documentation (`@snippet`) | JEP 413 | Definitivo | [CodeSnippetsExample](../src/br/com/javalab/java18/javadocsnippets/CodeSnippetsExample.java) |
| 18 | Internet-Address Resolution SPI | JEP 418 | Definitivo | [InetAddressResolverSpiExample](../src/br/com/javalab/java18/inetaddressspi/InetAddressResolverSpiExample.java) |
| 18 | Vector API (3ª incubadora: máscaras e reduções) | JEP 417 | Incubadora | [VectorMaskReductionExample](../src/br/com/javalab/java18/vectorapi/VectorMaskReductionExample.java) |
| 18 | Pattern Matching for switch (2ª Preview: dominância, sealed genérico) | JEP 420 | Segunda Preview | [PatternMatchingSwitchSecondPreview18 (legacy, JDK 18)](../legacy/java18/patternswitch/PatternMatchingSwitchSecondPreview18.java) |
| 18 | Deprecate Finalization for Removal (Cleaner como alternativa) | JEP 421 | Deprecated | [CleanerInsteadOfFinalizeExample](../src/br/com/javalab/java18/finalization/CleanerInsteadOfFinalizeExample.java) |
| 18 | Reimplement Core Reflection with Method Handles | JEP 416 | Definitivo | Documentado (mudança interna; API de reflexão inalterada) |
| 18 | Foreign Function & Memory API (2ª incubadora) | JEP 419 | Incubadora | Documentado (API intermediária; ver legacy/java19 e FFM definitiva no 22) |

## Java 19

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 19 | Virtual Threads | JEP 425 | Preview | [VirtualThreadsPreviewExample](../src/br/com/javalab/java19/virtualthreads/VirtualThreadsPreviewExample.java) |
| 19 | Record Patterns (named record patterns) | JEP 405 | Preview | [NamedRecordPatternsPreview19 (legacy, JDK 19)](../legacy/java19/recordpatterns/NamedRecordPatternsPreview19.java) |
| 19 | Pattern Matching for switch (3ª Preview: guards `when`, semântica de null) | JEP 427 | Terceira Preview | [PatternSwitchWhenGuardsExample](../src/br/com/javalab/java19/patternswitch/PatternSwitchWhenGuardsExample.java) |
| 19 | Structured Concurrency (`jdk.incubator.concurrent`) | JEP 428 | Incubadora | [StructuredConcurrencyIncubator19 (legacy, JDK 19)](../legacy/java19/structuredconcurrency/StructuredConcurrencyIncubator19.java) |
| 19 | Foreign Function & Memory API (`MemorySession`) | JEP 424 | Preview | [ForeignFunctionMemoryPreview19 (legacy, JDK 19)](../legacy/java19/foreignmemory/ForeignFunctionMemoryPreview19.java) |
| 19 | Vector API (4ª incubadora) | JEP 426 | Incubadora | Documentado (sem mudança didática relevante; ver java18/vectorapi) |
| 19 | Linux/RISC-V Port | JEP 422 | Produto (JVM) | Documentado (porte para nova arquitetura) |

## Java 20

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 20 | Virtual Threads (2ª Preview: Thread.Builder, ThreadLocal, 10 mil tarefas) | JEP 436 | Segunda Preview | [VirtualThreadsSecondPreviewExample](../src/br/com/javalab/java20/virtualthreads/VirtualThreadsSecondPreviewExample.java) |
| 20 | Record Patterns (2ª Preview: inferência de tipos; patterns no enhanced for) | JEP 432 | Segunda Preview | [GenericRecordPatternInferenceExample](../src/br/com/javalab/java20/recordpatterns/GenericRecordPatternInferenceExample.java)<br>[RecordPatternsInEnhancedFor20 (legacy, JDK 20)](../legacy/java20/recordpatterns/RecordPatternsInEnhancedFor20.java) |
| 20 | Pattern Matching for switch (4ª Preview: exaustividade, MatchException) | JEP 433 | Quarta Preview | [ExhaustiveSwitchMatchExceptionExample](../src/br/com/javalab/java20/patternswitch/ExhaustiveSwitchMatchExceptionExample.java) |
| 20 | Scoped Values (`jdk.incubator.concurrent`) | JEP 429 | Incubadora | [ScopedValuesIncubator20 (legacy, JDK 20)](../legacy/java20/scopedvalues/ScopedValuesIncubator20.java) |
| 20 | Structured Concurrency (2ª incubadora: herança de Scoped Values) | JEP 437 | Incubadora | [StructuredConcurrencySecondIncubator20 (legacy, JDK 20)](../legacy/java20/structuredconcurrency/StructuredConcurrencySecondIncubator20.java) |
| 20 | Foreign Function & Memory API (2ª Preview) | JEP 434 | Segunda Preview | Documentado (API intermediária; ver legacy/java19 e FFM definitiva no 22) |
| 20 | Vector API (5ª incubadora) | JEP 438 | Incubadora | Documentado (sem mudança didática relevante) |

## Java 21

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 21 | Virtual Threads (+ comparação threads de plataforma x virtual) | JEP 444 | Definitivo | [VirtualThreadsExample](../src/br/com/javalab/java21/virtualthreads/VirtualThreadsExample.java)<br>[PlatformVsVirtualThreadsComparison](../src/br/com/javalab/java21/virtualthreads/PlatformVsVirtualThreadsComparison.java) |
| 21 | Record Patterns (aninhados) | JEP 440 | Definitivo | [RecordPatternsExample](../src/br/com/javalab/java21/recordpatterns/RecordPatternsExample.java) |
| 21 | Pattern Matching for switch (case null, guards, sealed, enum qualificado) | JEP 441 | Definitivo | [PatternMatchingSwitchExample](../src/br/com/javalab/java21/patternswitch/PatternMatchingSwitchExample.java) |
| 21 | Sequenced Collections | JEP 431 | Definitivo | [SequencedCollectionsExample](../src/br/com/javalab/java21/sequencedcollections/SequencedCollectionsExample.java) |
| 21 | String Templates | JEP 430 | Retirado | [StringTemplatesPreview21 (legacy, JDK 21)](../legacy/java21/stringtemplates/StringTemplatesPreview21.java)<br>[StringTemplatesAlternativesExample](../src/br/com/javalab/java21/stringtemplates/StringTemplatesAlternativesExample.java) |
| 21 | Scoped Values | JEP 446 | Preview | [ScopedValuesExample](../src/br/com/javalab/java21/scopedvalues/ScopedValuesExample.java)<br>[ScopedValuesStaticMethodsPreview21 (legacy, JDK 21)](../legacy/java21/scopedvalues/ScopedValuesStaticMethodsPreview21.java) |
| 21 | Structured Concurrency (`ShutdownOnFailure`, `Subtask`) | JEP 453 | Preview | [StructuredConcurrencyPreview21 (legacy, JDK 21)](../legacy/java21/structuredconcurrency/StructuredConcurrencyPreview21.java) |
| 21 | Unnamed Patterns and Variables (`_`) | JEP 443 | Preview | [UnnamedPatternsAndVariablesExample](../src/br/com/javalab/java21/unnamed/UnnamedPatternsAndVariablesExample.java) |
| 21 | Unnamed Classes and Instance Main Methods | JEP 445 | Preview | [UnnamedClassInstanceMain21 (legacy, JDK 21)](../legacy/java21/unnamedclasses/UnnamedClassInstanceMain21.java) |
| 21 | Key Encapsulation Mechanism API (DHKEM X25519) | JEP 452 | Definitivo | [KeyEncapsulationExample](../src/br/com/javalab/java21/kem/KeyEncapsulationExample.java) |
| 21 | Generational ZGC | JEP 439 | Produto (JVM) | [GenerationalZgcObservationExample](../src/br/com/javalab/java21/generationalzgc/GenerationalZgcObservationExample.java) |
| 21 | Novos métodos: StringBuilder.repeat, Character.isEmoji, Math.clamp, String.indexOf(ch, from, to), splitWithDelimiters, List.reversed | — | Definitivo | [NewMethodsJava21Example](../src/br/com/javalab/java21/newmethods/NewMethodsJava21Example.java) |
| 21 | Prepare to Disallow the Dynamic Loading of Agents | JEP 451 | Definitivo | Documentado (aviso da JVM ao carregar agente dinamicamente) |
| 21 | Deprecate the Windows 32-bit x86 Port for Removal | JEP 449 | Deprecated | Documentado (porte de plataforma) |
| 21 | Foreign Function & Memory API (3ª Preview) | JEP 442 | Terceira Preview | Documentado (API intermediária; FFM definitiva no 22) |
| 21 | Vector API (6ª incubadora) | JEP 448 | Incubadora | Documentado (sem mudança didática relevante) |

## Java 22

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 22 | Foreign Function & Memory API (Arena, MemorySegment, downcall strlen, upcall qsort) | JEP 454 | Definitivo | [ForeignFunctionMemoryExample](../src/br/com/javalab/java22/ffm/ForeignFunctionMemoryExample.java) |
| 22 | Unnamed Variables & Patterns (`_`) | JEP 456 | Definitivo | [UnnamedVariablesExample](../src/br/com/javalab/java22/unnamed/UnnamedVariablesExample.java) |
| 22 | Launch Multi-File Source-Code Programs | JEP 458 | Definitivo | [MultiFileLauncherExample](../src/br/com/javalab/java22/multifile/MultiFileLauncherExample.java)<br>[PriceCalculator (auxiliar)](../src/br/com/javalab/java22/multifile/PriceCalculator.java) |
| 22 | Stream Gatherers (windowFixed, windowSliding, fold) | JEP 461 | Preview | [StreamGatherersPreviewExample](../src/br/com/javalab/java22/gatherers/StreamGatherersPreviewExample.java) |
| 22 | Class-File API (gerar classe em memória e carregar) | JEP 457 | Preview | [ClassFileGenerateExample](../src/br/com/javalab/java22/classfile/ClassFileGenerateExample.java) |
| 22 | Statements before super(...) | JEP 447 | Preview | [StatementsBeforeSuperExample](../src/br/com/javalab/java22/constructors/StatementsBeforeSuperExample.java) |
| 22 | Scoped Values | JEP 464 | Segunda Preview | [ScopedValuesSecondPreviewExample](../src/br/com/javalab/java22/scopedvalues/ScopedValuesSecondPreviewExample.java) |
| 22 | Structured Concurrency (ShutdownOnFailure) | JEP 462 | Segunda Preview | [StructuredConcurrencySecondPreviewExample (legacy, JDK 22)](../legacy/java22/structuredconcurrency/StructuredConcurrencySecondPreviewExample.java) |
| 22 | String Templates | JEP 459 | Segunda Preview | Documentado (retirado no 23; ver exemplos do Java 21) |
| 22 | Implicitly Declared Classes and Instance Main Methods | JEP 463 | Segunda Preview | Documentado (forma atual no Java 25, JEP 512) |
| 22 | Vector API | JEP 460 | Incubadora | Documentado (7ª incubadora; exemplo no Java 25) |
| 22 | Region Pinning for G1 | JEP 423 | Produto (JVM) | Documentado (comportamento interno do G1 em regiões críticas JNI) |

## Java 23

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 23 | Markdown Documentation Comments (`///`) | JEP 467 | Definitivo | [MarkdownDocCommentsExample](../src/br/com/javalab/java23/markdowndoc/MarkdownDocCommentsExample.java) |
| 23 | Stream Gatherers (gatherer customizado, scan, mapConcurrent) | JEP 473 | Segunda Preview | [CustomGathererExample](../src/br/com/javalab/java23/gatherers/CustomGathererExample.java) |
| 23 | Module Import Declarations (`import module java.base;`) | JEP 476 | Preview | [ModuleImportExample](../src/br/com/javalab/java23/moduleimport/ModuleImportExample.java) |
| 23 | Primitive Types in Patterns, instanceof, and switch | JEP 455 | Preview | [PrimitivePatternsExample](../src/br/com/javalab/java23/primitivepatterns/PrimitivePatternsExample.java) |
| 23 | Class-File API (leitura e análise de bytecode) | JEP 466 | Segunda Preview | [ClassFileReadExample](../src/br/com/javalab/java23/classfile/ClassFileReadExample.java) |
| 23 | Flexible Constructor Bodies (campos antes de super) | JEP 482 | Segunda Preview | [FlexibleConstructorBodiesExample](../src/br/com/javalab/java23/constructors/FlexibleConstructorBodiesExample.java) |
| 23 | Scoped Values (CallableOp, callWhere/runWhere) | JEP 481 | Terceira Preview | [ScopedValuesThirdPreviewExample (legacy, JDK 23)](../legacy/java23/scopedvalues/ScopedValuesThirdPreviewExample.java) |
| 23 | Structured Concurrency (ShutdownOnSuccess) | JEP 480 | Terceira Preview | [StructuredConcurrencyThirdPreviewExample (legacy, JDK 23)](../legacy/java23/structuredconcurrency/StructuredConcurrencyThirdPreviewExample.java) |
| 23 | ZGC: Generational Mode by Default | JEP 474 | Produto (JVM) | [GenerationalZgcObservation](../src/br/com/javalab/java23/zgc/GenerationalZgcObservation.java) |
| 23 | Deprecate the Memory-Access Methods in sun.misc.Unsafe for Removal | JEP 471 | Deprecated | Documentado (API interna; substitutos: VarHandle e FFM) |
| 23 | String Templates (retirados) | — | Retirado | Documentado (removidos do JDK 23 sem JEP; ver Java 21) |
| 23 | Implicitly Declared Classes and Instance Main Methods (java.io.IO) | JEP 477 | Terceira Preview | Documentado (ver legado do Java 24 e forma final no Java 25) |
| 23 | Vector API | JEP 469 | Incubadora | Documentado (8ª incubadora; exemplo no Java 25) |

## Java 24

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 24 | Stream Gatherers (comparação com laço manual, andThen) | JEP 485 | Definitivo | [StreamGatherersFinalExample](../src/br/com/javalab/java24/gatherers/StreamGatherersFinalExample.java) |
| 24 | Class-File API (transformação: remover/adicionar método) | JEP 484 | Definitivo | [ClassFileTransformExample](../src/br/com/javalab/java24/classfile/ClassFileTransformExample.java) |
| 24 | Flexible Constructor Bodies (prólogo antes de this(...), records) | JEP 492 | Terceira Preview | [FlexibleConstructorThisDelegationExample](../src/br/com/javalab/java24/constructors/FlexibleConstructorThisDelegationExample.java) |
| 24 | Module Import Declarations (ambiguidade, transitividade) | JEP 494 | Segunda Preview | [ModuleImportAmbiguityExample](../src/br/com/javalab/java24/moduleimport/ModuleImportAmbiguityExample.java) |
| 24 | Simple Source Files and Instance Main Methods (`println` sem qualificação) | JEP 495 | Quarta Preview | [SimpleSourceFileExample (legacy, JDK 24)](../legacy/java24/simplesourcefiles/SimpleSourceFileExample.java) |
| 24 | Scoped Values (API só fluente) | JEP 487 | Quarta Preview | [ScopedValuesFourthPreviewExample](../src/br/com/javalab/java24/scopedvalues/ScopedValuesFourthPreviewExample.java) |
| 24 | Structured Concurrency (política própria, joinUntil) | JEP 499 | Quarta Preview | [StructuredConcurrencyFourthPreviewExample (legacy, JDK 24)](../legacy/java24/structuredconcurrency/StructuredConcurrencyFourthPreviewExample.java) |
| 24 | Key Derivation Function API (HKDF) | JEP 478 | Preview | [KeyDerivationPreviewExample](../src/br/com/javalab/java24/kdf/KeyDerivationPreviewExample.java) |
| 24 | Quantum-Resistant Module-Lattice-Based KEM (ML-KEM) | JEP 496 | Definitivo | [MlKemExample](../src/br/com/javalab/java24/pqc/MlKemExample.java) |
| 24 | Quantum-Resistant Module-Lattice-Based Digital Signature (ML-DSA) | JEP 497 | Definitivo | [MlDsaExample](../src/br/com/javalab/java24/pqc/MlDsaExample.java) |
| 24 | Synchronize Virtual Threads without Pinning | JEP 491 | Definitivo | [SynchronizedWithoutPinningExample](../src/br/com/javalab/java24/virtualthreads/SynchronizedWithoutPinningExample.java) |
| 24 | Ahead-of-Time Class Loading & Linking | JEP 483 | Produto (JVM) | [AheadOfTimeClassLoadingExample](../src/br/com/javalab/java24/aot/AheadOfTimeClassLoadingExample.java) |
| 24 | Permanently Disable the Security Manager | JEP 486 | Removido | [SecurityManagerDisabledExample](../src/br/com/javalab/java24/securitymanager/SecurityManagerDisabledExample.java) |
| 24 | Primitive Types in Patterns, instanceof, and switch | JEP 488 | Segunda Preview | Documentado (sem mudanças; exemplos no Java 23 e 25) |
| 24 | Compact Object Headers | JEP 450 | Experimental | Documentado (exemplo no Java 25, JEP 519) |
| 24 | Prepare to Restrict the Use of JNI | JEP 472 | Produto (JVM) | Documentado (avisos ao carregar JNI; `--enable-native-access`) |
| 24 | Warn upon Use of Memory-Access Methods in sun.misc.Unsafe | JEP 498 | Deprecated | Documentado (aviso em tempo de execução; API interna) |
| 24 | Generational Shenandoah | JEP 404 | Experimental | Documentado (Shenandoah ausente no Oracle JDK usado no projeto) |
| 24 | ZGC: Remove the Non-Generational Mode | JEP 490 | Removido | Documentado (ver GenerationalZgcObservation do Java 23) |
| 24 | Linking Run-Time Images without JMODs | JEP 493 | Produto (JVM) | Documentado (opção de build do JDK para o jlink) |
| 24 | Vector API | JEP 489 | Incubadora | Documentado (9ª incubadora; exemplo no Java 25) |

## Java 25

| Java | Funcionalidade | JEP | Status | Classe |
|---|---|---|---|---|
| 25 | Scoped Values (+ comparação ThreadLocal x ScopedValue) | JEP 506 | Definitivo | [ScopedValuesExample](../src/br/com/javalab/java25/scopedvalues/ScopedValuesExample.java)<br>[ThreadLocalVsScopedValueComparison](../src/br/com/javalab/java25/scopedvalues/ThreadLocalVsScopedValueComparison.java) |
| 25 | Compact Source Files and Instance Main Methods (java.lang.IO) | JEP 512 | Definitivo | [HelloCompact (standalone)](../standalone/java25-compact-source/HelloCompact.java)<br>[InstanceMainExample](../src/br/com/javalab/java25/instancemain/InstanceMainExample.java) |
| 25 | Flexible Constructor Bodies | JEP 513 | Definitivo | [FlexibleConstructorBodiesFinalExample](../src/br/com/javalab/java25/constructors/FlexibleConstructorBodiesFinalExample.java) |
| 25 | Module Import Declarations | JEP 511 | Definitivo | [ModuleImportFinalExample](../src/br/com/javalab/java25/moduleimport/ModuleImportFinalExample.java) |
| 25 | Primitive Types in Patterns, instanceof, and switch (conversões exatas) | JEP 507 | Terceira Preview | [PrimitivePatternsThirdPreviewExample](../src/br/com/javalab/java25/primitivepatterns/PrimitivePatternsThirdPreviewExample.java) |
| 25 | Structured Concurrency (open + Joiner) | JEP 505 | Quinta Preview | [StructuredConcurrencyFifthPreviewExample](../src/br/com/javalab/java25/structuredconcurrency/StructuredConcurrencyFifthPreviewExample.java) |
| 25 | Stable Values | JEP 502 | Preview | [StableValuesExample](../src/br/com/javalab/java25/stablevalues/StableValuesExample.java) |
| 25 | Vector API (10ª incubadora) | JEP 508 | Incubadora | [VectorDotProductExample](../src/br/com/javalab/java25/vectorapi/VectorDotProductExample.java) |
| 25 | Key Derivation Function API (extract/expand + AES-GCM) | JEP 510 | Definitivo | [KeyDerivationExample](../src/br/com/javalab/java25/kdf/KeyDerivationExample.java) |
| 25 | Compact Object Headers | JEP 519 | Produto (JVM) | [CompactObjectHeadersObservation](../src/br/com/javalab/java25/compactheaders/CompactObjectHeadersObservation.java) |
| 25 | Ahead-of-Time Command-Line Ergonomics (`-XX:AOTCacheOutput`) | JEP 514 | Produto (JVM) | [AotCommandLineErgonomicsExample](../src/br/com/javalab/java25/aot/AotCommandLineErgonomicsExample.java) |
| 25 | Ahead-of-Time Method Profiling | JEP 515 | Produto (JVM) | [AotMethodProfilingExample](../src/br/com/javalab/java25/aot/AotMethodProfilingExample.java) |
| 25 | PEM Encodings of Cryptographic Objects | JEP 470 | Preview | [PemEncodingsExample](../src/br/com/javalab/java25/pem/PemEncodingsExample.java) |
| 25 | JFR Method Timing & Tracing | JEP 520 | Definitivo | [MethodTimingExample](../src/br/com/javalab/java25/jfr/MethodTimingExample.java) |
| 25 | JFR CPU-Time Profiling | JEP 509 | Experimental | Documentado (só Linux; evento `jdk.CPUTimeSample`) |
| 25 | JFR Cooperative Sampling | JEP 518 | Produto (JVM) | Documentado (mudança interna na amostragem de pilhas do JFR) |
| 25 | Generational Shenandoah | JEP 521 | Produto (JVM) | Documentado (Shenandoah ausente no Oracle JDK usado no projeto) |
| 25 | Remove the 32-bit x86 Port | JEP 503 | Removido | Documentado (remoção de plataforma) |

