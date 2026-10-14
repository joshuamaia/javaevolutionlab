# Guia de execução

Este guia explica como executar os exemplos do **Java Evolution Lab** no IntelliJ IDEA e pelo terminal, incluindo os casos especiais (Preview, Incubadora, acesso nativo, módulos e exemplos que exigem JDKs antigos).

> Todos os comandos de terminal partem da **raiz do projeto** (a pasta que contém `README.md`).

---

## 1. Onde fica cada tipo de exemplo

| Pasta | Conteúdo | JDK |
|---|---|---|
| `src/br/com/javalab/javaNN/` | Exemplos que compilam e rodam no **JDK 25**. É a única *source root* do IntelliJ. | 25 |
| `legacy/javaNN/` | Sintaxe/API **original** de recursos Preview/Incubadora que mudaram ou foram removidos (ex.: `break valor` no switch do Java 12, String Templates do Java 21). Não fazem parte da compilação do projeto. | O JDK indicado no cabeçalho do arquivo |
| `standalone/` | Exemplos que exigem estrutura própria (módulos JPMS com `module-info.java`, arquivos de código compacto sem pacote etc.). Cada subpasta tem um `README.md`. | 25 (salvo indicação) |

Por que separar? O IntelliJ compila toda a pasta `src/` de uma vez. Um único arquivo com sintaxe removida (por exemplo, `STR."..."`) impediria a execução de **todos** os outros exemplos. Por isso, o código histórico incompatível fica isolado em `legacy/`.

---

## 2. IntelliJ IDEA

### 2.1 Abrir o projeto

1. `File > Open...` e selecione a pasta do projeto (a que contém `javaevolutionlab.iml`).
2. O projeto já vem configurado (`.idea/` e `javaevolutionlab.iml`):
   - um único módulo Java, **sem Maven/Gradle**;
   - `src/` marcada como *Sources Root*;
   - saída de compilação em `out/`;
   - **Language level: `25 (Preview)`**, para que os exemplos com recursos Preview do Java 25 compilem;
   - opção adicional do `javac`: `--add-modules jdk.incubator.vector` (Vector API, Incubadora).
3. Se aparecer *"Project JDK is not defined"*: `File > Project Structure > Project > SDK` e escolha (ou adicione/baixe) um **JDK 25**. O nome esperado pelo projeto é `25`; qualquer JDK 25 serve.

### 2.2 Executar um exemplo

Abra a classe e clique no ícone ▶ ao lado do `main` (ou `Ctrl+Shift+F10`).

Como o language level é *Preview*, o IntelliJ adiciona `--enable-preview` automaticamente na compilação e na execução.

### 2.3 Exemplos que precisam de *VM options*

Alguns exemplos precisam de opções da JVM. O projeto inclui *run configurations* prontas em `.idea/runConfigurations/` (aparecem no seletor de execução). Para criar manualmente: `Run > Edit Configurations... > Modify options > Add VM options`.

| Situação | VM options |
|---|---|
| Vector API (`jdk.incubator.vector`) | `--add-modules jdk.incubator.vector` |
| Foreign Function & Memory API (chamadas nativas) | `--enable-native-access=ALL-UNNAMED` (sem ela, o JDK 25 apenas emite um aviso) |
| Recursos Preview | `--enable-preview` (já incluído automaticamente pelo IntelliJ) |

### 2.4 Exemplos de `legacy/` e `standalone/`

Essas pastas **não** são *source roots*, então o IntelliJ não os compila nem mostra o botão ▶. Execute-os pelo terminal (seções 4 e 5), usando o JDK indicado no cabeçalho de cada arquivo.

---

## 3. Terminal — JDK 25

Verifique a versão:

```bash
java -version    # deve mostrar 25
```

### 3.1 Modo código-fonte (sem `javac`)

Desde o Java 11 (JEP 330), é possível executar um arquivo `.java` diretamente. A partir do Java 22 (JEP 458), o lançador também encontra outras classes do mesmo pacote em outros arquivos.

```bash
java src/br/com/javalab/java16/records/RecordExample.java
```

### 3.2 Recursos Preview

```bash
java --enable-preview --source 25 src/br/com/javalab/java25/stablevalues/StableValuesExample.java
```

- `--enable-preview` libera os recursos Preview do JDK em uso;
- `--source 25` é **obrigatório** no modo código-fonte junto com `--enable-preview` e precisa ser exatamente a versão do JDK em execução.

### 3.3 APIs Incubadoras

Módulos incubadores (`jdk.incubator.*`) não são resolvidos por padrão; é preciso adicioná-los explicitamente:

```bash
java --add-modules jdk.incubator.vector src/br/com/javalab/java16/vector/VectorApiExample.java
```

O JDK emite o aviso `WARNING: Using incubator modules: jdk.incubator.vector`. Isso é esperado.

### 3.4 Acesso nativo (FFM API)

```bash
java --enable-native-access=ALL-UNNAMED src/br/com/javalab/java22/ffm/ForeignFunctionMemoryExample.java
```

### 3.5 Compilação explícita com `javac` + `java`

Compilar **todo** o projeto (igual ao que o IntelliJ faz):

```bash
# Linux/macOS/Git Bash
find src -name "*.java" > sources.txt
javac --release 25 --enable-preview --add-modules jdk.incubator.vector -d out @sources.txt
```

```powershell
# Windows PowerShell
Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName } | Out-File -Encoding ascii sources.txt
javac --release 25 --enable-preview --add-modules jdk.incubator.vector -d out "@sources.txt"
```

Executar uma classe compilada (nome totalmente qualificado):

```bash
java --enable-preview -cp out br.com.javalab.java16.records.RecordExample
java --enable-preview --add-modules jdk.incubator.vector -cp out br.com.javalab.java16.vector.VectorApiExample
```

> Classes compiladas com `--enable-preview` que usam recursos Preview só executam com `--enable-preview` e no **mesmo** JDK (25) que as compilou.

Compilar apenas uma versão:

```bash
javac --release 25 --enable-preview -d out $(find src/br/com/javalab/java08 -name "*.java")
```

Conferir se um exemplo histórico respeita a API da época (útil para estudo):

```bash
javac --release 8 -d /tmp/check $(find src/br/com/javalab/java08 -name "*.java")
```

---

## 4. Exemplos `legacy/` — JDKs antigos

Cada arquivo em `legacy/` informa no cabeçalho **qual JDK instalar** e o comando exato. São arquivos sem `package`, executados no modo código-fonte com o JDK da época:

```bash
# Exemplo: String Templates (Preview no Java 21, retirado no Java 23)
"<caminho-do-jdk-21>/bin/java" --enable-preview --source 21 legacy/java21/stringtemplates/StringTemplatesPreview21.java
```

Regras importantes:

- `--source NN` deve ser **igual** à versão do JDK que está executando quando houver `--enable-preview`. Um recurso Preview do Java 17 só roda no JDK 17.
- O modo código-fonte (`java Arquivo.java`) só existe a partir do **Java 11**. Para JDKs 9/10 (ou para recursos Incubadora dos JDKs 14, 19 e 20 quando preferir), compile explicitamente:

  ```bash
  javac --enable-preview --release 14 --add-modules jdk.incubator.foreign -d out-legacy legacy/java14/.../Exemplo.java
  java  --enable-preview --add-modules jdk.incubator.foreign -cp out-legacy Exemplo
  ```

### 4.1 Como obter JDKs antigos

- **IntelliJ IDEA**: `File > Project Structure > SDKs > + > Download JDK...` (escolha a versão e o fornecedor, por exemplo Eclipse Temurin, Azul Zulu ou Oracle OpenJDK). Os JDKs ficam em `~/.jdks/`.
- **Arquivos oficiais**: versões não-LTS antigas (12, 13, 14, 19, 20...) estão em <https://jdk.java.net/archive/>.
- **SDKMAN!** (Linux/macOS): `sdk install java 21.0.x-tem`.

Para usar um JDK específico sem mudar o `PATH`, chame o binário pelo caminho completo (`<jdk>/bin/java`) ou ajuste `JAVA_HOME` só na sessão do terminal:

```powershell
# PowerShell
$env:JAVA_HOME = "C:\Users\<voce>\.jdks\ms-21.0.12"
& "$env:JAVA_HOME\bin\java" -version
```

```bash
# Bash
JAVA_HOME=~/.jdks/ms-21.0.12 "$HOME/.jdks/ms-21.0.12/bin/java" -version
```

No IntelliJ, é possível executar uma classe com outro JDK em `Run > Edit Configurations... > Build and run > JRE`. Isso só vale para código que compila no JDK do projeto; arquivos de `legacy/` devem ser executados pelo terminal.

---

## 5. Exemplos `standalone/`

Cada subpasta contém um `README.md` com os comandos exatos. Resumo:

| Pasta | Recurso | Comando principal |
|---|---|---|
| `standalone/java09-modules/` | Java Platform Module System (JEP 261) | `javac --module-source-path ... -d out -m ...` e `java --module-path out -m ...` |
| `standalone/java11-shebang/` | Script executável com shebang (JEP 330) | `./order-report` (Linux/macOS) ou `java --source 11 order-report` |
| `standalone/java25-compact-source/` | Compact Source Files (JEP 512) | `java HelloCompact.java` |

(Consulte também `docs/feature-matrix.md`; a coluna **Classe** aponta para o arquivo de cada recurso.)

---

## 6. Recursos da JVM (GC, CDS, AOT, cabeçalhos compactos)

Melhorias internas da JVM não mudam o código-fonte. Os exemplos correspondentes são **exemplos de observação**: eles imprimem informações do runtime (MXBeans, propriedades, medições) e, quando possível, relançam a si mesmos com flags diferentes para comparar. Os comandos manuais estão no Javadoc de cada classe. Exemplos de flags:

```bash
java -Xlog:gc -XX:+UseZGC ...                       # escolher/observar o GC
java -XX:+UseCompactObjectHeaders ...               # JEP 519 (Java 25)
java -XX:AOTCacheOutput=app.aot -cp out Main        # JEP 514 (Java 25): treino + criação do cache AOT
java -XX:AOTCache=app.aot -cp out Main              # executar usando o cache AOT
```

---

## 7. Problemas comuns

| Sintoma | Causa / solução |
|---|---|
| `error: invalid source release: 25` ou `UnsupportedClassVersionError` | O `java`/`javac` em uso não é o 25. Confira `java -version` e o SDK do projeto no IntelliJ. |
| `... is a preview API and is disabled by default` | Falta `--enable-preview` (no terminal, junto com `--source 25`). |
| `package jdk.incubator.vector does not exist` / `NoClassDefFoundError: jdk/incubator/vector/...` | Falta `--add-modules jdk.incubator.vector` (na compilação **e** na execução). |
| `WARNING: A restricted method in java.lang.foreign.Linker has been called` | Adicione `--enable-native-access=ALL-UNNAMED`. |
| Saída com acentos quebrados no console do Windows | Use `chcp 65001` no terminal ou `-Dstdout.encoding=UTF-8`. |
| `error: class found on application class path: ...` ao usar o modo código-fonte | Não execute `java Arquivo.java` com `-cp out` apontando para classes já compiladas com o mesmo nome. |
| O IntelliJ não mostra ▶ em arquivos de `legacy/` ou `standalone/` | Comportamento esperado: essas pastas não são *source roots*. Use o terminal. |

---

## Apêndice — flags e casos especiais por versão

Lista, versão por versão, dos exemplos que exigem flags, JDK antigo ou estrutura especial. Os exemplos não citados rodam sem flags no JDK 25.

### Java 5

- Nenhuma. Todos os exemplos rodam com `java src/br/com/javalab/java05/<feature>/<Classe>.java` no JDK 25.

### Java 6

- Nenhuma flag de JVM. Observações:
  - `CompilerApiExample` e `AnnotationProcessingExample` precisam de um **JDK** (módulo `jdk.compiler`); num JRE `ToolProvider.getSystemJavaCompiler()` é `null` e o exemplo avisa e termina.
  - `ConsoleExample` só lê input com o argumento `--interativo` **e** num terminal real: `java src/br/com/javalab/java06/console/ConsoleExample.java --interativo`. Detecta terminal com `System.console() != null` e, se existir (JDK 22+), `Console.isTerminal()` chamado por reflexão. Nunca bloqueia em IDE/pipeline.
  - `DesktopIntegrationExample` só consulta suporte (não abre janelas); com `-Djava.awt.headless=true` mostra tudo como não suportado.

### Java 7

- Nenhum exemplo do Java 7 precisa de flags. Todos rodam com `java src/br/com/javalab/java07/<feature>/<Classe>.java`.
- G1 (apenas documentado): no JDK 7u4+ habilitava-se com `java -XX:+UseG1GC ...`; no JDK 9+ já é o padrão (`java -Xlog:gc ...` mostra "Using G1").

### Java 8

- Nenhum exemplo do Java 8 precisa de flags. Todos rodam com `java src/br/com/javalab/java08/<feature>/<Classe>.java`.
- Experimentos opcionais (Metaspace), documentados no Javadoc do exemplo:
  - `java -XX:MaxMetaspaceSize=64m src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java`
  - `java -Xlog:gc+metaspace src/br/com/javalab/java08/metaspace/MetaspaceObservationExample.java`
- Nashorn só funcionaria em JDK 8–14 (indisponíveis no ambiente); no JDK 25 o exemplo apenas verifica a ausência do motor.

### Java 9

- JPMS completo (dois módulos, `provides`/`uses`, `jlink`): `standalone/java09-modules/` — a partir dessa pasta:
  `javac -d out --module-source-path src -m br.com.javalab.app,br.com.javalab.inventory`
  `java --module-path out -m br.com.javalab.app/br.com.javalab.app.OrderApplication`
- Exemplos de observação com flags sugeridas no Javadoc (opcionais; todos rodam sem flags):
  `java -XX:-CompactStrings src/br/com/javalab/java09/compactstrings/CompactStringsExample.java`
  `java -XX:+UseParallelGC src/br/com/javalab/java09/gc/G1DefaultGcExample.java`
- `ProcessHandleExample` e `UnifiedJvmLoggingExample` relançam o próprio `java` (descoberto por `ProcessHandle.current().info().command()`).

### Java 10

- `ApplicationClassDataSharingExample` relança a si mesmo: empacota a própria classe num JAR temporário, gera a lista de classes (`-XX:DumpLoadedClassList`), cria o arquivo com `-Xshare:dump -XX:SharedClassListFile=... -XX:SharedArchiveFile=app.jsa` e compara `-Xshare:off` / CDS padrão / AppCDS (no JDK 10 real acrescenta `-XX:+UseAppCDS` automaticamente). Funciona tanto pelo classpath quanto no modo código-fonte.
- `ContainerAwarenessExample` relança `java -XX:ActiveProcessorCount=2 -XX:MaxRAMPercentage=5 -XX:+PrintFlagsFinal -version` para simular um contêiner. Comandos Docker apenas documentados (`docker run --cpus=2 -m 512m ...`).
- Flags sugeridas no Javadoc (opcionais): `java -XX:+UseG1GC -Xlog:gc -XX:ParallelGCThreads=1|8 src/br/com/javalab/java10/gc/G1ParallelFullGcExample.java`; `java -Xlog:handshake=debug src/br/com/javalab/java10/handshakes/ThreadLocalHandshakesExample.java`.

### Java 11

- Script com shebang: `standalone/java11-shebang/order-report` (sem extensão `.java`): `java --source 11 standalone/java11-shebang/order-report` (qualquer SO) ou `./order-report` no Linux/macOS após `chmod +x`.
- `SingleFileSourceLaunchExample` mostra diferenças entre `java src/br/com/javalab/java11/launcher/SingleFileSourceLaunchExample.java a b` (MemoryClassLoader) e a execução pelo classpath.
- Epsilon (opcional): `java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -Xlog:gc src/br/com/javalab/java11/gc/EpsilonGcExample.java`; com `-Xmx150m` a JVM termina com `OutOfMemoryError` (comportamento esperado do Epsilon). Sem flags, o exemplo relança `java -XX:+UseEpsilonGC -Xlog:gc -version`.
- HTTP Client / comparação: servidor local `com.sun.net.httpserver.HttpServer` na porta 0, sem internet.

### Java 12

- `legacy/java12/switchexpressions/SwitchBreakValueExample.java` — **requer JDK 12** (a sintaxe `break valor` não compila no JDK 13+):
  `java --enable-preview --source 12 legacy/java12/switchexpressions/SwitchBreakValueExample.java`
- Para experimentar (documentado no Javadoc de `GcAndCdsObservationExample`):
  `java -Xshare:off -version` (sem CDS), `java -Xlog:cds -version`,
  `java -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC ...` (Java 12; no 15+ dispensa o unlock),
  `java -XX:G1PeriodicGCInterval=5000 -Xlog:gc ...`.
- Todos os exemplos de `src/.../java12` rodam sem flags no JDK 25.

### Java 13

- Nenhum exemplo de `src/.../java13` precisa de flags no JDK 25.
- Comandos documentados em `DynamicCdsArchiveExample`:
  `java -XX:ArchiveClassesAtExit=app.jsa -cp minha-app.jar MinhaApp` e `java -XX:SharedArchiveFile=app.jsa -Xlog:cds -cp minha-app.jar MinhaApp`.
- No Java 13 voltava-se à implementação antiga de sockets com `-Djdk.net.usePlainSocketImpl=true` (removida no Java 18).
- ZGC uncommit: `-XX:+UseZGC -XX:ZUncommitDelay=<s>` / `-XX:-ZUncommit`.

### Java 14

- `legacy/java14/foreignmemory/ForeignMemoryAccessExample.java` — **requer JDK 14** (módulo `jdk.incubator.foreign` removido):
  `java --add-modules jdk.incubator.foreign --source 14 legacy/java14/foreignmemory/ForeignMemoryAccessExample.java`
- `HelpfulNullPointerExample`: no JDK 14 era preciso `-XX:+ShowCodeDetailsInExceptionMessages`; para ver o comportamento antigo no JDK 25:
  `java -XX:-ShowCodeDetailsInExceptionMessages src/br/com/javalab/java14/npe/HelpfulNullPointerExample.java`
- Records/pattern matching/text blocks: no Java 14 exigiam `--enable-preview --source 14`; no JDK 25 rodam sem flags.

### Java 15

- Nenhum exemplo de `src/.../java15` precisa de flags no JDK 25 (sealed/records/pattern matching exigiam `--enable-preview --source 15` em 2020).
- `HiddenClassExample` precisa do módulo `jdk.compiler` (presente em qualquer JDK) — funciona compilado e via `java src/.../HiddenClassExample.java`; se o compilador não existir, usa como alternativa o `.class` da classe aninhada `Template` lido como recurso (caminho testado separadamente).
- Comandos documentados: `java -XX:+UseZGC -Xlog:gc -version`, `java -XX:+UseShenandoahGC ...`.

### Java 16

- `VectorApiExample`: `java --add-modules jdk.incubator.vector src/br/com/javalab/java16/vector/VectorApiExample.java` (imprime "WARNING: Using incubator modules").
- `StrongEncapsulationExample`: para liberar o acesso, `java --add-opens java.base/java.lang=ALL-UNNAMED src/br/com/javalab/java16/encapsulation/StrongEncapsulationExample.java`.
- `ValueBasedClassesWarningExample`: diagnóstico em tempo de execução
  `java -XX:+UnlockDiagnosticVMOptions -XX:DiagnoseSyncOnValueBasedClasses=2 src/br/com/javalab/java16/valuebased/ValueBasedClassesWarningExample.java`
  (o javac emite de propósito o aviso "attempt to synchronize on an instance of a value-based class"; no JDK 16 a categoria -Xlint era `synchronization`, no JDK 25 aparece como `identity`).
- `SealedClassesSecondPreviewExample`: exigia `--enable-preview --source 16` em 2021; no JDK 25 roda sem flags.

### Java 17

- `legacy/java17/patternswitch/PatternMatchingSwitchPreview17.java` — **requer JDK 17**:
  `java --enable-preview --source 17 legacy/java17/patternswitch/PatternMatchingSwitchPreview17.java`
- `StrongEncapsulationExample`: para liberar o acesso, `--add-opens java.base/java.lang=ALL-UNNAMED`.
- `AlwaysStrictFloatingPointExample`: aviso visível com `javac -Xlint:strictfp`.
- Observação: o pacote do exemplo de JEP 306 se chama `strictmath` porque `strictfp` é palavra reservada e não pode ser nome de pacote.

### Java 18

- `VectorMaskReductionExample`: `java --add-modules jdk.incubator.vector src/br/com/javalab/java18/vectorapi/VectorMaskReductionExample.java`
- `legacy/java18/patternswitch/PatternMatchingSwitchSecondPreview18.java` — **requer JDK 18**:
  `java --enable-preview --source 18 legacy/java18/patternswitch/PatternMatchingSwitchSecondPreview18.java`
- `Utf8ByDefaultExample`: comportamento antigo com `java -Dfile.encoding=COMPAT ...`.
- `CleanerInsteadOfFinalizeExample`: `java --finalization=disabled ...` para ver que `finalize()` deixa de rodar.
- `CodeSnippetsExample`: gerar a documentação com `javadoc -d <dir-temporário> src/br/com/javalab/java18/javadocsnippets/CodeSnippetsExample.java`.
- `InetAddressResolverSpiExample`: não precisa de flags. Ele cria um diretório temporário com `META-INF/services/java.net.spi.InetAddressResolverProvider` e relança a si mesmo num processo filho (classpath atual + diretório). Em modo código-fonte (`java Arquivo.java`) detecta que a classe vem de um `.java` (CodeSource) e compila o próprio arquivo com `javax.tools` para o diretório temporário antes de relançar. Nenhum arquivo de serviço fica em `src/`; o diretório é apagado no fim. Repassa `--enable-preview` ao filho se o pai foi iniciado com ele.
- `SimpleWebServerExample`: só `127.0.0.1` porta 0, diretório temporário apagado no final.

### Java 19

- `java --enable-preview --source 19 legacy/java19/recordpatterns/NamedRecordPatternsPreview19.java`
- `java --enable-preview --source 19 --add-modules jdk.incubator.concurrent legacy/java19/structuredconcurrency/StructuredConcurrencyIncubator19.java`
  (`--enable-preview` porque o escopo cria virtual threads, Preview no 19)
- `java --enable-preview --source 19 legacy/java19/foreignmemory/ForeignFunctionMemoryPreview19.java`
- Os exemplos de `src/.../java19` usam API/sintaxe que ficou igual: no JDK 19 exigiam `--enable-preview --source 19`; no JDK 25 rodam sem flags.

### Java 20

- `java --enable-preview --source 20 legacy/java20/recordpatterns/RecordPatternsInEnhancedFor20.java`
- `java --add-modules jdk.incubator.concurrent --source 20 legacy/java20/scopedvalues/ScopedValuesIncubator20.java`
- `java --enable-preview --source 20 --add-modules jdk.incubator.concurrent legacy/java20/structuredconcurrency/StructuredConcurrencySecondIncubator20.java`
- Os exemplos de `src/.../java20` usam sintaxe/API que ficou igual: no JDK 20 exigiam `--enable-preview --source 20`; no JDK 25 rodam sem flags.

### Java 21

- Legacy (validados com **JDK 21**):
  - `java --enable-preview --source 21 legacy/java21/stringtemplates/StringTemplatesPreview21.java`
  - `java --enable-preview --source 21 legacy/java21/scopedvalues/ScopedValuesStaticMethodsPreview21.java`
  - `java --enable-preview --source 21 legacy/java21/structuredconcurrency/StructuredConcurrencyPreview21.java`
  - `java --enable-preview --source 21 legacy/java21/unnamedclasses/UnnamedClassInstanceMain21.java`
- `ScopedValuesExample` (src) usa só a parte comum da API (`where(...).run`, `get`, `isBound`, `orElse`): roda no JDK 25 sem flags e no JDK 21 com `java --enable-preview --source 21 src/br/com/javalab/java21/scopedvalues/ScopedValuesExample.java`.
- `UnnamedPatternsAndVariablesExample`: no JDK 21 exigia `--enable-preview --source 21`; no 25 roda sem flags (definitivo no 22, mesma sintaxe).
- Generational ZGC: no 21/22 `java -XX:+UseZGC -XX:+ZGenerational ...`; no 23 é o padrão do ZGC; no 24+ a flag `ZGenerational` é obsoleta. O exemplo relança `java -XX:+UseZGC -Xlog:gc -version`.
- Para diagnosticar pinning de virtual threads no 21: `-Djdk.tracePinnedThreads=full`.
- `PlatformVsVirtualThreadsComparison` leva ~5,5 s (pool de 200 threads de plataforma: ~5 s; virtual threads: ~150 ms).

### Java 22

- `ForeignFunctionMemoryExample`: `--enable-native-access=ALL-UNNAMED` (sem ela roda, mas imprime aviso de "restricted method"). **IntelliJ: VM option `--enable-native-access=ALL-UNNAMED`.**
  `java --enable-native-access=ALL-UNNAMED src/br/com/javalab/java22/ffm/ForeignFunctionMemoryExample.java`
- `MultiFileLauncherExample`: o ponto do exemplo é rodar pelo launcher a partir da raiz do projeto: `java src/br/com/javalab/java22/multifile/MultiFileLauncherExample.java` (no IntelliJ roda normalmente).
- Legado (JDK 22, indisponível): `java --enable-preview --source 22 legacy/java22/structuredconcurrency/StructuredConcurrencySecondPreviewExample.java`
- Os demais exemplos de `src/.../java22` rodam no JDK 25 **sem flags** (recursos que eram Preview no 22 e hoje são definitivos com a mesma sintaxe/API).

### Java 23

- `PrimitivePatternsExample` (ainda Preview no 25): `java --enable-preview --source 25 src/br/com/javalab/java23/primitivepatterns/PrimitivePatternsExample.java`. **IntelliJ: VM option `--enable-preview`.**
- `MarkdownDocCommentsExample`: para ver o HTML, `javadoc -d <dir-temporario> src/br/com/javalab/java23/markdowndoc/MarkdownDocCommentsExample.java`.
- `GenerationalZgcObservation`: relança a si mesmo com `-XX:+UseZGC` (funciona compilado e em modo código-fonte); também `java -XX:+UseZGC -Xlog:gc ...`.
- Legados (JDK 23, indisponível):
  `java --enable-preview --source 23 legacy/java23/scopedvalues/ScopedValuesThirdPreviewExample.java`
  `java --enable-preview --source 23 legacy/java23/structuredconcurrency/StructuredConcurrencyThirdPreviewExample.java`
- Demais exemplos: JDK 25 sem flags.

### Java 24

- Legados **validados com JDK 24** (`/c/Users/ALIENWARE/.jdks/openjdk-24.0.2+12-54/bin/java`):
  `java --enable-preview --source 24 legacy/java24/simplesourcefiles/SimpleSourceFileExample.java`
  `java --enable-preview --source 24 legacy/java24/structuredconcurrency/StructuredConcurrencyFourthPreviewExample.java`
- `AheadOfTimeClassLoadingExample`: roda sem flags; para ver o efeito, empacotar em JAR e usar `-XX:AOTMode=record -XX:AOTConfiguration=app.aotconf`, depois `-XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=app.aot`, depois `-XX:AOTCache=app.aot` (comandos completos no Javadoc). **IntelliJ (opcional): VM option `-XX:AOTCache=app.aot` depois de criar o cache.**
- `SecurityManagerDisabledExample`: opcionalmente `-Djava.security.manager=allow` para ver a JVM recusar a inicialização.
- `SynchronizedWithoutPinningExample`: roda sem flags; para comparar, executar o mesmo arquivo com JDK 21.
- Todos os exemplos de `src/.../java24` rodam no JDK 25 **sem flags** (KDF, Flexible Constructors, Module Import e Scoped Values eram Preview no 24 e hoje são definitivos com a mesma API).

### Java 25

- `--enable-preview`: `PrimitivePatternsThirdPreviewExample`, `StructuredConcurrencyFifthPreviewExample`, `StableValuesExample`, `PemEncodingsExample` (e `java23...PrimitivePatternsExample`).
  Ex.: `java --enable-preview --source 25 src/br/com/javalab/java25/stablevalues/StableValuesExample.java`
- `--add-modules jdk.incubator.vector`: `VectorDotProductExample`
  `java --add-modules jdk.incubator.vector src/br/com/javalab/java25/vectorapi/VectorDotProductExample.java`
- `--enable-native-access=ALL-UNNAMED`: `java22...ForeignFunctionMemoryExample` (evita aviso).
- Exemplos que relançam a si mesmos (sem flags; funcionam compilados e via `java Arquivo.java`, usam `jdk.launcher.sourcefile` no modo código-fonte): `CompactObjectHeadersObservation` (`-XX:±UseCompactObjectHeaders`), `AotCommandLineErgonomicsExample` e `AotMethodProfilingExample` (`-XX:AOTCacheOutput`/`-XX:AOTCache` em diretório temporário, apagado no fim), `java23...GenerationalZgcObservation`.
- Standalone: `cd standalone/java25-compact-source && java HelloCompact.java [nome]` (README na pasta).
- `InstanceMainExample` (`void main()` de instância, sem static/args): roda no IntelliJ e com `java src/br/com/javalab/java25/instancemain/InstanceMainExample.java`.
- JFR por linha de comando (opcional): `java '-XX:StartFlightRecording:method-timing=br.com.javalab.java25.jfr.MethodTimingExample::calculateFreight,filename=rec.jfr' ...` e `jfr view method-timing rec.jfr` (testado).

