package evervault.services;

import evervault.utils.EcdhCurve;

public abstract class EncryptionServiceFactory {
    public static EncryptionService build(EcdhCurve ecdhCurve) {
        if(EcdhCurve.SECP256R1.equals(ecdhCurve)) {
            return new EncryptionServiceBasedOnCurve256R1(new R1StdEncryptionOutputFormat());
        }
        return new EncryptionServiceBasedOnCurve256K1(new K1StdEncryptionOutputFormat());
    }
}
