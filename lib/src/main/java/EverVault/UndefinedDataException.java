package EverVault;

public class UndefinedDataException extends Exception {
    static final String ERROR_MESSAGE = "Data can not be null";

    public UndefinedDataException() {
        super(ERROR_MESSAGE);
    }
}
