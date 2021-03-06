package com.evervault.services;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class ResourceControl {
    protected static final int COUNT_LIMIT = 3;
    protected static final long CB_TIME_TO_FREE_MILLISECONDS = 30000;
    protected final ScheduledExecutorService executor;

    protected int counter;
    protected boolean blocked;
    protected final int countLimit;
    protected final long timeToFreeMilliseconds;
    protected Future resetTask;

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

    public void clear() {
        this.counter = 0;
    }

    protected Future reset() {
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
        if (counter >= countLimit) {
            if (resetTask == null || resetTask.isDone()) {
                blocked = true;

                resetTask = reset();
            }
        } else {
            counter++;
        }
    }
}
