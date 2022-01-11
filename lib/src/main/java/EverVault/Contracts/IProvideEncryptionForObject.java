package EverVault.Contracts;

public interface IProvideEncryptionForObject {
    String Encrypt(byte[] generatedEcdhKey, byte[] sharedKey, Object data);
}
