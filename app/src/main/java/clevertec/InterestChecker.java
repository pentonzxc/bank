package clevertec;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

public class InterestChecker {

    private static InterestChecker INSTANCE;

    private double interest = 1;

    private ScheduledFuture<Void> interestTask;

    private ScheduledFuture<?> resumeInterestTask;

    public List<Bank> bankStorage = BankStorage.getAll();

    final private ScheduledThreadPoolExecutor threadPool;

    private boolean WORK_PERIOD = true;

    private InterestChecker(int threadsCount) {
        threadPool = new ScheduledThreadPoolExecutor(threadsCount);
        threadPool.setRemoveOnCancelPolicy(true);
    }

    @SuppressWarnings("unchecked")
    public ScheduledFuture<Void> run() {
        if ((interestTask != null && interestTask.isCancelled()) || interestTask == null) {
            interestTask = (ScheduledFuture<Void>) threadPool.scheduleWithFixedDelay(this::checkInterest, 10, 30,
                    TimeUnit.SECONDS);
        }
        System.out.println("Active count :: " + threadPool.getActiveCount());
        System.out.println("Size of queue :: " + threadPool.getQueue().size());
        return interestTask;
    }

    public Future<Void> stop() {
        if (interestTask == null || interestTask.isCancelled()) {
            return CompletableFuture.completedFuture(null);
        }
        interestTask.cancel(true);
        return CompletableFuture.runAsync(() -> {
            while (interestTask.getDelay(TimeUnit.MILLISECONDS) >= 0) {
            }
        });
    }

    public static InterestChecker instance() {
        // P.S : I know about double synchronized
        if (INSTANCE == null) {
            synchronized (InterestChecker.class) {
                return new InterestChecker(2);
            }
        }
        return INSTANCE;
    }

    private void checkInterest() {
        System.out.print("I'm gay");
        try {
            if (WORK_PERIOD && isTodayLastDayOfMonth()) {

                List<Bank> banks = bankStorage;
                for (Bank bank : banks) {
                    for (Account account : bank.getAccounts()) {
                        synchronized (account.getLOCK()) {
                            account.addPercent(interest);
                        }
                    }
                }

                resumeInterestTask = threadPool.schedule(this::resumeCheckInterestInDay, 1, TimeUnit.DAYS);
                WORK_PERIOD = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
