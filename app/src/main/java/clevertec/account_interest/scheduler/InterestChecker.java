package clevertec.account_interest.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import clevertec.Account;
import clevertec.Bank;
import clevertec.util.DateUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class InterestChecker {

    private double interest = 1;

    private ScheduledFuture<Void> interestTask;

    // private ScheduledFuture<?> resumeInterestTask;

    private Supplier<List<Bank>> bankStorage;

    final protected ScheduledThreadPoolExecutor threadPool;

    volatile private boolean WORK_PERIOD = true;

    protected InterestChecker(int threadsCount, boolean daemon) {
        if (daemon) {
            // Behaviour like static methods of CompletableFuture
            this.threadPool = new ScheduledThreadPoolExecutor(threadsCount, new DaemonThreadFactory());
        } else {
            this.threadPool = new ScheduledThreadPoolExecutor(threadsCount);
        }
        this.threadPool.setRemoveOnCancelPolicy(true);
    }

    @SuppressWarnings("unchecked")
    public ScheduledFuture<Void> run() {
        if ((interestTask != null && interestTask.isCancelled()) || interestTask == null) {
            interestTask = (ScheduledFuture<Void>) threadPool.scheduleWithFixedDelay(this::checkInterest, 10, 30,
                    TimeUnit.SECONDS);
        }
        // System.out.println("Active count :: " + threadPool.getActiveCount());
        // System.out.println("Size of queue :: " + threadPool.getQueue().size());
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

    static InterestChecker instance(int threadsCount, boolean daemon) {
        return new InterestChecker(threadsCount, daemon);
    }

    private void checkInterest() {
        // System.out.println("in check interest");
        System.out.println(WORK_PERIOD && isTodayLastDayOfMonth());
        try {
            if (WORK_PERIOD && isTodayLastDayOfMonth()) {

                List<Bank> banks = bankStorage.get();
                for (Bank bank : banks) {
                    // System.out.println("in banks");
                    for (Account account : bank.getAccounts()) {
                        synchronized (account.getLock()) {
                            // System.out.println("add");
                            account.addPercent(interest);
                        }
                    }
                }

                // resumeInterestTask = threadPool.schedule(this::resumeCheckInterestInDay, 1,
                // TimeUnit.DAYS);
                threadPool.schedule(this::resumeCheckInterestInDay, 1, TimeUnit.DAYS);
                WORK_PERIOD = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeCheckInterestInDay() {
        // if (!resumeInterestTask.isCancelled()) {
        // resumeInterestTask.cancel(true);
        // }
        WORK_PERIOD = true;
    }

    protected boolean isTodayLastDayOfMonth() {
        return DateUtil.isTodayLastDayOfMonth();
    }

    public Supplier<List<Bank>> getBankStorage() {
        return bankStorage;
    }

    public void setBankStorage(Supplier<List<Bank>> bankStorage) {
        this.bankStorage = bankStorage;
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);

            return t;
        }

    }
}
