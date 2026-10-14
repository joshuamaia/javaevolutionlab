package br.com.javalab.java08.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Java 8 - Comparator.comparing().thenComparing() e afins
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: fábricas estáticas e métodos default em {@link Comparator}: {@code comparing},
 * {@code comparingInt/Double}, {@code thenComparing}, {@code reversed},
 * {@code nullsFirst/nullsLast}, {@code naturalOrder/reverseOrder}.</p>
 * <p>Benefício: ordenações com múltiplos critérios declarativas e sem erros de sinal.</p>
 * <p>Antes: classe anônima com comparações encadeadas por if.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/collections/ComparatorComparingExample.java
 * </pre>
 */
public class ComparatorComparingExample {

    static class Employee {
        final String name;
        final String department; // pode ser null
        final double salary;

        Employee(String name, String department, double salary) {
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        String getName() {
            return name;
        }

        String getDepartment() {
            return department;
        }

        double getSalary() {
            return salary;
        }

        @Override
        public String toString() {
            return String.format("%-6s %-10s %8.2f", name, department, salary);
        }
    }

    public static void main(String[] args) {
        List<Employee> staff = new ArrayList<>(Arrays.asList(
                new Employee("Bia", "financeiro", 5000),
                new Employee("Ana", "vendas", 9000),
                new Employee("Caio", "financeiro", 5000),
                new Employee("Dan", "vendas", 7000),
                new Employee("Eli", null, 4000)));

        System.out.println("=== Abordagem antiga: classe anônima com ifs ===");
        Collections.sort(staff, new Comparator<Employee>() {
            @Override
            public int compare(Employee a, Employee b) {
                int bySalary = Double.compare(b.salary, a.salary);
                if (bySalary != 0) {
                    return bySalary;
                }
                return a.name.compareTo(b.name);
            }
        });
        staff.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Java 8: salário desc, depois nome ===");
        staff.sort(Comparator.comparingDouble(Employee::getSalary).reversed()
                .thenComparing(Employee::getName));
        staff.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Departamento (nulls por último), depois salário desc ===");
        staff.sort(Comparator.comparing(Employee::getDepartment, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Employee::getSalary, Comparator.reverseOrder()));
        staff.forEach(System.out::println);

        System.out.println();
        System.out.println("=== Usando com streams: maior salário ===");
        System.out.println(staff.stream().max(Comparator.comparingDouble(Employee::getSalary)).get().getName());
    }
}
