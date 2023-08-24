package clevertec;

import java.time.LocalDate;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InterestChecker {

    private static InterestChecker INSTANCE;

    private Double interest;

    private ScheduledFuture<?> interestTask;

    private ScheduledFuture<?> resumeInterestTask;

    final private ScheduledThreadPoolExecutor threadPool;

    private boolean WORK_PERIOD = true;

    private InterestChecker(int threadsCount) {
        threadPool = new ScheduledThreadPoolExecutor(threadsCount);
        threadPool.setRemoveOnCancelPolicy(true);
    }

    public ScheduledFuture<?> run() {
        if ((interestTask != null && interestTask.isCancelled()) || interestTask == null) {
            interestTask = threadPool.scheduleWithFixedDelay(this::checkInterest, 0, 30, TimeUnit.SECONDS);
        }
        return interestTask;
    }

    public boolean stop() {
        if (interestTask.isCancelled()) {
            return false;
        }
        interestTask.cancel(true);
        return true;
    }

    public static InterestChecker instance() {
        // P.S : I know about double synchronized
        if (INSTANCE == null) {
            synchronized (InterestChecker.class) {
                return new InterestChecker(1);
            }
        }
        return INSTANCE;
    }

    private void checkInterest() {
        if (WORK_PERIOD && isTodayLastDayOfMonth()) {
            // remove the task and add in a day

            // List<Bank> banks = BankStorage.getAll();
            // for (var bank : banks) {
            // for (Account account : bank.accounts) {
            // synchronized (account.getLOCK()) {
            // account.addPercent(interest);
            // }
            // }
            // }

            resumeInterestTask = threadPool.schedule(this::resumeCheckInterestInDay, 1, TimeUnit.DAYS);
            WORK_PERIOD = false;
        }
    }

    private void resumeCheckInterestInDay() {
        if (!resumeInterestTask.isCancelled()) {
            resumeInterestTask.cancel(true);
        }
        WORK_PERIOD = true;
    }

    private boolean isTodayLastDayOfMonth() {
        var today = LocalDate.now();

        var lastdayOfMonth = today.withDayOfMonth(
                today.getMonth().length(today.isLeapYear())).getDayOfMonth();

        return today.getDayOfMonth() == lastdayOfMonth;
    }
}
