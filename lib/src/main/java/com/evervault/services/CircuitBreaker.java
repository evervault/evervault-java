package com.evervault.services;

import com.evervault.contracts.IProvideCircuitBreaker;
import com.evervault.exceptions.HttpFailureException;
import com.evervault.contracts.IExecute;
import com.evervault.exceptions.MaxRetryReachedException;
import com.evervault.exceptions.NotPossibleToHandleDataTypeException;
import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.HashMap;

public class CircuitBreaker implements IProvideCircuitBreaker {
    private HashMap<Integer, ResourceControl> control;
    private long timeOutMilliseconds;
    private boolean useCustomTimes = false;
    private int countLimit;

    public CircuitBreaker() {
        control = new HashMap<>();
    }

    public CircuitBreaker(int countLimit, long timeOutMilliseconds) {
        this();
        this.countLimit = countLimit;
        this.useCustomTimes = true;
        this.timeOutMilliseconds = timeOutMilliseconds;
    }

    private ResourceControl GetNewResourceControl() {
        if (useCustomTimes) {
            return new ResourceControl(countLimit, timeOutMilliseconds);
        }
        return new ResourceControl();
    }

    public <TReturn> TReturn execute(int methodIdentifier, IExecute<TReturn> executable) throws MaxRetryReachedException, NotPossibleToHandleDataTypeException, HttpFailureException, IOException, InterruptedException {
        if (!control.containsKey(methodIdentifier)) {
            control.put(methodIdentifier, GetNewResourceControl());
        }

        var resourceControl = control.get(methodIdentifier);

        try {
            var result = (TReturn)executable.execute();
            resourceControl.clear();
            return result;
        } catch (HttpTimeoutException httpTimeoutException) {
            resourceControl.timeOutOccurred();

            if (resourceControl.getBlocked()) {
                throw new MaxRetryReachedException();
            }

            return execute(methodIdentifier, executable);
        }
    }
}
