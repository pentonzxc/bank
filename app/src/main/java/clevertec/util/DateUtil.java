package clevertec.util;

import java.time.LocalDate;

public class DateUtil {
    private DateUtil() {
    }

    static public boolean isTodayLastDayOfMonth() {
        var today = LocalDate.now();

        var lastdayOfMonth = lastdayInMonthAsDate().getDayOfMonth();

        return today.getDayOfMonth() == lastdayOfMonth;
    }

    static public LocalDate lastdayInMonthAsDate() {
        var today = LocalDate.now();

        return today.withDayOfMonth(
                today.getMonth().length(today.isLeapYear()));
    }

}
