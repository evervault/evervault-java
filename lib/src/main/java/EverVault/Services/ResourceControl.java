package EverVault.Services;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ResourceControl {
    public static final int COUNT_LIMIT = 3;
    public static final long CB_TIME_TO_FREE_MILLISECS = 30000;
    private final Runnable unlockTask;

    private int counter;
    private boolean blocked;
    private final int countLimit;
    private final long timeToFreeMilliseconds;

    public boolean getBlocked() {
        return blocked;
    }

    public ResourceControl(int countLimit, long timeToFreeMilliseconds, Runnable timerTask) {
        this.timeToFreeMilliseconds = timeToFreeMilliseconds;
        this.countLimit = countLimit;
        this.unlockTask = Objects.requireNonNullElseGet(timerTask, () -> () -> {
            blocked = false;
            counter = 0;
        });
    }

    public ResourceControl() {
        this(COUNT_LIMIT, CB_TIME_TO_FREE_MILLISECS, null);
    }

    public void timeOutOccurred() {
        if (counter == countLimit) {
            blocked = true;

            var executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(unlockTask, timeToFreeMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            counter++;
        }
    }
}
