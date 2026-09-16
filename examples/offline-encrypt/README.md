# Offline encryption example

Encrypts a value with the Evervault Java SDK using a JWKS downloaded ahead of time.

```java
EvervaultKey key = EvervaultKey.fromJwks(jwksJson, kid);
Evervault evervault = Evervault.withKey(appId, key);
```

Passing a team UUID additionally sets up Outbound Relay credentials, which would otherwise
come from the Evervault API:

```java
Evervault evervault = Evervault.withKey(appId, apiKey, key, teamUuid);
```

`kid` is enforced when present - use `EvervaultKey.fromJwks(jwksJson)` if the JWK has no `kid`.

The example builds against the published SDK from Maven Central (see `build.gradle` for the
version) and uses the repository's Gradle wrapper rather than shipping its own.

For local SDK development, uncomment `includeBuild('../..')` in `settings.gradle` to
build against the working tree instead of the published artifact. Gradle substitutes
`com.evervault:lib` by group and module, so the version in `build.gradle` is ignored while
it is on.

## 1. Download your App's keys

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
| `EV_APP_ID` | `app_offline_example` | Evervault App whose credentials decrypt the ciphertext. Must be the App the JWKS came from |
| `EV_API_KEY` | — | When set, the example also decrypts via HTTP call |
