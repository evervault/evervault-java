package evervault.contracts;

public interface IProvideEncryptedFormat {
    String format(DataHeader header, String iv, String publicKey, String encryptedPayload);
}
