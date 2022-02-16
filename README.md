[![Evervault](https://evervault.com/evervault.svg)](https://evervault.com/)

# Evervault Java SDK

The [Evervault](https://evervault.com) Java SDK is a toolkit for encrypting data as it enters your server, and working with Cages. 

## Getting Started

Before starting with the Evervault Java SDK, you will need to [create an account](https://app.evervault.com/register) and a team.

For full installation support, [book time here](https://calendly.com/evervault/cages-onboarding).

## Documentation

See the Evervault [Java SDK documentation](https://docs.evervault.com/sdk/java).

## Installation

Our Java SDK is distributed via [maven](https://search.maven.org/artifact/com.evervault/lib), and can be installed using your preferred built tool.

### Gradle
```sh
implementation 'com.evervault:lib:1.0.1'
```

### Maven
```xml
<dependency>
  <groupId>com.evervault</groupId>
  <artifactId>lib</artifactId>
  <version>1.0.1</version>
</dependency>
```

## Usage

You have access to two main methods.

### encrypt

**encrypt** will encrypt your data and return an object which is a String in case you passed a literal type like bool, string, int, float, char, byte. 

In case you pass a map<literal, literal> then the key will be preserved and the value will be an encryped string. If value is another map for example, it will follow the sample principle recursively.

In case you pass a vector with literals the return will be vector with encrypted strings. 

### run

run will send the data to your cage to be processed.

### constructor

Evervault constructor expected your api key which you can retrieve from evervault website.

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