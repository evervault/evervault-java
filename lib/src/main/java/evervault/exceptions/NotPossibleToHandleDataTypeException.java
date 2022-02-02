package evervault.exceptions;

public class NotPossibleToHandleDataTypeException extends Exception {
    private static final String ERROR_MESSAGE = "It is not possible to encrypt data type";

    public NotPossibleToHandleDataTypeException() {
        super(ERROR_MESSAGE);
    }
}
