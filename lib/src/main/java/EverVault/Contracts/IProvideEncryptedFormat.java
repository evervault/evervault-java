package EverVault.Contracts;

public interface IProvideEncryptedFormat {
    String format(String everVaultVersion, DataHeader header, String iv, String publicKey, String encryptedPayload);
}
