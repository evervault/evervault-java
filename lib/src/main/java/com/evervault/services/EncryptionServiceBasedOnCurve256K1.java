package com.evervault.services;

import com.evervault.contracts.IProvideEncryptedFormat;
import com.evervault.exceptions.NotImplementedException;

public class EncryptionServiceBasedOnCurve256K1 extends EncryptionService {
    public EncryptionServiceBasedOnCurve256K1(IProvideEncryptedFormat encryptFormatProvider) {
        super(encryptFormatProvider);
    }

    protected static final String CURVE_NAME_256K1 = "secp256k1";

    @Override
    protected String getCurveName() {
        return CURVE_NAME_256K1;
    }

    @Override
    protected String getKeyAgreementAlgorithm() throws NotImplementedException {
        return KEY_AGREEMENT_ALGORITHM;
    }

    @Override
    protected String getKeyGeneratorAlgorithm() throws NotImplementedException {
        return ELLIPTIC_CURVE_ALGORITHM;
    }
}
