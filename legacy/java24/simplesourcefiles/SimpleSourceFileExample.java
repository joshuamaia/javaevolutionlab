/**
 * Java 24 - Simple Source Files and Instance Main Methods, quarta Preview (JEP 495)
 * - forma ORIGINAL (legado)
 *
 * Status: Quarta Preview. Histórico: "Unnamed Classes and Instance Main Methods" no 21
 * (JEP 445), "Implicitly Declared Classes and Instance Main Methods" no 22 (JEP 463) e 23
 * (JEP 477); quarta Preview no 24 (JEP 495) com o nome "Simple Source Files"; definitivo no
 * Java 25 (JEP 512) como "Compact Source Files and Instance Main Methods".
 *
 * O que é: um arquivo-fonte sem declaração de classe (a classe é declarada implicitamente),
 * com um "void main()" de instância, sem "public static" e sem "String[] args".
 *
 * Por que é legado: no Java 23/24 todo arquivo simples importava AUTOMATICAMENTE os métodos
 * estáticos de "java.io.IO" (println, print, readln), permitindo escrever "println(...)" sem
 * qualificação. No Java 25 (JEP 512) a classe virou "java.lang.IO" e esse import estático
 * implícito foi REMOVIDO: é preciso escrever "IO.println(...)". Por isso este arquivo não
 * compila no JDK 25. Versão atual: standalone/java25-compact-source/HelloCompact.java.
 * (O import implícito de "module java.base" continua existindo no 25.)
 *
 * Execução (JDK 24 - validado com OpenJDK 24.0.2):
 *   java --enable-preview --source 24 legacy/java24/simplesourcefiles/SimpleSourceFileExample.java
 *
 * Observação: comentário em bloco comum (não Javadoc) porque não há classe declarada para
 * documentar.
 */

// Campos e métodos "soltos" pertencem à classe implícita
final List<String> menu = List.of("café", "pão de queijo", "suco"); // List: import implícito de java.base

String formatItem(int index, String item) {
    return (index + 1) + ". " + item;
}

void main() {
    println("=== Cardápio (println sem qualificação: import implícito de java.io.IO) ===");
    for (int i = 0; i < menu.size(); i++) {
        println(formatItem(i, menu.get(i)));
    }
    print("Total de itens: ");
    println(menu.size());
}
