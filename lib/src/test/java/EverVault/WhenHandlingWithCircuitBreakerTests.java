package EverVault;

import EverVault.Contracts.IExecuteWithPossibleHttpTimeout;
import EverVault.Exceptions.MaxRetryReachedException;
import EverVault.Services.CircuitBreaker;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WhenHandlingWithCircuitBreakerTests {
    @Test
    void triesToExecuteThePassedMethod() throws MaxRetryReachedException, HttpTimeoutException {
        var execution = mock(IExecuteWithPossibleHttpTimeout.class);

        var circuitBreaker = new CircuitBreaker();

        circuitBreaker.execute(0, execution);

        verify(execution, times(1)).execute();
    }

    @Test
    void throwsMaxRetriesReachedWhenReachesLimitOfTimeouts() throws HttpTimeoutException, MaxRetryReachedException {
        var execution = mock(IExecuteWithPossibleHttpTimeout.class);

        when(execution.execute()).thenThrow(new HttpTimeoutException("foo"));

        var circuitBreaker = new CircuitBreaker(0, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));
    }

    @Test
    void handlesDifferentMethodsOnDifferentResources() throws HttpTimeoutException {
        var executionOne = mock(IExecuteWithPossibleHttpTimeout.class);
        var executionTwo = mock(IExecuteWithPossibleHttpTimeout.class);

        when(executionOne.execute()).thenThrow(new HttpTimeoutException("Foo"));
        when(executionTwo.execute()).thenThrow(new HttpTimeoutException("Foo"));

        var circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, executionOne));
        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(1, executionTwo));

        verify(executionOne, times(2)).execute();
        verify(executionTwo, times(2)).execute();
    }

    @Test
    void retriesIfItFails() throws HttpTimeoutException {
        var execution = mock(IExecuteWithPossibleHttpTimeout.class);

        when(execution.execute()).thenThrow(new HttpTimeoutException("foo"));

        var circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));

        verify(execution, times(2)).execute();
    }

    @Test
    void returnTheContentFromExecuteBack() throws MaxRetryReachedException, HttpTimeoutException {
        final var returnContent = "Foo";
        var execution = mock(IExecuteWithPossibleHttpTimeout.class);

        when(execution.execute()).thenReturn(returnContent);
        var circuitBreaker = new CircuitBreaker();

        var result = circuitBreaker.execute(0, execution);

        assertEquals(returnContent, result);
    }
}
