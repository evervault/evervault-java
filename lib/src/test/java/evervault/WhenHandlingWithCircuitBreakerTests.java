package evervault;

import evervault.contracts.IExecute;
import evervault.exceptions.HttpFailureException;
import evervault.exceptions.MaxRetryReachedException;
import evervault.exceptions.NotPossibleToHandleDataTypeException;
import evervault.services.CircuitBreaker;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.http.HttpTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WhenHandlingWithCircuitBreakerTests {
    @Test
    void triesToExecuteThePassedMethod() throws MaxRetryReachedException, IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        var execution = mock(IExecute.class);

        var circuitBreaker = new CircuitBreaker();

        circuitBreaker.execute(0, execution);

        verify(execution, times(1)).execute();
    }

    @Test
    void throwsMaxRetriesReachedWhenReachesLimitOfTimeouts() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        var execution = mock(IExecute.class);

        when(execution.execute()).thenThrow(new HttpTimeoutException("foo"));

        var circuitBreaker = new CircuitBreaker(0, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));
    }

    @Test
    void handlesDifferentMethodsOnDifferentResources() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        var executionOne = mock(IExecute.class);
        var executionTwo = mock(IExecute.class);

        when(executionOne.execute()).thenThrow(new HttpTimeoutException("Foo"));
        when(executionTwo.execute()).thenThrow(new HttpTimeoutException("Foo"));

        var circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, executionOne));
        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(1, executionTwo));

        verify(executionOne, times(2)).execute();
        verify(executionTwo, times(2)).execute();
    }

    @Test
    void retriesIfItFails() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        var execution = mock(IExecute.class);

        when(execution.execute()).thenThrow(new HttpTimeoutException("foo"));

        var circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));

        verify(execution, times(2)).execute();
    }

    @Test
    void returnTheContentFromExecuteBack() throws MaxRetryReachedException, IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        final var returnContent = "Foo";
        var execution = mock(IExecute.class);

        when(execution.execute()).thenReturn(returnContent);
        var circuitBreaker = new CircuitBreaker();

        var result = circuitBreaker.execute(0, execution);

        assertEquals(returnContent, result);
    }

    @Test
    void workingCorrectlyResetsCounter() throws IOException, MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        var execution = mock(IExecute.class);
        var sameExecId = mock(IExecute.class);

        when(sameExecId.execute()).thenThrow(new HttpTimeoutException("Foo"));
        when(execution.execute()).thenAnswer(new Answer<String>() {
            private int nCall = 0;

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (nCall > 0) {
                    return "Foo";
                }
                nCall++;

                throw new HttpTimeoutException("test");
            }
        });

        var circuitBreaker = new CircuitBreaker(1, 10);
        circuitBreaker.execute(0, execution);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, sameExecId));

        verify(sameExecId, times(2)).execute();
    }
}
