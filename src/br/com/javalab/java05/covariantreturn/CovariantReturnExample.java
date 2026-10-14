package br.com.javalab.java05.covariantreturn;

/**
 * Java 5 - Tipos de retorno covariantes (JSR 14)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: um método sobrescrito pode declarar como retorno um SUBTIPO do retorno
 * do método original. Ex.: {@code Product copy()} pode ser sobrescrito por
 * {@code Book copy()}. O javac gera um "bridge method" com a assinatura antiga para
 * manter a compatibilidade binária.</p>
 * <p>Benefício: elimina casts em quem chama métodos como {@code copy()},
 * {@code clone()}, builders e fábricas especializadas.</p>
 * <p>Antes (Java 1.4): o retorno da sobrescrita tinha que ser idêntico, e o chamador
 * fazia cast: {@code Book b = (Book) book.copy();}.</p>
 * <p>Obs.: veio junto com a implementação de generics (JSR 14), que precisava de
 * bridge methods.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java05/covariantreturn/CovariantReturnExample.java
 * </pre>
 */
public class CovariantReturnExample {

    static class Product implements Cloneable {
        protected final String name;

        Product(String name) {
            this.name = name;
        }

        Product copy() {
            return new Product(name);
        }

        /** clone() devolve Object em Object; aqui já devolvemos Product (covariante). */
        @Override
        public Product clone() {
            try {
                return (Product) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + name + "]";
        }
    }

    static class Book extends Product {
        private final String isbn;

        Book(String name, String isbn) {
            super(name);
            this.isbn = isbn;
        }

        /** Retorno covariante: Book em vez de Product. */
        @Override
        Book copy() {
            return new Book(name, isbn);
        }

        @Override
        public Book clone() {
            return (Book) super.clone(); // único cast fica aqui dentro, não no chamador
        }

        String getIsbn() {
            return isbn;
        }
    }

    public static void main(String[] args) throws Exception {
        Book original = new Book("Java Efetivo", "978-0321356680");

        System.out.println("=== Abordagem antiga: retorno fixo + cast no chamador ===");
        Product generic = original;
        Book castCopy = (Book) generic.copy(); // necessário quando o tipo estático é Product
        System.out.println("Cópia via cast: " + castCopy + " ISBN " + castCopy.getIsbn());

        System.out.println();
        System.out.println("=== Abordagem nova: retorno covariante ===");
        Book copy = original.copy();           // sem cast
        Book cloned = original.clone();        // sem cast
        System.out.println("copy():  " + copy + " ISBN " + copy.getIsbn());
        System.out.println("clone(): " + cloned + " ISBN " + cloned.getIsbn());
        System.out.println("Polimorfismo preservado: " + generic.copy().getClass().getSimpleName());

        System.out.println();
        System.out.println("=== Bridge methods gerados pelo compilador em Book ===");
        for (java.lang.reflect.Method m : Book.class.getDeclaredMethods()) {
            if (m.getName().equals("copy") || m.getName().equals("clone")) {
                System.out.println(m.getReturnType().getSimpleName() + " " + m.getName()
                        + "()  bridge=" + m.isBridge());
            }
        }
    }
}
