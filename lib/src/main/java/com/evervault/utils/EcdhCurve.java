package com.evervault.utils;

public enum EcdhCurve {
    SECP256K1("secp256k1"),
    SECP256R1("secp256r1");

    private final String curveId;

    EcdhCurve(String curveId) {
        this.curveId = curveId;
    }

    public boolean equalValue(String curveName) {
        return this.curveId.equals(curveName);
    }

}