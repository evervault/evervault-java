package EverVault.Services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class ResourceControl {
    public static final int COUNT_LIMIT = 3;
    public static final long CB_TIME_TO_FREE_MILLISECONDS = 30000;
    private final ScheduledExecutorService executor;

    private int counter;
    private boolean blocked;
    private final int countLimit;
    private final long timeToFreeMilliseconds;

    public boolean getBlocked() {
        return blocked;
    }

    public ResourceControl(int countLimit, long timeToFreeMilliseconds) {
        this.timeToFreeMilliseconds = timeToFreeMilliseconds;
        this.countLimit = countLimit;
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    public ResourceControl() {
        this(COUNT_LIMIT, CB_TIME_TO_FREE_MILLISECONDS);
    }

    private Future reset() {
        return executor.submit(() -> {
            try {
                Thread.sleep(timeToFreeMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            blocked = false;
            counter = 0;
        });
    }

    public void timeOutOccurred() {
        if (counter == countLimit) {
            blocked = true;

            reset();
        } else {
            counter++;
        }
    }
}
