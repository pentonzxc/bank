package clevertec;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InterestCheckerTest {
    InterestChecker instance = InterestChecker.instance();

    @AfterEach
    void afterEach() {
        Future<Void> stopTask = instance.stop();
        try {
            stopTask.get();
        } catch (Exception ex) {
            throw new RuntimeException("Can't stop task", ex);
        }
    }

    @Test
    public void whenInterestRun_expectedTaskIsNeverDone() {
        ScheduledFuture<?> future = instance.run();
        try {
            future.get(1, TimeUnit.MINUTES);
        } catch (Exception ex) {
            assertInstanceOf(TimeoutException.class, ex);
        }
    }

    @Test
    public void whenInterestRun_thenRunAgain_expectedIgnore() throws InterruptedException {
        ScheduledFuture<Void> task1 = instance.run();
        ScheduledFuture<Void> task2 = instance.run();
        ScheduledFuture<Void> task3 = instance.run();

        Assertions.assertSame(task1, task2);
        Assertions.assertSame(task2, task3);
    }

    @Test
    public void whenRun_thenImmediatelyStop_expectedTaskStopped() {
        instance.run();
        Future<Void> stopTask = instance.stop();
        try {
            stopTask.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Order(Integer.MIN_VALUE)
    @Test
    public void onEmpty() {
        try {
            instance.stop().get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // write to test on check bussinec logic

    

    // TODO: write advanced test which check possibility of deadlocks

}
