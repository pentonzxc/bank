package clevertec;

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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class InterestCheckerTest {
    private TestInstanceInterestChecker checker = new TestInstanceInterestChecker(true, false);

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
    public void whenInterestRun_expectTaskIsNeverDone() {
        ScheduledFuture<Void> future = checker.run();
        try {
            future.get(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            assertInstanceOf(TimeoutException.class, ex);
        }
    }

    @Test
    public void whenInterestRun_thenRunAgain_expectIgnoreNewRunCalls() throws InterruptedException {
        ScheduledFuture<Void> task1 = checker.run();
        ScheduledFuture<Void> task2 = checker.run();
        ScheduledFuture<Void> task3 = checker.run();

        Assertions.assertSame(task1, task2);
        Assertions.assertSame(task2, task3);
    }

    @Test
    public void whenRun_thenImmediatelyStop_expectTaskStopped() {
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
    public void whenNotRun_thenCallStop_expectIgnoreStopCall() {
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
        List<Bank> banks = (List<Bank>) FindAllService.allEntities()[1];
        double sumBefore = sumMoneyOfAllAccountsInBanks(banks);
        checker.setBankStorage(() -> banks);

        checker.run();

        Thread.sleep(30000);
        double sumAfter = sumMoneyOfAllAccountsInBanks(banks);
        checker = checker.newInstance();
        
        assertTrue(sumBefore == sumAfter);

    }

    @Test
    void whenLastDayInMonth_expectMoreMoneyOnAccounts() throws InterruptedException {
        List<Bank> banks = (List<Bank>) FindAllService.allEntities()[1];
        double sumBefore = sumMoneyOfAllAccountsInBanks(banks);

        checker.setBankStorage(() -> banks);
        checker.setMockDate(true);
        checker.run();

        Thread.sleep(30000);
        double sumAfter = sumMoneyOfAllAccountsInBanks(banks);
        checker = checker.newInstance();

        assertTrue(sumAfter > sumBefore || (sumAfter == sumBefore && sumBefore == 0));

    }

    @Test
    void whenLastDayMonth_expectOnlyOneRaiseOfMoneyOnAccounts() throws InterruptedException {
        List<Bank> banks = (List<Bank>) FindAllService.allEntities()[1];

        checker.setBankStorage(() -> banks);
        checker.setMockDate(true);
        checker.run();

        Thread.sleep(30000);
        double sumAfter = sumMoneyOfAllAccountsInBanks(banks);

        Thread.sleep(50000);
        double sumAfterAfter = sumMoneyOfAllAccountsInBanks(banks);
        checker = checker.newInstance();

        assertTrue(sumAfterAfter == sumAfter);

    }

    // TODO: write advanced test which check possibility of deadlocks

    private double sumMoneyOfAllAccountsInBanks(List<Bank> banks) {
        double[] sum = new double[] { 0d };

        banks.forEach(b -> b.accounts.forEach(a -> sum[0] += a.getMoney()));

        return sum[0];
    }

}
