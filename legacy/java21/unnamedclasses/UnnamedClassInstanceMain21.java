/*
 * Java 21 - Unnamed Classes and Instance Main Methods, Preview (JEP 445)
 *
 * Status: Preview no Java 21. Depois: "Implicitly Declared Classes and Instance Main Methods"
 * no 22 (JEP 463) e 23 (JEP 477), "Simple Source Files" no 24 (JEP 495) e definitivo no Java 25
 * como "Compact Source Files and Instance Main Methods" (JEP 512).
 *
 * O que é: um arquivo .java SEM declaração de classe. Os métodos e campos do arquivo pertencem a
 * uma "classe sem nome" (unnamed class) gerada pelo compilador. O método main pode ser de
 * instância, sem "public", sem "static" e sem "String[] args".
 * Benefício: o primeiro programa de quem está aprendendo não exige entender classes, modificadores
 * de acesso, static e arrays.
 * Antes: public class Hello { public static void main(String[] args) { ... } }
 *
 * Por que está em legacy/ (e sem pacote): uma unnamed class não pode ter "package" e por isso não cabe
 * no classpath de src/. A sintaxe deste arquivo, por acaso, também é aceita pelo JDK 25 como
 * "compact source file" (lá, sem flags e com novidades como a classe java.lang.IO e o import
 * automático de java.base). Os exemplos do Java 25 ficam em standalone/.
 * Regras do Java 21 ilustradas aqui: a classe sem nome é final, não pode ser referenciada pelo nome,
 * pode ter campos e métodos auxiliares, e o launcher escolhe o main pela ordem de prioridade
 * (static main(String[]) > static main() > main(String[]) de instância > main() de instância).
 * Diferença observável: no JDK 21 a classe gerada é sintética (isSynthetic() = true); no JDK 25
 * a classe implícita não é mais marcada como sintética (false).
 *
 * Execução (requer JDK 21):
 *   java --enable-preview --source 21 legacy/java21/unnamedclasses/UnnamedClassInstanceMain21.java
 */

String storeName = "Loja do Java Evolution Lab";

String greeting(String customer) {
    return "Olá, " + customer + "! Bem-vindo à " + storeName + ".";
}

void main() {
    System.out.println("=== Programa sem declaração de classe e com main de instância ===");
    System.out.println(greeting("Ana"));
    System.out.println("Nome gerado para a classe: " + getClass().getName());
    System.out.println("A classe é sintética? " + getClass().isSynthetic());
}
