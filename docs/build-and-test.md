# Build, Test, Run, Deploy

All commands below are taken from the actual build files. Anything not backed by a build/CI file is marked UNCERTAIN rather than invented.

## Prerequisites
- **Java 17** (enforced by `maven.compiler.source/target`).
- **Maven** — use the bundled wrapper (`./mvnw` on Unix, `mvnw.cmd` on Windows). README states Maven 3.6.0+; UNCERTAIN on the exact pinned wrapper version.
- **.NET 8 SDK** — only for `dashboard/`.
- Git.

## Build (Java library modules)

From the repo root:

```bash
# Full build + test + install all reactor modules to ~/.m2
./mvnw clean install

# Fast install without behavioral tests (recommended for local dev — tests need
# browser/DB/env setup). This is the documented normal path.
./mvnw clean install -DskipTests

# Compile only / test only
./mvnw clean compile
./mvnw test

# One module and everything it depends on
./mvnw -pl web -am clean install
```

The 9 library modules build via the root reactor (`pom.xml`). `demo-framework/` and `dashboard/` are **not** in the reactor and build separately.

## Test

- **Unit tests** run through Maven Surefire. No POM configures a Surefire version, so Maven falls back to its **very old default (2.12.4)**, which only runs **JUnit 4** tests. Many unit tests in the reactor are **JUnit 5 (Jupiter)** (e.g. `web/.../WaitManagerTests`, `core/.../ProgressHandlerTests`) — Surefire 2.12.4 silently reports `Tests run: 0` for them. This is the "tests not discovered by the build" gotcha.
- **Run a single test class / method** (JUnit 4 only, via the default Surefire):
  ```bash
  ./mvnw17 -pl <module> test -Dtest=SomeClass
  ./mvnw17 -pl <module> test -Dtest=SomeClass#someMethod
  ```
- **Run JUnit 5 (Jupiter) unit tests** by invoking a modern Surefire directly (the reactor already has `junit-jupiter-engine` + `junit-platform-suite-api` on the test classpath):
  ```bash
  ./mvnw17 -pl <module> test-compile
  ./mvnw17 -pl <module> org.apache.maven.plugins:maven-surefire-plugin:3.2.5:test -Dtest=SomeTests -DfailIfNoTests=false
  ```
  (Use `./mvnw17`, not `./mvnw` — see the JDK-17 wrapper note below.)
- **Behavioral (Cucumber) tests** are NOT run the same way as unit tests. They execute through a `RunCucumberTest` main/JUnit runner that extends `RunCucumberTestBase` and calls `io.cucumber.core.cli.Main`. They are what `-DskipTests` skips at install time. Running them via an IDE's built-in Cucumber plugin is **unsupported** (`demo-framework/README.md`) — required environment/browser/tag context would be missing.

  Runtime parameters (passed as `-D` system properties, env vars, or `config.json`):
  - `-Dbrowser=<chrome|firefox|edge|mock|...>` (default chrome; enum `DriverName`)
  - `-Denvironment=<name>` (selects users/urls/connection strings)
  - `-Denvironments=<a,b,c>` (comma-separated → parallel per-environment run via `ParallelHelper`)
  - `-Dcucumber.filter.tags="<tag expression>"` (blank/absent = run all scenarios)
  - Env vars: `IMPLICIT_WAIT`, `PAGE_LOAD_TIMEOUT`; optional `GRAB_CONSOLE_LOGS`, `GRAB_DOM`.
  - **Inter-step settling** (see `ProgressHandler`, `core/.../sharedobjects/ProgressHandler.java`): between steps the framework waits out the app's update cycle deterministically rather than sleeping a fixed time. Bounded by `PROGRESS_HANDLER_MAX_WAIT` (seconds; fractional allowed; default 15). A `ProgressHandler` subclass sets `PROGRESS_INDICATOR_XPATH` (and optionally `PROGRESS_INDICATOR_FINISHED_STATE_XPATH`) to wait for the app's spinner to clear. Set `settings.waitForAngular=true` in `config.json` to additionally wait for Angular change detection to settle (`window.getAllAngularTestabilities()`); no-op on non-Angular pages.

- **Integration tests**: there is no separate integration-test phase configured (no Failsafe plugin, no `*IT` convention). The DB and web behavioral scenarios *are* the integration layer, and they require live browsers/databases. UNCERTAIN: how integration tests are gated in CI (no CI config in repo — see below).

## Lint / static analysis
**None configured.** No Checkstyle, Spotless, PMD, SpotBugs, or JaCoCo plugin appears in any POM. There is no formatter config in the repo. UNCERTAIN whether linting happens in an external CI pipeline.

## Building & running the example client framework (`demo-framework/`)

Not part of the reactor; build it after the Crucible artifacts are installed to `~/.m2`:

```bash
./mvnw clean install -DskipTests          # publish crucible libs first
cd demo-framework && ../mvnw package       # builds a fat jar-with-dependencies
```

- Packaged via `maven-assembly-plugin` (`jar-with-dependencies`). The manifest `mainClass` is `stepDefinitions.com.eliassen.crucible.demo.RunCucumberTest` — **this FQCN looks malformed/reversed** (the real class is `com.eliassen.crucible.demo.stepDefinitions.RunCucumberTest`); see known-issues before relying on `java -jar`.
- Bundles `chromedriver`/`geckodriver` and `*.json` resources; a `demo-framework/Dockerfile` also exists.
- Run modes (per `demo-framework/README.md`): IDE JUnit run config, Maven, or executable jar — passing the runtime parameters above.

## Documentation generators (`stepdefinitionlibrary`, `taglibrary`)
Each is a library with a `Main` class that scans a target framework (loose files or an external test JAR, configured in that module's `config.json`) and emits Markdown. **No `exec-maven-plugin` is configured**, so there is no `mvn exec:java` target. UNCERTAIN on the intended invocation — presumably run the `Main` class from an IDE or `java -cp <jar-with-deps> ...MainClass`. Output paths/filenames and the source JAR path are set in `stepdefinitionlibrary/config.json` / `taglibrary/config.json`.

## Versioning / release
From `README.md` (backed by the `versions-maven-plugin` in the parent POM):

```bash
./mvnw versions:set -DnewVersion=X.Y.Z   # update version across all modules
./mvnw versions:commit                    # keep the change
./mvnw versions:revert                    # undo
```

Note: `demo-framework` and `taglibrary`'s `core` dependency hardcode `2.0.1-SNAPSHOT` rather than a property, so `versions:set` will not update them consistently (see known-issues).

## Deploy / publish
- **No Maven distribution is configured** — there is no `<distributionManagement>` or `maven-deploy-plugin` beyond the default. The README says a shared artifact feed is "Coming Soon"; today, consumers must **build from source and `mvn install` to their local `~/.m2`**.
- **Dashboard (.NET):**
  ```bash
  cd dashboard/Lightwell-Testing-Dashboard-2
  dotnet restore
  dotnet build
  dotnet run
  dotnet test    # from ../Lightwell-Testing-Dashboard-Tests (MSTest)
  ```
  Container build via `dashboard/docker/Dockerfile` (base `mcr.microsoft.com/dotnet/aspnet:8.0`, entrypoint `dotnet Lightwell-Testing-Dashboard-2.dll`, `JENKINS_HOME=/Jenkins`). Jenkins connection configured via `appsettings.json` (`jenkins:`) / `JENKINS_*` env vars.

## CI
**There is no CI configuration in this repository** — no `.github/workflows/`, `Jenkinsfile`, `azure-pipelines.yml`, or `.gitlab-ci.yml` exists. The .NET dashboard consumes a Jenkins server, so CI is presumably defined **externally in Jenkins**, but the pipeline definition is not in this repo. UNCERTAIN: exact build/test/deploy commands the real pipeline runs. Do not assume any CI gate exists when making changes.
