package EverVault;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Handler {

    private Base64.Decoder decoder;
    private Base64.Encoder encoder;

    public Base64Handler() {
        decoder = Base64.getDecoder();
        encoder = Base64.getEncoder();
    }

    protected byte[] decodeBase64String(String contentToDecode) {
        return decoder.decode(contentToDecode.getBytes(StandardCharsets.UTF_8));
    }

    protected String encodeBase64(byte[] byteArray) {
        return new String(encoder.encode(byteArray), StandardCharsets.UTF_8);
    }
}
