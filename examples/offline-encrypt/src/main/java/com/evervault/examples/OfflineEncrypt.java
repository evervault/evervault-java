package com.evervault.examples;

import com.evervault.Evervault;
import com.evervault.exceptions.EvervaultException;
import com.evervault.models.EvervaultKey;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OfflineEncrypt {
    private static final String DEFAULT_JWKS_PATH = "keys/jwks.json";
    private static final String DEFAULT_PLAINTEXT = "4242424242424242";

    public static void main(String[] args) throws Exception {
        String kid = argOrEnv(args, 0, "EV_KEY_ID");
        Path jwksPath = Paths.get(argOrEnv(args, 1, "EV_JWKS_PATH", DEFAULT_JWKS_PATH));
        String appId = env("EV_APP_ID", "app_offline_example");
        String apiKey = env("EV_API_KEY", "");

        if (!Files.exists(jwksPath)) {
            System.err.println("No JWK Set at " + jwksPath.toAbsolutePath());
            System.err.println("See README.md for how to download one into " + DEFAULT_JWKS_PATH);
            System.exit(1);
        }

        String jwks = new String(Files.readAllBytes(jwksPath), StandardCharsets.UTF_8);

        EvervaultKey key = null;
        try {
            key = kid == null ? EvervaultKey.fromJwks(jwks) : EvervaultKey.fromJwks(jwks, kid);
        } catch (EvervaultException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Evervault evervault = Evervault.withKey(appId, key);

        System.out.println("Loaded " + jwksPath + " (kid " + (key.getKid() == null ? "none" : key.getKid()) + ", curve " + key.getCurve() + ")");
        System.out.println("No network calls were made to construct the client.");

        String plaintext = DEFAULT_PLAINTEXT;
        String encrypted = (String) evervault.encrypt(plaintext);

        System.out.println();
        System.out.println("plaintext:  " + plaintext);
        System.out.println("ciphertext: " + encrypted);

        if (apiKey.isEmpty()) {
            System.out.println();
            System.out.println("Set EV_APP_ID and EV_API_KEY to also decrypt the ciphertext back, which does use the network.");
            return;
        }

        String decrypted = new Evervault(appId, apiKey).decrypt(encrypted, String.class);

        System.out.println("decrypted:  " + decrypted);
        System.out.println(plaintext.equals(decrypted) ? "Round trip succeeded." : "Round trip FAILED.");
    }

    private static String argOrEnv(String[] args, int index, String variable) {
        return argOrEnv(args, index, variable, null);
    }

    private static String argOrEnv(String[] args, int index, String variable, String fallback) {
        if (args.length > index && !args[index].trim().isEmpty()) {
            return args[index];
        }

        return env(variable, fallback);
    }

    private static String env(String variable, String fallback) {
        String value = System.getenv(variable);

        return value == null || value.trim().isEmpty() ? fallback : value;
    }
}
