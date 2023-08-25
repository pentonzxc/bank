package clevertec.account_interest.scheduler;

public class InterestCheckerFactory {
    private static volatile InterestChecker instance;

    private InterestCheckerFactory() {
    }

    static public InterestChecker aInterestChecker() {
        InterestChecker current = instance;
        if (current == null) {
            synchronized (InterestChecker.class) {
                current = instance;

                if (current == null) {
                    instance = current = InterestChecker.instance(2, false);
                }
            }
        }
        return current;
    }
}
