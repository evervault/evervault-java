package EverVault.Services;

import java.util.Timer;
import java.util.TimerTask;

public class ResourceControl {
    public static final int COUNT_LIMIT = 3;
    public static final long CB_TIME_TO_FREE_MILLISECS = 30000;

    public int counter;
    public Timer timer;
    public boolean blocked;
    private int countLimit;
    private long timeToFreeMilliseconds;

    public ResourceControl(int countLimit, long timeToFreeMilliseconds) {

        this.countLimit = countLimit;
        this.timeToFreeMilliseconds = timeToFreeMilliseconds;
    }

    public ResourceControl() {
        timeToFreeMilliseconds = CB_TIME_TO_FREE_MILLISECS;
        countLimit = COUNT_LIMIT;
    }

    public void timeOutOccurred() {
//        if (counter == countLimit) {
//            blocked = true;
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    blocked = false;
//                    counter = 0;
//                }
//            }, timeToFreeMilliseconds);
//        } else {
//            counter++;
//        }
    }
}
