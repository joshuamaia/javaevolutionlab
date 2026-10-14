package br.com.javalab.java08.datetime;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Java 8 - Date and Time API: java.time (JEP 150 / JSR 310)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: nova API de datas inspirada no Joda-Time: tipos imutáveis e thread-safe
 * ({@code LocalDate}, {@code LocalTime}, {@code LocalDateTime}, {@code ZonedDateTime},
 * {@code Instant}), quantidades ({@code Period} para datas, {@code Duration} para tempo)
 * e {@code DateTimeFormatter} (thread-safe).</p>
 * <p>Benefício: modelo claro (data sem hora, hora sem fuso...), meses de 1 a 12,
 * aritmética fácil e fusos horários corretos.</p>
 * <p>Antes: {@code java.util.Date}/{@code Calendar} mutáveis e {@code SimpleDateFormat}
 * não thread-safe (ver {@code DateVsDateTimeApiComparison}).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java08/datetime/DateTimeApiExample.java
 * </pre>
 */
public class DateTimeApiExample {

    public static void main(String[] args) {
        System.out.println("=== LocalDate: data sem hora ===");
        LocalDate orderDate = LocalDate.of(2014, Month.MARCH, 18); // lançamento do Java 8
        LocalDate dueDate = orderDate.plusDays(30);
        System.out.println("Pedido: " + orderDate + " (" + orderDate.getDayOfWeek() + ")");
        System.out.println("Vencimento (+30 dias): " + dueDate);
        System.out.println("Último dia do mês: " + orderDate.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println("Próxima segunda: " + orderDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        System.out.println("Ano bissexto? " + orderDate.isLeapYear());

        System.out.println();
        System.out.println("=== LocalTime e LocalDateTime ===");
        LocalTime opening = LocalTime.of(9, 0);
        LocalDateTime meeting = LocalDateTime.of(orderDate, LocalTime.of(14, 30));
        System.out.println("Abertura: " + opening + " | fechamento: " + opening.plusHours(9));
        System.out.println("Reunião: " + meeting + " | adiada: " + meeting.plusWeeks(1).withHour(10));

        System.out.println();
        System.out.println("=== ZonedDateTime: fusos horários ===");
        ZonedDateTime saoPaulo = ZonedDateTime.of(meeting, ZoneId.of("America/Sao_Paulo"));
        ZonedDateTime tokyo = saoPaulo.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println("São Paulo: " + saoPaulo);
        System.out.println("Tóquio:    " + tokyo);
        System.out.println("Instant (UTC): " + saoPaulo.toInstant());

        System.out.println();
        System.out.println("=== Period (datas) e Duration (tempo) ===");
        LocalDate birth = LocalDate.of(1995, 5, 23);
        Period age = Period.between(birth, orderDate);
        System.out.println("Período: " + age + " -> " + age.getYears() + " anos, " + age.getMonths() + " meses");
        System.out.println("Dias entre datas: " + ChronoUnit.DAYS.between(orderDate, dueDate));
        Duration shift = Duration.between(LocalTime.of(8, 15), LocalTime.of(17, 45));
        System.out.println("Turno: " + shift + " = " + shift.toMinutes() + " minutos");
        Instant start = Instant.now();
        Duration elapsed = Duration.between(start, start.plusMillis(1500));
        System.out.println("Duration de 1500ms: " + elapsed);

        System.out.println();
        System.out.println("=== DateTimeFormatter (imutável e thread-safe) ===");
        DateTimeFormatter br = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("Formatado: " + meeting.format(br));
        System.out.println("ISO: " + meeting.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        LocalDate parsed = LocalDate.parse("25/12/2014", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("Parse de '25/12/2014': " + parsed + " (" + parsed.getDayOfWeek() + ")");

        System.out.println();
        System.out.println("=== Imutabilidade ===");
        LocalDate original = LocalDate.of(2014, 1, 1);
        original.plusYears(1); // retorno ignorado: original não muda
        System.out.println("original continua " + original);
    }
}
