package com.evervault.services;

import com.evervault.contracts.IExecute;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutableSchedulerService {

    private final ScheduledExecutorService scheduledExecutor;

    private final Map<IExecute, Future<?>> scheduledFutures;

    public ExecutableSchedulerService(int threadPoolSize) {
        scheduledFutures = new HashMap<>();
        scheduledExecutor = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public <TReturn> void schedule(IExecute<TReturn> executable, int initialDelay, int period, TimeUnit timeUnit) {
        if (scheduledFutures.containsKey(executable)) {
            return;
        }
        var future = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                executable.execute();
            } catch (Exception e) {
                // Silently ignoring exceptions
            }
        }, initialDelay, period, timeUnit);
        scheduledFutures.put(executable, future);
    }

    public <TReturn> void cancel(IExecute<TReturn> executable) {
        if (!scheduledFutures.containsKey(executable)) {
            return;
        }
        scheduledFutures.get(executable).cancel(true);
    }

}
