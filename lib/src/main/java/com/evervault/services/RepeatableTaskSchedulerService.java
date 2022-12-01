package com.evervault.services;

import com.evervault.contracts.IExecuteRepeatableTask;
import com.evervault.contracts.IScheduleRepeatableTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RepeatableTaskSchedulerService implements IScheduleRepeatableTask {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;

    private final ScheduledExecutorService scheduledExecutor;

    private final Map<IExecuteRepeatableTask, Future<?>> scheduledFutures;

    public RepeatableTaskSchedulerService() {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    public RepeatableTaskSchedulerService(int threadPoolSize) {
        scheduledFutures = new HashMap<>();
        scheduledExecutor = Executors.newScheduledThreadPool(threadPoolSize);
    }

    @Override
    public void schedule(IExecuteRepeatableTask task) {
        var future = scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    task.execute();
                } catch (Exception e) {
                    // Silently ignoring exceptions
                } finally {
                    scheduledExecutor.schedule(this, task.getDelay(), task.getTimeUnit());
                }
            }
        }, task.getDelay(), task.getTimeUnit());
        scheduledFutures.put(task, future);
    }

    public void cancel(IExecuteRepeatableTask executable) {
        if (!scheduledFutures.containsKey(executable)) {
            return;
        }
        scheduledFutures.get(executable).cancel(true);
    }

}
