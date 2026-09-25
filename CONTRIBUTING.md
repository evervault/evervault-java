[![Evervault](https://evervault.com/evervault.svg)](https://evervault.com/)

# Contributing

Bug reports and pull requests are welcome on the [GitHub issue tracker](https://github.com/evervault/evervault-java/issues).

## Building and testing

The SDK lives in `lib/`. Use the Gradle wrapper:

```sh
./gradlew build
./gradlew test
```

`build` runs the test task, so the credentials note below applies to it too.

### Tests that need credentials

The suite has 139 unit tests plus four end-to-end classes under `com.evervault.EndToEndTests`
that call the Evervault API. Without credentials, those four fail at initialization instead of
being skipped, so a clean checkout reports a red build:

```
EncryptTest > initializationError FAILED
143 tests completed, 4 failed
```

Either supply credentials:

```sh
TEST_EV_APP_ID=app_xxx TEST_EV_API_KEY=ev:key:... ./gradlew test
```

or run the unit tests alone:

```sh
./gradlew test --tests 'com.evervault.When*'
```

### Java compatibility

Reproducing a specific CI Java version needs that JDK installed locally. Toolchain
auto-provisioning is not configured, so the build fails with "No matching toolchains" if it
is missing:

```sh
./gradlew test -PjavaToolchainVersion=8
```

### Dependency locking

All configurations are locked. Adding, removing, or upgrading a dependency requires
regenerating the lockfiles, or resolution fails:

```sh
./gradlew dependencies --write-locks
```

Commit the resulting `lib/gradle.lockfile` and `buildscript-gradle.lockfile`.

## Changesets and releases

We use [changesets](https://github.com/changesets/changesets) to manage versions.

A PR that should land in a release needs a changeset. Run `pnpm changeset`, pick the bump
level, and describe the change for the changelog. Do not pick major for a breaking change
without team approval.

Releasing is two steps, both on `main`:

1. Merging a PR that contains changesets prompts the bot to open a "New Release" PR that
   bumps the version in `package.json` and `lib/build.gradle`.
2. Merging that PR tags the commit, creates the GitHub release, and stages the artifact to
   Sonatype. Publishing to Maven Central then waits on manual approval of the `maven-central`
   deployment environment. Until someone approves it, the release is staged but not public.

After approval the artifact appears at `com.evervault:lib:<version>` on Maven Central.
