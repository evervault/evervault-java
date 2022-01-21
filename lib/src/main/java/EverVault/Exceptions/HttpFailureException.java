package EverVault.Exceptions;

public class HttpFailureException extends Exception {
    static final String ERROR_MESSAGE = "Http status response: %d. Message: %s";

    public HttpFailureException(int httpErrorNumber, String message) {
        super(String.format(ERROR_MESSAGE, httpErrorNumber, message));
    }
}
