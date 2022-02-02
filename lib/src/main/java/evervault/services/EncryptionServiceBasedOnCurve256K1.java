package evervault.services;

import evervault.contracts.IProvideEncryptedFormat;

public class EncryptionServiceBasedOnCurve256K1 extends EncryptionService {
    public EncryptionServiceBasedOnCurve256K1(IProvideEncryptedFormat encryptFormatProvider) {
        super(encryptFormatProvider);
    }

    protected static final String CURVE_NAME_256K1 = "secp256k1";

    @Override
    protected String getCurveName() {
        return CURVE_NAME_256K1;
    }
}
