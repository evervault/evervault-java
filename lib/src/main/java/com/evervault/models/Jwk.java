package com.evervault.models;

import com.evervault.exceptions.EvervaultException;
import com.evervault.utils.Base64Handler;
import com.evervault.utils.EcdhCurve;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class Jwk {

    private static final String KEY_TYPE_EC = "EC";
    private static final String KEY_USE_ENCRYPTION = "enc";
    private static final String PRIVATE_KEY_MEMBER = "d";
    private static final String NO_KID = "with no kid";
    private static final int COORDINATE_LENGTH = 32;

    private final String kid;
    private final EcdhCurve curve;
    private final byte[] x;
    private final byte[] y;

    private Jwk(String kid, EcdhCurve curve, byte[] x, byte[] y) {
        this.kid = kid;
        this.curve = curve;
        this.x = x;
        this.y = y;
    }

    String getKid() {
        return kid;
    }

    String describe() {
        return describe(kid);
    }

    EcdhCurve getCurve() {
        return curve;
    }

    byte[] getX() {
        return x.clone();
    }

    byte[] getY() {
        return y.clone();
    }

    static Jwk fromJson(JsonObject jwk) throws EvervaultException {
        String kid = readOptionalString(jwk, "kid");

        String kty = readString(jwk, "kty", kid);
        if (!KEY_TYPE_EC.equals(kty)) {
            throw invalid(String.format("JWK %s has kty '%s'; only '%s' keys are supported", describe(kid), kty, KEY_TYPE_EC));
        }

        if (jwk.has(PRIVATE_KEY_MEMBER)) {
            throw invalid(String.format("JWK %s contains the private key member '%s'; only public keys are accepted", describe(kid), PRIVATE_KEY_MEMBER));
        }

        String use = readOptionalString(jwk, "use");
        if (use != null && !KEY_USE_ENCRYPTION.equals(use)) {
            throw invalid(String.format("JWK %s has use '%s'; only '%s' keys are supported", describe(kid), use, KEY_USE_ENCRYPTION));
        }

        EcdhCurve curve = readCurve(jwk, kid);
        byte[] x = readCoordinate(jwk, "x", kid);
        byte[] y = readCoordinate(jwk, "y", kid);

        return new Jwk(kid, curve, x, y);
    }

    private static EcdhCurve readCurve(JsonObject jwk, String kid) throws EvervaultException {
        String crv = readString(jwk, "crv", kid);

        return EcdhCurve.fromJwkCurveName(crv).orElseThrow(() -> invalid(
                String.format("JWK %s has unsupported crv '%s'; supported curves are %s", describe(kid), crv, EcdhCurve.jwkCurveNames())));
    }

    private static byte[] readCoordinate(JsonObject jwk, String member, String kid) throws EvervaultException {
        byte[] decoded;
        try {
            decoded = Base64Handler.decodeBase64UrlString(readString(jwk, member, kid));
        } catch (IllegalArgumentException e) {
            throw invalid(String.format("JWK %s member '%s' is not valid base64url", describe(kid), member));
        }

        if (decoded.length != COORDINATE_LENGTH) {
            throw invalid(String.format("JWK %s member '%s' decodes to %d bytes; expected %d", describe(kid), member, decoded.length, COORDINATE_LENGTH));
        }

        return decoded;
    }

    private static String readString(JsonObject jwk, String member, String kid) throws EvervaultException {
        String value = readOptionalString(jwk, member);

        if (value == null) {
            throw invalid(String.format("JWK %s is missing required member '%s'", describe(kid), member));
        }

        return value;
    }

    private static String readOptionalString(JsonObject jwk, String member) {
        JsonElement value = jwk.get(member);

        if (value == null || value.isJsonNull() || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isEmpty()) {
            return null;
        }

        return value.getAsString();
    }

    private static String describe(String kid) {
        return kid == null ? NO_KID : "'" + kid + "'";
    }

    private static EvervaultException invalid(String message) {
        return new EvervaultException(new IllegalArgumentException(message));
    }
}
