package EverVault.Contracts;

public interface IDataHandler {
    boolean canEncrypt(Object data);
    String encrypt(Object data);
}
