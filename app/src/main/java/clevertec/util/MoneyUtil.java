package clevertec.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {
    private MoneyUtil() {
    }

    /**
     * Round double to specified places.
     * 
     * @param numeric
     * @param places
     * @return double
     */
    public static double round(double numeric, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(numeric);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Round double to 2 places.
     * 
     * @param numeric
     * @return double
     */
    public static double roundMoney(double numeric) {
        return round(numeric, 2);
    }

}
