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

    // TODO: can make it a bit faster
    // instead of simple synhronized with for blocking if current lock was taken (just replace Object lock on Lock lock)
    // instead of List we have Queue of accounts
    // pop from Queue, can use Lock with timeout 1 second, if TimeoutException then enqueue back in Queue
    // else enqueue the next elem from Queue
    private void checkInterest() {
        System.out.println(WORK_PERIOD && isTodayLastDayOfMonth());
        try {
            if (WORK_PERIOD && isTodayLastDayOfMonth()) {
                List<Bank> banks = bankStorage.get();
                for (Bank bank : banks) {
                    for (Account account : bank.getAccounts()) {
                        synchronized (account.getLock()) {
                            account.addPercent(interest);
                        }
                    }
                }
                threadPool.schedule(this::resumeCheckInterestInDay, 1, TimeUnit.DAYS);
                WORK_PERIOD = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeCheckInterestInDay() {
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
