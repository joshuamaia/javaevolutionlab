# Java Evolution Lab — do Java 5 ao Java 25

Biblioteca de **exemplos práticos e executáveis** que mostra, versão por versão, como a linguagem Java e suas APIs evoluíram do **Java 5 (2004)** ao **Java 25 (2025)**.

Cada funcionalidade tem **sua própria classe**, com `main` próprio, comentários em português e saída no console. Basta escolher uma pasta, abrir a classe e executar.

- 100% **Java SE puro**: sem Maven, Gradle, Spring, Lombok ou bibliotecas externas.
- Organizado **cronologicamente**: `java05`, `java06`, ..., `java25`.
- Inclui versões **LTS e não-LTS**, recursos **Preview**, **Incubadora**, **Experimentais** e os que foram **retirados**.
- Exemplos históricos respeitam a **sintaxe da época**. Código que não compila mais no JDK atual fica isolado em `legacy/`.

---

## Sumário

1. [Objetivo educacional](#objetivo-educacional)
2. [Pré-requisitos](#pré-requisitos)
3. [Configuração do IntelliJ IDEA](#configuração-do-intellij-idea)
4. [Estrutura das pastas](#estrutura-das-pastas)
5. [Como executar os exemplos](#como-executar-os-exemplos)
6. [Recursos Preview](#recursos-preview)
7. [Recursos Incubadores](#recursos-incubadores)
8. [Usando diferentes JDKs](#usando-diferentes-jdks)
9. [Linha do tempo da evolução do Java](#linha-do-tempo-da-evolução-do-java)
10. [Versões LTS](#versões-lts)
11. [Comparações "antes × depois"](#comparações-antes--depois)
12. [Referências oficiais](#referências-oficiais)

Documentos complementares:

- [docs/java-timeline.md](docs/java-timeline.md): linha do tempo detalhada, versão por versão.
- [docs/feature-matrix.md](docs/feature-matrix.md): tabela Java × Funcionalidade × JEP × Status × Classe.
- [docs/execution-guide.md](docs/execution-guide.md): guia completo de execução e casos especiais.

---

## Objetivo educacional

- Mostrar **o que** mudou em cada versão e **por que** mudou (o benefício).
- Comparar a **abordagem antiga** com a **nova** sempre que houver uma evolução clara.
- Acompanhar a trajetória dos recursos que passaram por várias rodadas de **Preview** (Records, Pattern Matching, Virtual Threads, Scoped Values, Structured Concurrency...) até se tornarem definitivos, ou até serem **retirados** (String Templates).
- Servir de **consulta rápida**: cada arquivo começa com um cabeçalho padronizado:

```java
/**
 * Java 16 - Records (JEP 395)
 *
 * Status: Definitivo. Histórico: Preview no Java 14 (JEP 359), segunda Preview no Java 15 (JEP 384).
 * O que é / Benefício / Antes ...
 * Execução (JDK 25): java src/br/com/javalab/java16/records/RecordExample.java
 */
```

## Pré-requisitos

| Ferramenta | Versão | Observação |
|---|---|---|
| JDK | **25** | JDK principal do projeto (Oracle JDK, OpenJDK, Temurin, Zulu...) |
| IntelliJ IDEA | 2025.2 ou superior | Community ou Ultimate. Versões anteriores não conhecem o *language level* 25 |
| JDKs antigos (opcional) | 12, 14, 17, 19–24 | Somente para os exemplos de `legacy/` |

Nada mais precisa ser instalado.

## Configuração do IntelliJ IDEA

O repositório já contém a configuração do projeto (`.idea/` + `javaevolutionlab.iml`):

1. `File > Open...` → selecione a pasta do projeto.
2. Se pedido, defina o SDK: `File > Project Structure > Project > SDK = 25`.
3. Confira (já configurado):
   - **Language level**: `25 (Preview)`;
   - **Sources Root**: `src/`;
   - **Compiler output**: `out/`;
   - **Javac options** (`Settings > Build > Compiler > Java Compiler`): `--add-modules jdk.incubator.vector`.
4. Abra qualquer classe de exemplo e clique em ▶ ao lado do `main`.

Não há sistema de build externo: o IntelliJ compila com o próprio `javac` do JDK.

## Estrutura das pastas

```text
java-evolution-lab/
├── README.md
├── javaevolutionlab.iml          # módulo IntelliJ (Java puro)
├── .idea/                        # configuração do projeto + run configurations
├── docs/
│   ├── java-timeline.md
│   ├── feature-matrix.md
│   └── execution-guide.md
├── src/                          # Sources Root: tudo aqui compila e roda no JDK 25
│   └── br/com/javalab/
│       ├── java05/  generics/  enums/  annotations/  concurrency/ ...
│       ├── java06/  ...
│       ├── java07/  ...
│       ├── java08/  lambda/  streams/  optional/  datetime/  completablefuture/ ...
│       ├── ...
│       └── java25/  scopedvalues/  stablevalues/  structuredconcurrency/ ...
├── legacy/                       # sintaxe/API original que NÃO compila no JDK 25
│   └── javaNN/<funcionalidade>/Arquivo.java
└── standalone/                   # exemplos com estrutura própria (módulos, arquivos compactos)
```

Convenções:

- Pacote: `br.com.javalab.javaNN.<funcionalidade>`.
- Nomes de classes, métodos e variáveis em **inglês**; comentários e explicações em **português**.
- Cada classe é **independente**: não depende de outras classes de exemplo.
- Comparações "antes × depois" usam o sufixo `Comparison` (ex.: `ClassVsRecordComparison`).

## Como executar os exemplos

**IntelliJ:** abra a classe e clique em ▶.

**Terminal (modo código-fonte, Java 11+):**

```bash
java src/br/com/javalab/java16/records/RecordExample.java
```

**Terminal (compilação explícita):**

```bash
javac --release 25 --enable-preview --add-modules jdk.incubator.vector -d out $(find src -name "*.java")
java --enable-preview -cp out br.com.javalab.java16.records.RecordExample
```

Os detalhes, incluindo PowerShell, módulos, legacy e problemas comuns, estão em [docs/execution-guide.md](docs/execution-guide.md).

## Recursos Preview

Recursos Preview estão completos, mas ainda podem mudar; precisam ser habilitados explicitamente:

```bash
java --enable-preview --source 25 Arquivo.java                     # modo código-fonte
javac --release 25 --enable-preview -d out Arquivo.java            # compilação
java --enable-preview -cp out pacote.Classe                        # execução
```

No IntelliJ, o *language level* `25 (Preview)` faz isso automaticamente.

Recursos Preview no JDK 25 cobertos pelo projeto: Structured Concurrency (JEP 505), Stable Values (JEP 502), Primitive Types in Patterns (JEP 507) e PEM Encodings (JEP 470).

> Um recurso Preview só funciona no **mesmo** JDK que o define. Por isso, Previews antigos cuja sintaxe mudou (ex.: `break valor` no switch do Java 12) estão em `legacy/` e exigem o JDK da época.

## Recursos Incubadores

APIs incubadoras ficam em módulos `jdk.incubator.*`, que não são resolvidos por padrão:

```bash
javac --add-modules jdk.incubator.vector ...
java  --add-modules jdk.incubator.vector ...
```

No JDK 25, o único incubador é a **Vector API** (JEP 508, décima incubadora). Incubadores históricos que já não existem (`jdk.incubator.foreign` do Java 14, `jdk.incubator.concurrent` dos Java 19/20) estão em `legacy/`, com o JDK e as flags necessárias indicados no cabeçalho.

## Usando diferentes JDKs

- Os exemplos de `src/` são executados no **JDK 25**, inclusive os históricos (Java 5 ao Java 24), pois o Java mantém compatibilidade retroativa.
- Os exemplos de `legacy/` informam no cabeçalho qual JDK instalar e o comando exato. Exemplo:

  ```bash
  ~/.jdks/ms-21.0.12/bin/java --enable-preview --source 21 legacy/java21/stringtemplates/StringTemplatesPreview21.java
  ```

- Para baixar JDKs antigos: IntelliJ (`Project Structure > SDKs > + > Download JDK`), <https://jdk.java.net/archive/> ou SDKMAN!.
- Veja [docs/execution-guide.md#4-exemplos-legacy--jdks-antigos](docs/execution-guide.md#4-exemplos-legacy--jdks-antigos).

## Linha do tempo da evolução do Java

| Versão | Ano | LTS | Destaques |
|---|---|---|---|
| Java 5 | 2004 | — | Generics, enums, annotations, autoboxing, for-each, varargs, `java.util.concurrent` |
| Java 6 | 2006 | — | Compiler API, Scripting API, JDBC 4.0, `NavigableMap`/`Deque`, `ServiceLoader`, `Console` |
| Java 7 | 2011 | — | Project Coin (diamond, try-with-resources, multi-catch, switch em String), NIO.2, Fork/Join |
| Java 8 | 2014 | ✔ | Lambdas, Streams, `Optional`, `java.time`, default methods, `CompletableFuture` |
| Java 9 | 2017 | — | Módulos (JPMS), JShell, `List.of`, Flow API, `StackWalker`, `ProcessHandle` |
| Java 10 | 2018 | — | `var`, `copyOf`, AppCDS, Full GC paralelo no G1 |
| Java 11 | 2018 | ✔ | `HttpClient`, novos métodos de `String`, `java Arquivo.java`, Flight Recorder |
| Java 12 | 2019 | — | Switch Expressions (Preview), `Collectors.teeing`, Shenandoah |
| Java 13 | 2019 | — | Text Blocks (Preview), `yield` |
| Java 14 | 2020 | — | Switch Expressions definitivas, Records (Preview), Helpful NPEs |
| Java 15 | 2020 | — | Text Blocks, Sealed (Preview), Hidden Classes, EdDSA, ZGC/Shenandoah produção |
| Java 16 | 2021 | — | Records, Pattern Matching para `instanceof`, `Stream.toList`, Vector API (Incubadora) |
| Java 17 | 2021 | ✔ | Sealed Classes, novos PRNGs, encapsulamento forte do JDK |
| Java 18 | 2022 | — | UTF-8 por padrão, Simple Web Server, `@snippet` |
| Java 19 | 2022 | — | Virtual Threads (Preview), Record Patterns (Preview), FFM (Preview) |
| Java 20 | 2023 | — | Scoped Values (Incubadora), segundas Previews |
| Java 21 | 2023 | ✔ | Virtual Threads, Record Patterns, Pattern Matching para switch, Sequenced Collections |
| Java 22 | 2024 | — | FFM API, variáveis sem nome `_`, multi-file source launch, Stream Gatherers (Preview) |
| Java 23 | 2024 | — | Markdown em Javadoc, `import module` (Preview), ZGC generacional padrão |
| Java 24 | 2025 | — | Stream Gatherers, Class-File API, ML-KEM/ML-DSA, virtual threads sem *pinning* |
| Java 25 | 2025 | ✔ | Scoped Values, arquivos compactos + `void main()`, construtores flexíveis, `import module`, KDF, cabeçalhos compactos |

Detalhes em [docs/java-timeline.md](docs/java-timeline.md).

## Versões LTS

Desde o Java 9 (2017), há uma versão nova a cada **seis meses** (março e setembro). Algumas são designadas **LTS (Long-Term Support)** pelos fornecedores: inicialmente a cada três anos e, a partir do Java 17, a cada **dois anos**.

| LTS | Lançamento | Marco |
|---|---|---|
| Java 8 | Mar/2014 | Programação funcional (lambdas/streams) |
| Java 11 | Set/2018 | Primeira LTS no modelo semestral; `HttpClient` |
| Java 17 | Set/2021 | Records, Sealed, Pattern Matching para `instanceof` |
| Java 21 | Set/2023 | Virtual Threads, Pattern Matching para switch, Record Patterns |
| Java 25 | Set/2025 | Scoped Values, arquivos compactos, construtores flexíveis, AOT |

Versões não-LTS são importantes para estudo: é nelas que os recursos aparecem como Preview e amadurecem.

## Comparações "antes × depois"

| Comparação | Classe |
|---|---|
| Classes tradicionais × Records | `java16/records/ClassVsRecordComparison` |
| Threads tradicionais × Virtual Threads | `java21/virtualthreads/PlatformVsVirtualThreadsComparison` |
| Switch tradicional × Switch Expressions | `java14/switchexpressions/SwitchStatementVsExpressionComparison` |
| `instanceof` tradicional × Pattern Matching | `java16/patternmatching/InstanceofVsPatternMatchingComparison` |
| `for` tradicional × Streams | `java08/streams/ForLoopVsStreamComparison` |
| `Future` × `CompletableFuture` | `java08/completablefuture/FutureVsCompletableFutureComparison` |
| `ThreadLocal` × Scoped Values | `java25/scopedvalues/ThreadLocalVsScopedValueComparison` |
| Coleções mutáveis × imutáveis | `java09/collections/MutableVsImmutableCollectionsComparison` |
| Concatenação × Text Blocks | `java15/textblocks/ConcatenationVsTextBlockComparison` |
| Classes comuns × Sealed Classes | `java17/sealedclasses/RegularVsSealedClassesComparison` |
| `java.io.File` × NIO.2 | `java07/nio2/FileVsNio2Comparison` |
| `HttpURLConnection` × `HttpClient` | `java11/httpclient/HttpUrlConnectionVsHttpClientComparison` |

## Conteúdo e validação

| Local | Conteúdo |
|---|---|
| `src/` | 266 arquivos (265 exemplos executáveis + 1 classe auxiliar do exemplo multi-file do Java 22), de `java05` a `java25` |
| `legacy/` | 19 exemplos com a sintaxe/API original de Previews e Incubadoras que mudaram ou foram removidas (Java 12, 14, 17–24) |
| `standalone/` | Módulos JPMS (Java 9), script com shebang (Java 11) e arquivo de código compacto (Java 25) |

Como o projeto foi verificado:

- `src/` inteiro compila numa única chamada (`javac --release 25 --enable-preview --add-modules jdk.incubator.vector`) e **todos os 265 exemplos executam e terminam sozinhos** no JDK 25.
- Pastas históricas compiladas com o `--release` da época (ex.: `java05`–`java08` com `--release 8`, `java09` com `--release 9`, ...), para garantir que não usam API ou sintaxe posterior.
- Exemplos `legacy/` dos Java 17, 21 e 24 validados nos JDKs 17, 21 e 24. Os de 12, 14, 18, 19, 20, 22 e 23 foram revisados contra as JEPs, mas não executados (JDKs não disponíveis); isso está indicado no cabeçalho de cada arquivo e em [docs/java-timeline.md](docs/java-timeline.md).
- Nenhum exemplo acessa a internet: recursos de rede usam servidores locais na porta 0 criados pelo próprio exemplo. Arquivos temporários são criados fora do projeto e apagados no fim.

## Referências oficiais

- JEP Index (OpenJDK): <https://openjdk.org/jeps/0>
- Páginas de cada release do OpenJDK: <https://openjdk.org/projects/jdk/> (ex.: <https://openjdk.org/projects/jdk/25/>)
- Java SE Specifications (JLS/JVMS por versão): <https://docs.oracle.com/javase/specs/>
- Documentação da API do Java SE 25: <https://docs.oracle.com/en/java/javase/25/docs/api/>
- Java Language Updates (Oracle): <https://docs.oracle.com/en/java/javase/25/language/>
- Java Community Process (JSRs do Java 5 ao 8): <https://jcp.org/>
- Notas de versões antigas: Java 5 <https://docs.oracle.com/javase/1.5.0/docs/relnotes/features.html>, Java 6 <https://www.oracle.com/java/technologies/javase/features.html>
- Arquivo de JDKs antigos: <https://jdk.java.net/archive/>
- Roadmap de suporte Oracle Java SE: <https://www.oracle.com/java/technologies/java-se-support-roadmap.html>
