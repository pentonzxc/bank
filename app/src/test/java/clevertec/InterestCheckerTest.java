package clevertec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import clevertec.account.Account;
import clevertec.config.Config;
import clevertec.service.Instances;
import clevertec.util.MoneyUtil;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class InterestCheckerTest {
    private TestInstanceInterestChecker checker = new TestInstanceInterestChecker(true, false);

    {
        Account acc1 = new Account();
        Account acc2 = new Account();

        acc1.setBalance(100);
        acc2.setBalance(100.20);

        checker.setAccounts(() -> List.of(acc1, acc2));
    }

    @AfterEach
    void afterEach() {
        checker.setMockDate(false);
        Future<Void> stopTask = checker.stop();
        try {
            stopTask.get();
        } catch (Exception ex) {
            throw new RuntimeException("Can't stop task", ex);
        }
    }

    @Test
    void whenInterestRun_expectTaskIsNeverDone() {
        ScheduledFuture<Void> future = checker.run();
        try {
            future.get(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            assertInstanceOf(TimeoutException.class, ex);
        }
    }

    /**
     * @throws InterruptedException
     */
    @Test
    void whenInterestRun_thenRunAgain_expectIgnoreNewRunCalls() throws InterruptedException {
        ScheduledFuture<Void> task1 = checker.run();
        ScheduledFuture<Void> task2 = checker.run();
        ScheduledFuture<Void> task3 = checker.run();

        Assertions.assertSame(task1, task2);
        Assertions.assertSame(task2, task3);
    }

    @Test
    void whenRun_thenImmediatelyStop_expectTaskStopped() {
        Exception ex = null;

        checker.run();
        Future<Void> stopTask = checker.stop();
        try {
            stopTask.get();
        } catch (Exception ex_) {
            ex = ex_;
        }

        assertNull(ex, ex != null ? ex.getMessage() : "");
    }

    @Test
    void whenNotRun_thenCallStop_expectIgnoreStopCall() {
        Exception ex = null;

        try {
            checker.stop().get(5, TimeUnit.SECONDS);
        } catch (Exception ex_) {
            ex = ex_;
        }

        assertNull(ex, ex != null ? ex.getMessage() : "");
    }

    @Test
    void whenNotLastDayInMonth_expectSameMoneyOnAccounts() throws InterruptedException {
        List<Account> accounts = Instances.accountService().readAll();
        double sumBefore = balanceSumAllAccounts(accounts);
        checker.setAccounts(() -> accounts);

        checker.run();

        Thread.sleep(30000);
        double sumAfter = balanceSumAllAccounts(accounts);
        checker = checker.newInstance();

        assertTrue(sumBefore == sumAfter);

    }

    @Test
    void whenLastDayInMonth_expectMoreMoneyOnAccounts() throws InterruptedException {
        List<Account> accounts = Instances.accountService().readAll();
        double sumBefore = balanceSumAllAccounts(accounts);
        Double expectedSum = MoneyUtil
                .roundMoney(sumBefore * (1 + Config.getProperty("INTEREST_PERCENT", Double::parseDouble) / 100));

        checker.setAccounts(() -> accounts);
        checker.setMockDate(true);
        checker.run();

        Thread.sleep(30000);
        double sumAfter = balanceSumAllAccounts(accounts);
        checker = checker.newInstance();

        assertTrue(sumAfter > sumBefore || (sumAfter == sumBefore && sumBefore == 0));
        assertEquals(expectedSum, sumAfter);
    }

    @Test
    void whenLastDayMonth_expectOnlyOneRaiseOfMoneyOnAccounts() throws InterruptedException {
        List<Account> accounts = Instances.accountService().readAll();

        checker.setAccounts(() -> accounts);
        checker.setMockDate(true);
        checker.run();

        Thread.sleep(30000);
        double sumAfter = balanceSumAllAccounts(accounts);

        Thread.sleep(50000);
        double sumAfterAfter = balanceSumAllAccounts(accounts);
        checker = checker.newInstance();

        assertTrue(sumAfterAfter == sumAfter);
    }

    // TODO: write advanced test which check possibility of deadlocks

    private double balanceSumAllAccounts(List<Account> accounts) {
        double[] sum = new double[] { 0d };

        // banks.forEach(b -> b.accounts.forEach(a -> sum[0] += a.getBalance()));

        return MoneyUtil.roundMoney(sum[0]);
    }

}
