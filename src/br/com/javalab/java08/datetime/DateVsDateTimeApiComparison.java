package br.com.javalab.java08.datetime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Java 8 - Comparação: Date/Calendar vs java.time (JEP 150 / JSR 310)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: as mesmas tarefas com a API antiga ({@code Date}, {@code Calendar},
 * {@code SimpleDateFormat}) e com {@code java.time}, incluindo a conversão entre elas.</p>
 * <p>Problemas da API antiga: meses começando em 0, anos a partir de 1900 em
 * {@code new Date(y, m, d)}, objetos mutáveis, {@code SimpleDateFormat} não thread-safe,
 * {@code Date} que na verdade é um instante (com hora) e não uma data.</p>
 * <p>Ponte: {@code Date.from(Instant)}, {@code date.toInstant()}, {@code GregorianCalendar.toZonedDateTime()}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/datetime/DateVsDateTimeApiComparison.java
 * </pre>
 */
public class DateVsDateTimeApiComparison {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo")); // saída determinística

        System.out.println("=== 1) Criar a data 18/03/2014 ===");
        Date oldDate = new Date(2014 - 1900, 2, 18); // ano - 1900 e mês 0-based!
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2014, Calendar.MARCH, 18); // Calendar.MARCH == 2
        System.out.println("Antigo (Date):     " + oldDate);
        System.out.println("Antigo (Calendar): " + cal.getTime());
        System.out.println("java.time:         " + LocalDate.of(2014, 3, 18));

        System.out.println();
        System.out.println("=== 2) Somar 30 dias ===");
        cal.add(Calendar.DAY_OF_MONTH, 30); // MUTA o calendário
        System.out.println("Antigo: " + cal.getTime() + " (o objeto cal foi alterado)");
        LocalDate date = LocalDate.of(2014, 3, 18);
        System.out.println("java.time: " + date.plusDays(30) + " (date continua " + date + ")");

        System.out.println();
        System.out.println("=== 3) Mutabilidade perigosa ===");
        Date shared = new Date(0);
        Date alias = shared;
        alias.setTime(86_400_000L); // quem tinha 'shared' vê a mudança
        System.out.println("Date compartilhado foi alterado por outra referência: " + shared.getTime());

        System.out.println();
        System.out.println("=== 4) Dias entre datas ===");
        Calendar c1 = new GregorianCalendar(2014, Calendar.JANUARY, 1);
        Calendar c2 = new GregorianCalendar(2014, Calendar.DECEMBER, 25);
        long oldDays = (c2.getTimeInMillis() - c1.getTimeInMillis()) / (24L * 60 * 60 * 1000);
        System.out.println("Antigo (milissegundos / 86400000): " + oldDays + " (sujeito a horário de verão)");
        System.out.println("java.time: " + ChronoUnit.DAYS.between(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 25)));

        System.out.println();
        System.out.println("=== 5) Formatação ===");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // NÃO thread-safe
        System.out.println("Antigo: " + sdf.format(c1.getTime()) + " | parse: " + sdf.parse("25/12/2014"));
        System.out.println("java.time: " + LocalDate.parse("2014-12-25")
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        System.out.println();
        System.out.println("=== 6) Conversão entre as APIs (ponte do Java 8) ===");
        Date legacy = new Date(1_395_100_000_000L);
        LocalDateTime converted = LocalDateTime.ofInstant(legacy.toInstant(), ZoneId.systemDefault());
        Date back = Date.from(converted.atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("Date -> LocalDateTime: " + converted);
        System.out.println("LocalDateTime -> Date: " + back + " | iguais? " + legacy.equals(back));
        System.out.println("GregorianCalendar -> ZonedDateTime: " + ((GregorianCalendar) c2).toZonedDateTime());
    }
}
