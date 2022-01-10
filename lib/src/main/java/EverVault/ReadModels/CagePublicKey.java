package EverVault.ReadModels;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CagePublicKey implements java.io.Serializable {
    public String teamUuid;
    public String key;
    public String ecdhKey;

    public byte[] GetDecodedTeamCageKey() {
        var decoder = Base64.getDecoder();
        return decoder.decode(ecdhKey.getBytes(StandardCharsets.UTF_8));
    }
}
