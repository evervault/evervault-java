package com.evervault;

import com.evervault.exceptions.HttpFailureException;
import com.evervault.exceptions.MaxRetryReachedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import com.evervault.services.CircuitBreaker;
import com.evervault.contracts.IExecute;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WhenHandlingWithCircuitBreakerTests {
    @Test
    void triesToExecuteThePassedMethod() throws MaxRetryReachedException, IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        IExecute execution = mock(IExecute.class);

        CircuitBreaker circuitBreaker = new CircuitBreaker();

        circuitBreaker.execute(0, execution);

        verify(execution, times(1)).execute();
    }

    @Test
    void throwsMaxRetriesReachedWhenReachesLimitOfTimeouts() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        IExecute execution = mock(IExecute.class);

        when(execution.execute()).thenThrow(new SocketTimeoutException("foo"));

        CircuitBreaker circuitBreaker = new CircuitBreaker(0, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));
    }

    @Test
    void handlesDifferentMethodsOnDifferentResources() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        IExecute executionOne = mock(IExecute.class);
        IExecute executionTwo = mock(IExecute.class);

        when(executionOne.execute()).thenThrow(new SocketTimeoutException("Foo"));
        when(executionTwo.execute()).thenThrow(new SocketTimeoutException("Foo"));

        CircuitBreaker circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, executionOne));
        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(1, executionTwo));

        verify(executionOne, times(2)).execute();
        verify(executionTwo, times(2)).execute();
    }

    @Test
    void retriesIfItFails() throws IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        IExecute execution = mock(IExecute.class);

        when(execution.execute()).thenThrow(new SocketTimeoutException("foo"));

        CircuitBreaker circuitBreaker = new CircuitBreaker(1, 10);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, execution));

        verify(execution, times(2)).execute();
    }

    @Test
    void returnTheContentFromExecuteBack() throws MaxRetryReachedException, IOException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        final String returnContent = "Foo";
        IExecute execution = mock(IExecute.class);

        when(execution.execute()).thenReturn(returnContent);
        CircuitBreaker circuitBreaker = new CircuitBreaker();

        Object result = circuitBreaker.execute(0, execution);

        assertEquals(returnContent, result);
    }

    @Test
    void workingCorrectlyResetsCounter() throws IOException, MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, InterruptedException {
        IExecute execution = mock(IExecute.class);
        IExecute sameExecId = mock(IExecute.class);

        when(sameExecId.execute()).thenThrow(new SocketTimeoutException("Foo"));
        when(execution.execute()).thenAnswer(new Answer<String>() {
            private int nCall = 0;

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (nCall > 0) {
                    return "Foo";
                }
                nCall++;

                throw new SocketTimeoutException("test");
            }
        });

        CircuitBreaker circuitBreaker = new CircuitBreaker(1, 10);
        circuitBreaker.execute(0, execution);

        assertThrows(MaxRetryReachedException.class, () -> circuitBreaker.execute(0, sameExecId));

        verify(sameExecId, times(2)).execute();
    }
}
