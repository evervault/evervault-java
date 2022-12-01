package com.evervault.services;

import com.evervault.contracts.IExecuteRepeatableTask;
import com.evervault.contracts.IScheduleRepeatableTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RepeatableTaskSchedulerService implements IScheduleRepeatableTask {

    private final ScheduledExecutorService scheduledExecutor;

    private final Map<IExecuteRepeatableTask, Future<?>> scheduledFutures;

    public RepeatableTaskSchedulerService(int threadPoolSize) {
        scheduledFutures = new HashMap<>();
        scheduledExecutor = Executors.newScheduledThreadPool(threadPoolSize);
    }

    @Override
    public void schedule(IExecuteRepeatableTask executable) {
        var future = scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    executable.execute();
                } catch (Exception e) {
                    // Silently ignoring exceptions
                } finally {
                    scheduledExecutor.schedule(this, executable.getDelay(), executable.getTimeUnit());
                }
            }
        }, executable.getDelay(), executable.getTimeUnit());
        scheduledFutures.put(executable, future);
    }

    public void cancel(IExecuteRepeatableTask executable) {
        if (!scheduledFutures.containsKey(executable)) {
            return;
        }
        scheduledFutures.get(executable).cancel(true);
    }

}
