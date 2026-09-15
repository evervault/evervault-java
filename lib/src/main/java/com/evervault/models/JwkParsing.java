package com.evervault.models;

import com.evervault.exceptions.EvervaultException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

final class JwkParsing {

    private JwkParsing() {
    }

    /**
     * Reads a member as a string, empty when it is missing, null, not a string, or an empty string
     */
    static Optional<String> readString(JsonObject json, String member) {
        JsonElement value = json.get(member);

        if (value == null || value.isJsonNull() || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString() || value.getAsString().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(value.getAsString());
    }

    static EvervaultException invalid(String message) {
        return new EvervaultException(new IllegalArgumentException(message));
    }
}
