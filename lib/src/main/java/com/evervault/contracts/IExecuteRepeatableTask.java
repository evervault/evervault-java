package com.evervault.contracts;

import java.util.concurrent.TimeUnit;

public abstract class IExecuteRepeatableTask {
    private int delay;
    private TimeUnit timeUnit;

    public IExecuteRepeatableTask(int delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public abstract void execute() throws Exception;

    public void updateDelay(int delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public int getDelay() {
        return delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
