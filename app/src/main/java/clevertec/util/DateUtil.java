package clevertec.util;

import java.time.LocalDate;

/**
 * Class that contains methods to work with date.
 */
public class DateUtil {
    private DateUtil() {
    }

    /**
     * If today last day of month (ex: 2022-31-12 and etc) - return true.
     * 
     * @return boolean
     */
    static public boolean isTodayLastDayOfMonth() {
        var today = LocalDate.now();

        var lastdayOfMonth = lastdayInMonthAsDate().getDayOfMonth();

        return today.getDayOfMonth() == lastdayOfMonth;
    }

    /**
     * Get last day in current month and year.
     * 
     * @return LocalDate
     */
    static public LocalDate lastdayInMonthAsDate() {
        var today = LocalDate.now();

        return today.withDayOfMonth(
                today.getMonth().length(today.isLeapYear()));
    }

}
