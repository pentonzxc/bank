package clevertec.account_interest.scheduler;

/**
 * Thread safe factory for {@link InterestChecker}.
 * 
 */
public class InterestCheckerFactory {
    private static volatile InterestChecker instance;

    private InterestCheckerFactory() {
    }

    /**
     * Factory pattern.
     * <p>
     * Create instance only once, in thread safe way.
     * 
     * @return {@link InterestChecker}
     */
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
