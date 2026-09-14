package com.evervault;

import com.evervault.exceptions.EvervaultException;
import com.evervault.models.EvervaultKey;
import com.evervault.services.EncryptionServiceFactory;
import com.evervault.utils.EcdhCurve;

import org.junit.jupiter.api.Test;

import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhenUsingASuppliedKeyTests {
    private static final String K1_COMPRESSED_KEY = "AwGsgh0ez4PgRYp9byghd6aJTqO+cWitrGUMAbnndywW";
    private static final String K1_KID = "Qt4WT5TUY2K3c1hlcixVzSlYeMsWcWGW_CsSVziIlCc";
    private static final String K1_X = "AayCHR7Pg-BFin1vKCF3polOo75xaK2sZQwBued3LBY";
    private static final String K1_Y = "IJespnnehKnHF-SHQm3L3SW-bFou_qqKs0oqqYkd8Tc";

    private static final String R1_COMPRESSED_KEY = "A0KYZDAy/qUm4IiFZOOLBgyAyUMufcc/EN8VRhaRmioc";
    private static final String R1_KID = "ISO0IyC6lK4pmKK2Gx4wTTshSbAnjG4qhr7y2OfGoHQ";

    private static final String R1_JWK = "{\"kty\":\"EC\",\"crv\":\"P-256\"," +
            "\"x\":\"QphkMDL-pSbgiIVk44sGDIDJQy59xz8Q3xVGFpGaKhw\"," +
            "\"y\":\"ziMMugyC-5mz66bxB7Nrf2xo0Zkf5kehejQk_QcoUWM\"," +
            "\"use\":\"enc\",\"kid\":\"" + R1_KID + "\"}";

    private static final String K1_JWK = "{\"kty\":\"EC\",\"crv\":\"secp256k1\"," +
            "\"x\":\"" + K1_X + "\"," +
            "\"y\":\"" + K1_Y + "\"," +
            "\"use\":\"enc\",\"kid\":\"" + K1_KID + "\"}";

    private static final String JWKS = "{\"keys\":[" + R1_JWK + "," + K1_JWK + "]}";

    private static String message(EvervaultException e) {
        return e.getMessage() == null ? "" : e.getMessage();
    }

    private static PublicKey legacyKeyFrom(EcdhCurve curve, String base64Key) throws Exception {
        return EncryptionServiceFactory.build(curve).getEllipticCurvePublicKeyFrom(base64Key);
    }

    private static String failingToParse(String jwks, String kid) {
        return message(assertThrows(EvervaultException.class, () -> EvervaultKey.fromJwks(jwks, kid)));
    }

    private static String failingToParse(String jwks) {
        return message(assertThrows(EvervaultException.class, () -> EvervaultKey.fromJwks(jwks)));
    }

    @Test
    void readsTheSecp256k1KeyAsTheLegacyCompressedKeyDoes() throws Exception {
        EvervaultKey key = EvervaultKey.fromJwks(JWKS, K1_KID);

        assertEquals(EcdhCurve.SECP256K1, key.getCurve());
        assertEquals(K1_KID, key.getKid());
        assertEquals(legacyKeyFrom(EcdhCurve.SECP256K1, K1_COMPRESSED_KEY), key.getPublicKey());
    }

    @Test
    void readsTheP256KeyAsTheLegacyCompressedKeyDoes() throws Exception {
        EvervaultKey key = EvervaultKey.fromJwks(JWKS, R1_KID);

        assertEquals(EcdhCurve.SECP256R1, key.getCurve());
        assertEquals(R1_KID, key.getKid());
        assertEquals(legacyKeyFrom(EcdhCurve.SECP256R1, R1_COMPRESSED_KEY), key.getPublicKey());
    }

    @Test
    void rejectsAMissingJwks() {
        assertTrue(failingToParse(null, K1_KID).contains("jwksJson"));
        assertTrue(failingToParse("  ", K1_KID).contains("jwksJson"));
        assertTrue(failingToParse(null).contains("jwksJson"));
        assertTrue(failingToParse("  ").contains("jwksJson"));
    }

    @Test
    void rejectsAMissingKid() {
        assertTrue(failingToParse(JWKS, null).contains("kid"));
    }

    @Test
    void rejectsJsonThatIsNotAJwks() {
        assertTrue(failingToParse("{", K1_KID).contains("not valid JSON"));
        assertTrue(failingToParse("[]", K1_KID).contains("'keys' array"));
        assertTrue(failingToParse("{\"keys\":{}}", K1_KID).contains("'keys' array"));
        assertTrue(failingToParse("{\"keys\":[]}", K1_KID).contains("empty"));
        assertTrue(failingToParse("{\"keys\":[1]}", K1_KID).contains("not a JSON object"));
        assertTrue(failingToParse("{\"keys\":[{\"kty\":\"EC\"},{\"kty\":\"EC\"}]}", K1_KID).contains("does not contain a key with kid"));
    }

    @Test
    void rejectsAJwksWithDuplicateEcKids() {
        String duplicated = "{\"keys\":[" + K1_JWK + "," + K1_JWK + "]}";

        String error = failingToParse(duplicated, K1_KID);

        assertTrue(error.contains("more than one EC key"), error);
        assertTrue(error.contains(K1_KID), error);
    }

    @Test
    void prefersTheEcKeyWhenAnotherKeyTypeSharesItsKid() throws Exception {
        String rsa = "{\"kty\":\"RSA\",\"use\":\"sig\",\"kid\":\"" + K1_KID + "\",\"n\":\"a\",\"e\":\"AQAB\"}";

        EvervaultKey rsaFirst = EvervaultKey.fromJwks("{\"keys\":[" + rsa + "," + K1_JWK + "]}", K1_KID);
        EvervaultKey rsaLast = EvervaultKey.fromJwks("{\"keys\":[" + K1_JWK + "," + rsa + "]}", K1_KID);

        assertEquals(EcdhCurve.SECP256K1, rsaFirst.getCurve());
        assertEquals(EcdhCurve.SECP256K1, rsaLast.getCurve());
    }

    @Test
    void takesTheSoleKeyWhenNoKidIsAskedFor() throws Exception {
        String withoutKid = "{\"keys\":[" + K1_JWK.replace(",\"kid\":\"" + K1_KID + "\"", "") + "]}";

        EvervaultKey key = EvervaultKey.fromJwks(withoutKid);

        assertEquals(EcdhCurve.SECP256K1, key.getCurve());
        assertNull(key.getKid());
    }

    @Test
    void takesTheSoleKeyEvenWhenItCarriesAKid() throws Exception {
        EvervaultKey key = EvervaultKey.fromJwks("{\"keys\":[" + K1_JWK + "]}");

        assertEquals(K1_KID, key.getKid());
    }

    @Test
    void refusesTheSoleKeyWhenItDoesNotCarryTheRequestedKid() {
        String withoutKid = "{\"keys\":[" + K1_JWK.replace(",\"kid\":\"" + K1_KID + "\"", "") + "]}";

        String error = failingToParse(withoutKid, K1_KID);

        assertTrue(error.contains("the only key in the set has no kid"), error);
        assertTrue(error.contains(K1_KID), error);
    }

    @Test
    void refusesTheSoleKeyWhenItsKidIsNotAString() {
        String numericKid = "{\"keys\":[" + K1_JWK.replace("\"kid\":\"" + K1_KID + "\"", "\"kid\":123") + "]}";

        assertTrue(failingToParse(numericKid, "123").contains("the only key in the set has no kid"));
    }

    @Test
    void refusesToChooseBetweenSeveralKeysWithoutAKid() {
        String error = failingToParse(JWKS);

        assertTrue(error.contains("holds 2 keys"), error);
        assertTrue(error.contains(K1_KID), error);
        assertTrue(error.contains(R1_KID), error);
    }

    @Test
    void describesAKidlessKeyByItsAbsentKidWhenItIsInvalid() {
        String withoutKid = "{\"keys\":[" + K1_JWK.replace(",\"kid\":\"" + K1_KID + "\"", "").replace("\"crv\":\"secp256k1\",", "") + "]}";

        String error = failingToParse(withoutKid);

        assertTrue(error.contains("with no kid"), error);
        assertTrue(error.contains("'crv'"), error);
    }

    @Test
    void doesNotFallBackToAKidlessKeyWhenTheSetHasSeveralKeys() {
        String withoutKid = "{\"keys\":[" + R1_JWK + "," + K1_JWK.replace(",\"kid\":\"" + K1_KID + "\"", "") + "]}";

        String error = failingToParse(withoutKid, K1_KID);

        assertTrue(error.contains("does not contain a key with kid"), error);
    }

    @Test
    void rejectsAPrivateKey() {
        String privateKey = JWKS.replace("\"use\":\"enc\",\"kid\":\"" + K1_KID + "\"",
                "\"d\":\"" + K1_X + "\",\"use\":\"enc\",\"kid\":\"" + K1_KID + "\"");

        String error = failingToParse(privateKey, K1_KID);

        assertTrue(error.contains("private key"), error);
        assertTrue(error.contains(K1_KID), error);
    }

    @Test
    void doesNotMatchANumericKid() {
        String numericKid = "{\"keys\":[" + K1_JWK.replace("\"kid\":\"" + K1_KID + "\"", "\"kid\":123") + "," + R1_JWK + "]}";

        String error = failingToParse(numericKid, "123");

        assertTrue(error.contains("does not contain a key with kid '123'"), error);
    }

    @Test
    void reportsTheAvailableKidsWhenTheRequestedOneIsAbsent() {
        String error = failingToParse(JWKS, "nope");

        assertTrue(error.contains("nope"), error);
        assertTrue(error.indexOf(R1_KID) < error.indexOf(K1_KID), error);
    }

    @Test
    void rejectsAKeyThatIsNotAnEncryptionKey() {
        String rsa = "{\"keys\":[{\"kty\":\"RSA\",\"kid\":\"" + K1_KID + "\",\"n\":\"a\",\"e\":\"AQAB\"}]}";
        assertTrue(failingToParse(rsa, K1_KID).contains("kty"));

        String signing = JWKS.replace("\"use\":\"enc\",\"kid\":\"" + K1_KID + "\"", "\"use\":\"sig\",\"kid\":\"" + K1_KID + "\"");
        assertTrue(failingToParse(signing, K1_KID).contains("use"));
    }

    @Test
    void acceptsAKeyWithoutAUseMember() throws Exception {
        String withoutUse = JWKS.replace("\"use\":\"enc\",\"kid\":\"" + K1_KID + "\"", "\"kid\":\"" + K1_KID + "\"");

        EvervaultKey key = EvervaultKey.fromJwks(withoutUse, K1_KID);

        assertEquals(K1_KID, key.getKid());
    }

    @Test
    void ignoresKeysItCannotUseWhenAnotherKeyMatches() throws Exception {
        String withSigningKey = JWKS.replace("{\"keys\":[",
                "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"kid\":\"a-signing-key\",\"n\":\"a\",\"e\":\"AQAB\"},");

        EvervaultKey key = EvervaultKey.fromJwks(withSigningKey, K1_KID);

        assertEquals(EcdhCurve.SECP256K1, key.getCurve());
        assertEquals(K1_KID, key.getKid());
    }

    @Test
    void rejectsAnUnsupportedCurve() {
        String p384 = JWKS.replace("\"crv\":\"secp256k1\"", "\"crv\":\"P-384\"");

        String error = failingToParse(p384, K1_KID);

        assertTrue(error.contains("P-384"), error);
        assertTrue(error.contains("P-256"), error);
        assertTrue(error.contains("secp256k1"), error);
    }

    @Test
    void rejectsCoordinatesThatAreMissingOrTheWrongLength() {
        String missing = JWKS.replace("\"x\":\"" + K1_X + "\",", "");
        assertTrue(failingToParse(missing, K1_KID).contains("'x'"));

        String truncated = JWKS.replace("\"y\":\"" + K1_Y + "\"", "\"y\":\"IJespnnehKnHF-SHQm3L3S\"");
        assertTrue(failingToParse(truncated, K1_KID).contains("expected 32"));
    }

    @Test
    void rejectsCoordinatesThatAreNotBase64url() {
        String standardBase64 = JWKS.replace("\"x\":\"" + K1_X + "\"", "\"x\":\"" + K1_X.replace('-', '+') + "\"");

        assertTrue(failingToParse(standardBase64, K1_KID).contains("base64url"));
    }

    @Test
    void rejectsAPointThatIsNotOnTheCurveWhenParsing() {
        String offCurve = JWKS.replace(K1_X, "AayCHR7Pg-BFin1vKCF3polOo75xaK2sZQwBued3LBc");

        String error = failingToParse(offCurve, K1_KID);

        assertTrue(error.contains("valid point"), error);
        assertTrue(error.contains(K1_KID), error);
    }

    @Test
    void constructsAndEncryptsWithoutReachingTheApi() throws Exception {
        Evervault evervault = Evervault.withKey("app_not_a_real_app", EvervaultKey.fromJwks(JWKS, K1_KID));

        String encrypted = (String) evervault.encrypt("Hello World!");

        assertTrue(encrypted.startsWith("ev:RFVC:"), encrypted);
    }

    @Test
    void acceptsAnApiKeyAlongsideTheSuppliedKey() throws Exception {
        Evervault evervault = Evervault.withKey("app_not_a_real_app", "not-a-real-api-key", EvervaultKey.fromJwks(JWKS, K1_KID));

        String encrypted = (String) evervault.encrypt("Hello World!");

        assertTrue(encrypted.startsWith("ev:RFVC:"), encrypted);
    }

    @Test
    void usesTheCurveNamedByTheSuppliedKey() throws Exception {
        Evervault evervault = Evervault.withKey("app_not_a_real_app", EvervaultKey.fromJwks(JWKS, R1_KID));

        String encrypted = (String) evervault.encrypt("Hello World!");

        assertTrue(encrypted.startsWith("ev:Tk9D:"), encrypted);
    }

    @Test
    void requiresTheSuppliedKeyToBePresent() {
        assertTrue(message(assertThrows(EvervaultException.class, () -> Evervault.withKey("app_not_a_real_app", null)))
                .contains("key"));
        assertTrue(message(assertThrows(EvervaultException.class, () -> Evervault.withKey("app_not_a_real_app", "not-a-real-api-key", null)))
                .contains("key"));
    }

    @Test
    void refusesToHandOutOutboundRelayConfiguration() throws Exception {
        Evervault evervault = Evervault.withKey("app_not_a_real_app", EvervaultKey.fromJwks(JWKS, K1_KID));

        assertTrue(assertThrows(IllegalStateException.class, evervault::getEvervaultProxyCredentials)
                .getMessage().contains("Outbound Relay"));
        assertTrue(assertThrows(IllegalStateException.class, evervault::getEvervaultHttpRoutePlanner)
                .getMessage().contains("Outbound Relay"));
    }
}
