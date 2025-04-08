package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }
    
    public static LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(dateStr, formatter);
    }
    
    public static boolean isBetween(
        LocalDate test,
        LocalDate start,
        LocalDate end
    ) {
        return (test.isEqual(start) || test.isAfter(start)) &&
           (test.isEqual(end) || test.isBefore(end));
    }
}