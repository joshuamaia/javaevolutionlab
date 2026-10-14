// Java 25 - Compact Source Files and Instance Main Methods (JEP 512)
//
// Status: Definitivo. Histórico: Preview no 21 (JEP 445), 22 (JEP 463), 23 (JEP 477) e
// 24 (JEP 495). Versão da época com println() sem qualificação: legacy/java24/simplesourcefiles.
//
// Este arquivo é um "arquivo de código compacto": não declara classe nem pacote. O compilador
// cria uma classe final implícita (nome = nome do arquivo) com os campos e métodos abaixo.
// Ele também importa automaticamente o módulo java.base (List, Map, LocalDate... sem imports).
// Entrada/saída simples via java.lang.IO (IO.println, IO.print, IO.readln) - java.lang é
// importado sempre, então basta "IO.".
//
// Execução (JDK 25), nesta pasta:  java HelloCompact.java
//                     ou:          java HelloCompact.java Maria   (usa o argumento como nome)
// Nota: é possível declarar "void main(String[] args)" e o launcher passa os argumentos.

final List<String> tasks = List.of("revisar pedido", "emitir nota", "enviar e-mail");

String greeting(String name) {
    return "Olá, " + name + "! Hoje é " + LocalDate.now().getDayOfWeek();
}

void main(String[] args) {
    // Lê do teclado só se nenhum argumento for passado E houver um console interativo.
    String name;
    if (args.length > 0) {
        name = args[0];
    } else if (System.console() != null && System.console().isTerminal()) {
        name = IO.readln("Seu nome: ");
    } else {
        name = "visitante";
    }
    IO.println(greeting(name));
    IO.println("Tarefas:");
    for (int i = 0; i < tasks.size(); i++) {
        IO.println("  " + (i + 1) + ". " + tasks.get(i));
    }
}
