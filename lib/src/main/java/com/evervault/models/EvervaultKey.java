package com.evervault.models;

import com.evervault.exceptions.EvervaultException;
import com.evervault.exceptions.MandatoryParameterException;
import com.evervault.exceptions.NotImplementedException;
import com.evervault.services.EncryptionServiceFactory;
import com.evervault.utils.Base64Handler;
import com.evervault.utils.EcdhCurve;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

public class EvervaultKey {
    private static final byte UNCOMPRESSED_POINT_PREFIX = 0x04;

    private final EcdhCurve curve;
    private final String kid;
    private final PublicKey publicKey;

    private EvervaultKey(EcdhCurve curve, String kid, PublicKey publicKey) {
        this.curve = curve;
        this.kid = kid;
        this.publicKey = publicKey;
    }

    public EcdhCurve getCurve() {
        return curve;
    }

    /**
     * The key id, or null when the JWK did not have one
     */
    public String getKid() {
        return kid;
    }

    /**
     * The public key derived from this JWK
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Reads the key with the given kid, if and only if a key exists with that kid
     */
    public static EvervaultKey fromJwks(String jwksJson, String kid) throws EvervaultException {
        if (kid == null || kid.trim().isEmpty()) {
            throw new EvervaultException(new MandatoryParameterException("kid"));
        }

        return fromJwk(parse(jwksJson).getKey(kid));
    }

    /**
     * Reads the only key in the set, if and only if there is a singular key
     */
    public static EvervaultKey fromJwks(String jwksJson) throws EvervaultException {
        return fromJwk(parse(jwksJson).getSoleKey());
    }

    private static Jwks parse(String jwksJson) throws EvervaultException {
        if (jwksJson == null || jwksJson.trim().isEmpty()) {
            throw new EvervaultException(new MandatoryParameterException("jwksJson"));
        }

        return Jwks.fromJson(jwksJson);
    }

    private static EvervaultKey fromJwk(Jwk jwk) throws EvervaultException {
        if (jwk == null) {
            throw new EvervaultException(new MandatoryParameterException("jwk"));
        }

        byte[] x = jwk.getX();
        byte[] y = jwk.getY();

        byte[] point = new byte[1 + x.length + y.length];
        point[0] = UNCOMPRESSED_POINT_PREFIX;
        System.arraycopy(x, 0, point, 1, x.length);
        System.arraycopy(y, 0, point, 1 + x.length, y.length);

        PublicKey publicKey;
        try {
            publicKey = EncryptionServiceFactory.build(jwk.getCurve())
                    .getEllipticCurvePublicKeyFrom(Base64Handler.encodeBase64(point));
        } catch (IllegalArgumentException e) {
            throw new EvervaultException(new IllegalArgumentException(String.format(
                    "JWK %s does not describe a valid point on curve '%s'", jwk.describe(), jwk.getCurve().getJwkCurveName()), e));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NotImplementedException e) {
            throw new EvervaultException(e);
        }

        return new EvervaultKey(jwk.getCurve(), jwk.getKid(), publicKey);
    }
}
