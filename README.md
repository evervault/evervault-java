[![Evervault](https://evervault.com/evervault.svg)](https://evervault.com/)

# Evervault Java SDK

The [Evervault](https://evervault.com) Java SDK can be used to:

1. Encrypt data at your server
2. Run your Cages
3. Encrypt/decrypt data with Relay

You can use our Java SDK to encrypt data — rather than with **[Relay](/concepts/relay/overview)** — and still send it to a third-party via Relay. Encrypting with our backend SDKs is best for developers who want to avoid the network latency of Relay and/or want to avoid sending plaintext data to Relay to be encrypted.

Encrypting data with our backend SDKs instead of Relay may expose you to greater compliance burden because plaintext data touches your server before it is encrypted.

You don’t need to change your database configuration. You can store Evervault-encrypted data in your database as you would the plaintext version.

## Getting Started

Before starting with the Evervault Java SDK, you will need to [create an account](https://app.evervault.com/register) and a team.

For full installation support, [book time here](https://calendly.com/evervault/cages-onboarding).

## Documentation

See the Evervault [Java SDK documentation](https://docs.evervault.com/reference/java-sdk).

## Installation

Our Java SDK is distributed via [maven](https://search.maven.org/artifact/com.evervault/lib), and can be installed using your preferred build tool.

### Gradle
```sh
implementation 'com.evervault:lib:2.0.4'
```

### Maven
```xml
<dependency>
  <groupId>com.evervault</groupId>
  <artifactId>lib</artifactId>
  <version>2.0.4</version>
</dependency>
```

## Usage

The Evervault Java SDK exposes a constructor and two functions:

* `evervault.encrypt()`
* `evervault.run()`

### Relay Interception

The Evervault Java SDK can automatically route all outbound HTTPS requests through Relay for decryption. This can be done by setting up a proxy to Evervault on your HTTP client.

To disable this behaviour, set `intercept` to `false` in the initialization options. For the most common Java HTTP Clients, here is how intercept can be set up:

### Evervault CA
To allow outbound interception with Relay the Evervault Root Ca certificate must be added to the JVM keystore.
```
curl https://ca.evervault.com --output evervault-ca.cert
```
```
sudo keytool -import -alias evervault-ca -file evervault-ca.cert -keystore <path/to/jdk/cacerts>
```

#### Apache HTTP Client

Apache HTTP Client uses two extra JVM settings to authenticate with the proxy. When the Evervault Java SDK is initialised with intercept enabled, it sets these settings.

```java
// Note This is done automatically when you setup the Evervault SDK

System.setProperty("https.proxyUser", user);
System.setProperty("https.proxyPassword", password);
System.setProperty("http.proxyUser", user);
System.setProperty("http.proxyPassword", password);
```

#### Java 11 Client

When using the new Java 11 client, **you will have to update the client to use the default authenticator and proxy like so**:

```java
// When the Evervault Java SDK is initialised with intercept enabled, it's set's the default authenticator and proxy.
// You just need to inject the authenticator into your builder.

HttpClient httpClient = HttpClient.newBuilder()
  .authenticator(Authenicator.getDefault())
  ...
  .build();
```

#### HTTPUrlConnection API

When using the original HTTPUrlConnection API, you can use intercept out of the box. When you initialize the evervault Java SDK with intercept enabled (enabled by default). It sets the JVM properties for sending a request through a proxy.

```java
// Note This is done automatically when you setup the Evervault SDK

System.setProperty("http.proxyHost", proxyHost);
System.setProperty("http.proxyPort", proxyPort);
System.setProperty("https.proxyHost", proxyHost);
System.setProperty("https.proxyPort", proxyPort);
```

### Manual Proxy

If you use a different http client to the clients above, you can setup relay interception by setting the http client to proxy requests through relay with these details:

| Setting   | Value                                                                   |
|-----------|-------------------------------------------------------------------------|
| host      | strict.relay.evervault.com                                              |
| port      | 8443                                                                    |
| user      | Your Evervault Team's UUID (Can be found in the Evervault Dashboard)    |
| password  | Your Evervault Team's API_KEY (Can be found in the Evervault Dashboard) |

### encrypt

**encrypt** will encrypt your data and return an object which is a String in case you passed a literal type like bool, string, int, float, char, byte. 

In case you pass a map<literal, literal> then the key will be preserved and the value will be an encryped string. If value is another map for example, it will follow the sample principle recursively.

In case you pass a vector with literals the return will be vector with encrypted strings. 

### run

run will send the data to your cage to be processed.

### constructor

Evervault constructor expects your api key which you can retrieve from evervault website. There are also optional parameters.

```java
var Evervault = new Evervault(API_KEY)
```

| Parameter        | Type                         | Description                                                                                                                          |
| ---------------- | ---------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| `apiKey`         | `String`                     | The API key of your Evervault Team                                                                                                   |
| `curve`          | `Evervault.EcdhCurve`        | The elliptic curve used for cryptographic operations. See [Elliptic Curve Support](/reference/elliptic-curve-support) to learn more. |
| `intercept`      | `Boolean`                    | Route outbound requests through Evervault to automatically decrypt encrypted fields.                                                 |
| `ignoreDomains`  | `String[]`                   | An array of hostnames which will not be routed through Evervault for encryption. eg [ "api.example.com", "support.example.com" ]        |


### Example
```java
private static class Bar {
    public String name;

    public static final String NAME_CONTENT = "Foo";

    public static Bar createFooStructure(Evervault evervault) throws EvervaultException {
        var bar = new Bar();
        bar.name = (String) evervault.encrypt(NAME_CONTENT);

        return bar;
    }
}

void encryptAndRun() throws EvervaultException {
    var evervault = new Evervault(getEnvironmentApiKey());

    var cageResult = evervault.run(cageName, Bar.createFooStructure(evervault), false, null);
}
```

### Changelog

#### 1.0.2

* Evervault urls ending with slash now fixed.

#### 2.0.1

* Evervault Java SDK now supports intercepting requests for decryption.

#### 2.0.2

* Update README with CA instructions

### 2.0.3

* Move to new package name structure

### 2.0.4

* Fix issue with Java 11 Http Client and proxy