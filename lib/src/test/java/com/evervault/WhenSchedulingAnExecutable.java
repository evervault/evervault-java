package com.evervault;

import com.evervault.services.ExecutableSchedulerService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class WhenSchedulingAnExecutable {

    @Test
    public void shouldScheduleAnExecutable() throws InterruptedException {
        // Given
        var executableSchedulerService = new ExecutableSchedulerService(1);
        var counter = new Counter();

        // When
        executableSchedulerService.schedule(() -> {
            counter.increment();
            return null;
        }, 0, 50, TimeUnit.MILLISECONDS);
        Thread.sleep(200);

        // Then
        assert counter.value >= 3;
    }

    private static class Counter {
        public int value = 0;

        public void increment() {
            value++;
        }
    }

}
