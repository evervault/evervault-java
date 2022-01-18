package EverVault.Services;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ResourceControl {
    public static final int COUNT_LIMIT = 3;
    public static final long CB_TIME_TO_FREE_MILLISECS = 30000;
    private final TimerTask timerTask;

    private int counter;
    private final Timer timer;
    private boolean blocked;
    private final int countLimit;
    private long timeToFreeMilliseconds;

    public boolean getBlocked() {
        return blocked;
    }

    public ResourceControl(int countLimit, long timeToFreeMilliseconds, TimerTask timerTask) {
        this.timeToFreeMilliseconds = timeToFreeMilliseconds;
        this.timer = new Timer();
        this.countLimit = countLimit;
        this.timerTask = Objects.requireNonNullElseGet(timerTask, () -> new TimerTask() {
            @Override
            public void run() {
                blocked = false;
                counter = 0;
            }
        });
    }

    public ResourceControl() {
        this(COUNT_LIMIT, CB_TIME_TO_FREE_MILLISECS, null);
    }

    public void timeOutOccurred() {
        if (counter == countLimit) {
            this.timer.schedule(timerTask, timeToFreeMilliseconds);
        } else {
            counter++;
        }
    }
}
