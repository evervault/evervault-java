package evervault.exceptions;

public class NotImplementedException extends Exception {
    public static final String ERROR_MESSAGE = "%s is not implemented.";

    public NotImplementedException(String methodName) {
        super(String.format(ERROR_MESSAGE, methodName));
    }
}
