package clevertec.account.interest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import clevertec.account.Account;
import clevertec.config.Config;
import clevertec.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * Сlass that checks whether it is necessary to charge interest at the end of
 * the month on the account.
 * <p>
 * Have thread pool, that run task checker.
 * 
 * @see java.util.concurrent.ScheduledThreadPoolExecutor
 * 
 */
public class InterestChecker {

    private final double INTEREST_PERCENT = Config.getProperty("INTEREST_PERCENT", Double::parseDouble);
    private final long INTEREST_CHECK_TIME_DELAY = Config.getProperty("INTEREST_CHECK_TIME_DELAY", Long::parseLong);
    private final long INTEREST_FIRST_CHECK_DELAY = Config.getProperty("INTEREST_FIRST_CHECK_DELAY", Long::parseLong);
    private final long RESUME_WORK_DELAY = 1000 * 60 * 60 * 24;

    volatile private boolean working = true;

    protected final ScheduledThreadPoolExecutor threadPool;

    private ScheduledFuture<Void> interestTask;

    @Setter
    @Getter
    private Supplier<List<Account>> accounts;

    protected InterestChecker(int threadsCount, boolean daemon) {
        if (daemon) {
            // Behaviour like static methods of CompletableFuture
            this.threadPool = new ScheduledThreadPoolExecutor(threadsCount, new DaemonThreadFactory());
        } else {
            this.threadPool = new ScheduledThreadPoolExecutor(threadsCount);
        }
        this.threadPool.setRemoveOnCancelPolicy(true);
    }

    /**
     * This method run task checker.
     * <p>
     * Call maximum once, calls after ignore - until task is alive.
     * 
     * @see InterestChecker#stop
     * @see java.util.concurrent.ScheduledFuture
     * @return State of running task.
     */
    @SuppressWarnings("unchecked")
    public ScheduledFuture<Void> run(Supplier<List<Account>> accounts) {
        if ((interestTask != null && interestTask.isCancelled()) || interestTask == null) {
            interestTask = (ScheduledFuture<Void>) threadPool.scheduleWithFixedDelay(
                    this::checkInterest,
                    INTEREST_FIRST_CHECK_DELAY,
                    INTEREST_CHECK_TIME_DELAY,
                    TimeUnit.MILLISECONDS);
        }
        return interestTask;
    }

    /**
     * This method run task checker without providing accounts.
     * <p>
     * If accounts don't exist throw {@link AssertionError}.
     * 
     * @see InterestChecker#stop
     * @see AssertionError
     * @see java.util.concurrent.ScheduledFuture
     * @return State of running task.
     */
    public ScheduledFuture<Void> run() {
        assert accounts != null;
        return run(accounts);
    }

    /**
     * Stop the running task.
     * 
     * @see InterestChecker#run
     * @see java.util.concurrent.Future
     * @return State of cancelation task.
     */
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

    /**
     * Get the instance of class.
     * 
     * @param threadsCount - amount of threads in ThreadPool.
     * @param daemon       - if true set the {@link DaemonThreadFactory}.
     * @return {@link InterestChecker}
     */
    static InterestChecker instance(int threadsCount, boolean daemon) {
        return new InterestChecker(threadsCount, daemon);
    }

    // TODO: to make faster
    // instead of using for comprehensions with synchronized use Lock with timeout
    // instead of List we have Queue of accounts
    // pop from Queue, take lock on account setting timeout 1 second
    // if timeout is happened => put account back in queue
    private void checkInterest() {
        System.out.println(working && isTodayLastDayOfMonth());
        try {
            if (working && isTodayLastDayOfMonth()) {
                List<Account> accounts_ = accounts.get();
                for (Account account : accounts_) {
                    synchronized (account.getLock()) {
                        account.addPercent(INTEREST_PERCENT);
                    }
                }
                threadPool.schedule(this::resumeCheckInterest, RESUME_WORK_DELAY, TimeUnit.MILLISECONDS);
                working = false;
            }
        } catch (

        Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resumeCheckInterest() {
        working = true;
    }

    /**
     * @return boolean
     */
    protected boolean isTodayLastDayOfMonth() {
        return DateUtil.isTodayLastDayOfMonth();
    }

    /**
     * Thread factory which create daemon threads.
     * 
     * @see java.lang.Thread#setDaemon(boolean)
     * 
     */
    static class DaemonThreadFactory implements ThreadFactory {

        /**
         * Create new daemon thread.
         * 
         * @param {@link Runnable}
         * @see java.lang.Thread#setDaemon(boolean)
         * @return {@link Thread}
         */
        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);

            return t;
        }

    }
}
