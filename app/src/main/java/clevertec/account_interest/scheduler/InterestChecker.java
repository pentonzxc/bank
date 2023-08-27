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
import clevertec.config.Config;
import clevertec.util.DateUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class InterestChecker {

    private final double INTEREST_PERCENT = Config.getProperty("INTEREST_PERCENT", Double::parseDouble);
    private final long INTEREST_CHECK_TIME_DELAY = Config.getProperty("INTEREST_CHECK_TIME_DELAY", Long::parseLong);
    private final long INTEREST_FIRST_CHECK_DELAY = Config.getProperty("INTEREST_FIRST_CHECK_DELAY", Long::parseLong);
    private final long RESUME_WORK_DELAY = 1000 * 60 * 60 * 24;

    volatile private boolean working = true;

    protected final ScheduledThreadPoolExecutor threadPool;

    private ScheduledFuture<Void> interestTask;

    private Supplier<List<Bank>> bankStorage;


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
            interestTask = (ScheduledFuture<Void>) threadPool.scheduleWithFixedDelay(
                    this::checkInterest,
                    INTEREST_FIRST_CHECK_DELAY,
                    INTEREST_CHECK_TIME_DELAY,
                    TimeUnit.MILLISECONDS);
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
    // instead of simple synhronized with for blocking if current lock was taken
    // (just replace Object lock on Lock lock)
    // instead of List we have Queue of accounts
    // pop from Queue, can use Lock with timeout 1 second, if TimeoutException then
    // enqueue back in Queue
    // else enqueue the next elem from Queue
    private void checkInterest() {
        System.out.println(working && isTodayLastDayOfMonth());
        try {
            if (working && isTodayLastDayOfMonth()) {
                List<Bank> banks = bankStorage.get();
                for (Bank bank : banks) {
                    for (Account account : bank.getAccounts()) {
                        synchronized (account.getLock()) {
                            account.addPercent(INTEREST_PERCENT);
                        }
                    }
                }
                threadPool.schedule(this::resumeCheckInterest, RESUME_WORK_DELAY, TimeUnit.MILLISECONDS);
                working = false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeCheckInterest() {
        working = true;
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
