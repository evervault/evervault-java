package com.evervault.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public enum EcdhCurve {
    SECP256K1("secp256k1", "secp256k1", 32),
    SECP256R1("secp256r1", "P-256", 32);

    private static final List<String> JWK_CURVE_NAMES = buildJwkCurveNames();

    private final String curveId;
    private final String jwkCurveName;
    private final int coordinateLength;

    EcdhCurve(String curveId, String jwkCurveName, int coordinateLength) {
        this.curveId = curveId;
        this.jwkCurveName = jwkCurveName;
        this.coordinateLength = coordinateLength;
    }

    public boolean equalValue(String curveName) {
        return this.curveId.equals(curveName);
    }

    public String getJwkCurveName() {
        return jwkCurveName;
    }

    /**
     * The byte length of each affine coordinate of a point on this curve
     */
    public int getCoordinateLength() {
        return coordinateLength;
    }

    public static Optional<EcdhCurve> fromJwkCurveName(String crv) {
        for (EcdhCurve curve : values()) {
            if (curve.jwkCurveName.equals(crv)) {
                return Optional.of(curve);
            }
        }

        return Optional.empty();
    }

    public static List<String> jwkCurveNames() {
        return JWK_CURVE_NAMES;
    }

    private static List<String> buildJwkCurveNames() {
        List<String> names = new ArrayList<>();
        for (EcdhCurve curve : values()) {
            names.add(curve.jwkCurveName);
        }

        return Collections.unmodifiableList(names);
    }
}
