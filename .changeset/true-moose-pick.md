---
"evervault-java": minor
---

Upgrade BouncyCastle from `bcprov-jdk15on:1.70` to `bcprov-jdk18on:1.84` to
pick up outstanding security fixes and move off the unmaintained `jdk15on`
line.

**Action required for some consumers.** The Maven coordinate changed
(`bcprov-jdk15on` → `bcprov-jdk18on`), but both jars ship classes under
identical `org.bouncycastle.*` packages. If your project also pulls in
`bcprov-jdk15on` directly or transitively via another dependency, you will
end up with both jars on the classpath — build tools don't dedupe across
different artifact ids. Classloader ordering then decides which BC "wins"
at runtime, which can cause subtle crypto failures. Evict any remaining
`bcprov-jdk15on` from your dependency tree (Gradle `exclude`, Maven
`<exclusions>`), or add an explicit dependency on `bcprov-jdk18on` at the
version you want.

`InvalidCipherException` gains a new public constructor
`InvalidCipherException(Throwable cause)` that preserves the underlying
cause. The existing `InvalidCipherException(InvalidCipherTextException)`
constructor is now `@Deprecated` because it leaks BouncyCastle types into
the SDK's public API; it will be removed in the next major release. Switch
any direct construction to the `Throwable` overload.
