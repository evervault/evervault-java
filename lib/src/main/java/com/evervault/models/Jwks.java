package com.evervault.models;

import com.evervault.exceptions.EvervaultException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Jwks {
    private final Map<String, JsonObject> keysByKid;
    private final List<JsonObject> keys;

    private Jwks(Map<String, JsonObject> keysByKid, List<JsonObject> keys) {
        this.keysByKid = keysByKid;
        this.keys = keys;
    }

    /**
     * The kids in the set, not including JWKs with no kid
     */
    List<String> getKids() {
        return Collections.unmodifiableList(new ArrayList<>(keysByKid.keySet()));
    }

    /**
     * Reads the key with the given kid, if and only if a key exists with that kid
     */
    Jwk getKey(String kid) throws EvervaultException {
        JsonObject key = keysByKid.get(kid);

        if (key == null) {
            throw missingKid(kid);
        }

        return Jwk.fromJson(key);
    }

    /**
     * Returns the only key in the set, if and only if there is a singular key
     */
    Jwk getSoleKey() throws EvervaultException {
        if (keys.size() != 1) {
            throw invalid(String.format(
                    "JWKS holds %d keys; pass the kid of the one to use. Available kids: %s", keys.size(), getKids()));
        }

        return Jwk.fromJson(keys.get(0));
    }

    static Jwks fromJson(String jwksJson) throws EvervaultException {
        JsonElement parsed;
        try {
            parsed = JsonParser.parseString(jwksJson);
        } catch (JsonSyntaxException e) {
            throw invalid("JWKS is not valid JSON: " + e.getMessage());
        }

        if (parsed == null || !parsed.isJsonObject()) {
            throw invalid("JWKS must be a JSON object containing a 'keys' array");
        }

        JsonElement keysJson = parsed.getAsJsonObject().get("keys");
        if (keysJson == null || !keysJson.isJsonArray()) {
            throw invalid("JWKS must be a JSON object containing a 'keys' array");
        }

        if (keysJson.getAsJsonArray().size() == 0) {
            throw invalid("JWKS 'keys' array is empty");
        }

        Map<String, JsonObject> keysByKid = new LinkedHashMap<>();
        List<JsonObject> keys = new ArrayList<>();
        for (JsonElement element : keysJson.getAsJsonArray()) {
            if (!element.isJsonObject()) {
                throw invalid("JWKS 'keys' array contains an entry that is not a JSON object");
            }

            JsonObject keyJson = element.getAsJsonObject();
            keys.add(keyJson);

            String kid = readString(keyJson, "kid");
            if (kid == null) {
                continue;
            }

            JsonObject existing = keysByKid.get(kid);
            if (existing == null) {
                keysByKid.put(kid, keyJson);
            } else if (isEc(keyJson) && isEc(existing)) {
                throw invalid(String.format("JWKS contains more than one %s key with kid '%s'", Jwk.KEY_TYPE_EC, kid));
            } else if (isEc(keyJson)) {
                keysByKid.put(kid, keyJson);
            }
        }

        return new Jwks(keysByKid, keys);
    }

    private EvervaultException missingKid(String kid) {
        if (keys.size() == 1 && keysByKid.isEmpty()) {
            return invalid(String.format(
                    "JWKS does not contain a key with kid '%s'; the only key in the set has no kid. Use EvervaultKey.fromJwks(jwksJson) to accept it.", kid));
        }

        return invalid(String.format("JWKS does not contain a key with kid '%s'; available kids: %s", kid, getKids()));
    }

    private static boolean isEc(JsonObject key) {
        return Jwk.KEY_TYPE_EC.equals(readString(key, "kty"));
    }

    private static String readString(JsonObject key, String member) {
        JsonElement value = key.get(member);

        if (value == null || value.isJsonNull() || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isEmpty()) {
            return null;
        }

        return value.getAsString();
    }

    private static EvervaultException invalid(String message) {
        return new EvervaultException(new IllegalArgumentException(message));
    }
}
