package clevertec;

import java.util.concurrent.Future;

import clevertec.account.interest.InterestChecker;
import clevertec.config.Config;

public class TestInstanceInterestChecker extends InterestChecker {

    private boolean mockDate;

    private boolean daemon;

    private TestInstanceInterestChecker(int threadCount, boolean daemon, boolean mockDate) {
        super(threadCount, daemon);
        this.daemon = daemon;
        this.mockDate = mockDate;
    }

    public TestInstanceInterestChecker(boolean daemon, boolean mockDate) {
        this(2, daemon, mockDate);
    }

    public TestInstanceInterestChecker() {
        this(2, false, false);
    }

    
    /** 
     * @return boolean
     */
    @Override
    protected boolean isTodayLastDayOfMonth() {
        return this.mockDate;
    }

    public boolean a() {
        return isTodayLastDayOfMonth();
    }

    public void setMockDate(boolean mock) {
        this.mockDate = mock;
    }

    public boolean isMockDate() {
        return this.mockDate;
    }

    @Override
    public Future<Void> stop() {
        return super.stop();
    }

    public TestInstanceInterestChecker newInstance() {
        this.threadPool.shutdownNow();
        return new TestInstanceInterestChecker(this.daemon, this.mockDate);
    }

}
