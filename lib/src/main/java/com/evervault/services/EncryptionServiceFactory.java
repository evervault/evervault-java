package com.evervault.services;

import com.evervault.utils.EcdhCurve;

public abstract class EncryptionServiceFactory {
    public static EncryptionService build(EcdhCurve ecdhCurve) {
        if (ecdhCurve == null) {
            throw new IllegalArgumentException("ecdhCurve");
        }

        switch (ecdhCurve) {
            case SECP256R1:
                return new EncryptionServiceBasedOnCurve256R1(new R1StdEncryptionOutputFormat());
            case SECP256K1:
                return new EncryptionServiceBasedOnCurve256K1(new K1StdEncryptionOutputFormat());
            default:
                throw new IllegalArgumentException("No encryption service for curve " + ecdhCurve);
        }
    }
}
