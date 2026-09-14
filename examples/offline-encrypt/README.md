# Offline encryption example

Encrypts a value with the Evervault Java SDK using a JWKS downloaded ahead of time.

```java
EvervaultKey key = EvervaultKey.fromJwks(jwksJson, kid);
Evervault evervault = Evervault.withKey(appId, key);
```

`kid` is enforced when present - use `EvervaultKey.fromJwks(jwksJson)` if the JWK has no `kid`.

Built against the SDK in this repository via `includeBuild('../..')`, using the
repository's Gradle wrapper rather than carrying its own.

## 1. Download your app's keys

```sh
mkdir -p keys
curl "https://keys.evervault.com/<TEAM_UUID>/apps/<APP_ID>?format=jwks" > keys/jwks.json
```

`keys/` is gitignored.

## 2. Run it

```sh
../../gradlew run
```

If the JWKS has multiple keys a `kid` must be provided.

```
JWKS holds 2 keys; pass the kid of the one to use. Available kids: [<kid1>, <kid2>]
```

```sh
../../gradlew run --args="<kid>"
```

## 3. Optional: check the round trip

Decrypting requires network and an API key.

```sh
EV_APP_ID=app_xxx EV_API_KEY=ev:key:... ../../gradlew run --args="<kid>"
```

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `EV_KEY_ID` | — | `kid` to use, unless the JWKS holds a single key |
| `EV_JWKS_PATH` | `keys/jwks.json` | JWKS to read |
| `EV_APP_ID` | `app_offline_example` | App the ciphertext is bound to, required for decryption |
| `EV_API_KEY` | — | When set, the example also decrypts via HTTP call |
