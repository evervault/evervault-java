package EverVault;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class Base64Handler {
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;

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

    /// Should this be here? Python's SDK has it names as __base_64_remove_padding
    protected String removePadding(String content) {
        int i = content.length() - 1;

        for (; i > 0; i--) {
            if (content.charAt(i) != '=') {
                break;
            }
        }

        return content.substring(0, ++i);
    }
}
