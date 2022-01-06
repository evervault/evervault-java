package EverVault.Exceptions;

public class UndefinedDataException extends Exception {
    static final String ERROR_MESSAGE = "Data can't be null";

    public UndefinedDataException() {
        super(ERROR_MESSAGE);
    }
}
