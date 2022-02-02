package evervault.services;

import evervault.contracts.IProvideEncryptedFormat;

public class EncryptionServiceBasedOnCurve256R1 extends EncryptionService {
    public EncryptionServiceBasedOnCurve256R1(IProvideEncryptedFormat encryptFormatProvider) {
        super(encryptFormatProvider);
    }

    protected static final String CURVE_NAME_256R1 = "secp256r1";

    @Override
    protected String getCurveName() {
        return CURVE_NAME_256R1;
    }
}
