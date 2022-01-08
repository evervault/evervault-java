package EverVault.Exceptions;

public class HttpFailureException extends Exception {
    static final String ERROR_MESSAGE = "Http status response: ";

    public HttpFailureException(int httpErrorNumber) {
        super(ERROR_MESSAGE + httpErrorNumber);
    }
}
