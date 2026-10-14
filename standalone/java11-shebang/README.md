# Java 11 — Script com shebang (JEP 330)

`order-report` é um programa Java de arquivo único, **sem extensão `.java`**, cuja primeira
linha é um shebang:

```
#!/usr/bin/java --source 11
```

O lançador `java` compila o arquivo em memória e ignora a linha `#!`. Como a linha não é
Java válido, o arquivo não pode ser compilado com `javac` (por isso fica fora de `src/`).

## Linux / macOS

```bash
chmod +x order-report
./order-report                      # usa dados de exemplo
./order-report Ana:10 Bruno:20 Ana:5
```

Ajuste o caminho do shebang se o `java` não estiver em `/usr/bin/java`
(ex.: `#!/opt/jdk-25/bin/java --source 11`). `#!/usr/bin/env java --source 11` não funciona
em todos os sistemas, porque o `env` recebe "java --source 11" como um único argumento
(no Linux use `#!/usr/bin/env -S java --source 11`).

## Qualquer SO (inclusive Windows)

```bash
java --source 11 order-report
java --source 11 order-report Ana:10 Bruno:20
```

Arquivo sem extensão `.java` exige `--source N`. Com extensão `.java` basta `java Arquivo.java`
(ver `src/br/com/javalab/java11/launcher/SingleFileSourceLaunchExample.java`).
