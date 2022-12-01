package com.evervault;

import com.evervault.contracts.IExecuteRepeatableTask;
import com.evervault.services.RepeatableTaskSchedulerService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class WhenSchedulingAnExecutable {

    @Test
    public void shouldScheduleAnExecutable() throws InterruptedException {
        // Given
        var executableSchedulerService = new RepeatableTaskSchedulerService(1);
        var counter = new Counter();

        // When
        executableSchedulerService.schedule(
                new IExecuteRepeatableTask(50, TimeUnit.MILLISECONDS) {
                    @Override
                    public void execute() throws Exception {
                        counter.increment();
                    }
                }
        );
        Thread.sleep(190);

        // Then
        assert counter.value == 3;
    }

    @Test
    public void shouldScheduleAnExecutableAndDynamicallyUpdateDelay() throws InterruptedException {
        // Given
        var executableSchedulerService = new RepeatableTaskSchedulerService(1);
        var counter = new Counter();

        // When
        executableSchedulerService.schedule(
                new IExecuteRepeatableTask(50, TimeUnit.MILLISECONDS) {
                    @Override
                    public void execute() throws Exception {
                        counter.increment();
                        updateDelay(100, TimeUnit.MILLISECONDS);
                    }
                }
        );
        Thread.sleep(190);

        // Then
        assert counter.value == 2;
    }

    @Test
    public void shouldSilentlyIgnoreExceptions() throws InterruptedException {
        // Given
        var executableSchedulerService = new RepeatableTaskSchedulerService(1);
        var counter = new Counter();

        // When
        executableSchedulerService.schedule(
                new IExecuteRepeatableTask(5, TimeUnit.MILLISECONDS) {
                    @Override
                    public void execute() throws Exception {
                        throw new Exception();
                    }
                }
        );
        Thread.sleep(100);
    }

    private static class Counter {
        public int value = 0;

        public void increment() {
            value++;
        }
    }

}
