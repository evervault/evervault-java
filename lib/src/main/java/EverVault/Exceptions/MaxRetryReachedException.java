package EverVault.Exceptions;

public class MaxRetryReachedException extends Exception {
    public static final String ERROR_MESSAGE = "Max number of retries have been reached, waiting for a moment";

    public MaxRetryReachedException() {
        super(ERROR_MESSAGE);
    }
}
