package EverVault.Services;

import EverVault.Contracts.IExecuteWithPossibleHttpTimeout;
import EverVault.Exceptions.MaxRetryReachedException;

import java.net.http.HttpTimeoutException;
import java.util.HashMap;

public class CircuitBreaker {
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

    public <TReturn> TReturn execute(int methodIdentifier, IExecuteWithPossibleHttpTimeout executable) throws MaxRetryReachedException {
        try {
            return executable.execute();
        } catch (HttpTimeoutException httpTimeoutException) {
            if (!control.containsKey(methodIdentifier)) {
                control.put(methodIdentifier, GetNewResourceControl());
            }

            var resourceControl = control.get(methodIdentifier);
            resourceControl.timeOutOccurred();

            if (resourceControl.getBlocked()) {
                throw new MaxRetryReachedException();
            }

            return execute(methodIdentifier, executable);
        }
    }
}
