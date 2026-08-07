# Architecture

## What Crucible is

Crucible is a **behavioral (BDD) test-automation framework distributed as reusable Java libraries**, not a runnable application. Consumers build their own *client framework* (the in-repo example is `demo-framework/`) that depends on the published Crucible artifacts, adds page objects + `.feature` files + step definitions, and executes Cucumber. The repo also ships a separate **.NET results dashboard** (`dashboard/`) that visualizes CI runs from Jenkins.

Parent Maven coordinates: `com.eliassen.crucible:crucible:2.0.1-SNAPSHOT` (see `pom.xml`).

## Tech stack (exact versions)

All versions below are read directly from the build files. Java modules inherit shared dependencies from the parent `pom.xml`.

### Java platform & build
- Java **17** (`maven.compiler.source`/`target` in `pom.xml:37-38`)
- Maven via wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`). UNCERTAIN: exact Maven version — not pinned in a readable `maven-wrapper.properties` that I inspected; README states "Maven 3.6.0 or higher".
- `maven-compiler-plugin` **3.15.0**, `versions-maven-plugin` **2.15.0** (`pom.xml:118-130`)

### Shared dependencies (parent `pom.xml:44-117`)
- `io.cucumber:cucumber-java` **7.34.1** (property `cucumber.version`)
- `io.cucumber:cucumber-junit` **7.34.1** (transitively pulls JUnit **4.x**)
- `org.seleniumhq.selenium:selenium-java` **4.41.0**
- `com.fasterxml.jackson.core:jackson-databind` / `jackson-annotations` / `jackson-core` **2.18.3** (property `jackson.version`)
- `org.json:json` **20220320**
- `commons-io:commons-io` **2.11.0**
- `com.github.javafaker:javafaker` **1.0.2**
- `org.apache.httpcomponents:httpclient` **4.5.13**
- `com.google.guava:guava` **31.1-jre**
- `org.jsoup:jsoup` **1.18.3**
- `org.junit.jupiter:junit-jupiter-engine` **5.11.4** (test scope)
- `org.junit.platform:junit-platform-suite-api` **1.11.4** (test scope)

Note: both JUnit 4 (via `cucumber-junit`; `RunCucumberTestBase` uses `org.junit.AfterClass`) and JUnit 5 (Jupiter + platform-suite; `src/test/.../ModuleTestSuite.java`) are present.

### Module-specific dependencies
- **db** (`db/pom.xml`): `mssql-jdbc` **10.2.0.jre17**, `com.oracle.ojdbc:ojdbc8` **19.3.0.0**, `org.apache.derby:derby` **10.16.1.1**, `com.mysql:mysql-connector-j` **8.0.31**, `com.h2database:h2` **2.1.214**, `org.postgresql:postgresql` **42.7.1**
- **ai** (`ai/pom.xml`): `dev.langchain4j:langchain4j` **1.12.2**, `dev.langchain4j:langchain4j-bedrock` **1.12.2**, `org.projectlombok:lombok` **1.18.42**, `com.github.jai-imageio:jai-imageio-jpeg2000` **1.4.0** (also declares property `langchain4j.beta.version` **1.12.2-beta22**, UNCERTAIN if used — no dependency references it)
- **frameworkbrowser** (`frameworkbrowser/pom.xml`): `com.github.Steppschuh:Java-Markdown-Generator` **1.3.2** (via JitPack repo)
- **demo-framework** (`demo-framework/pom.xml`, standalone, not in reactor): `com.rabbitmq:amqp-client` **5.11.0**, `org.slf4j:slf4j-api` / `slf4j-simple` **1.7.32**, `org.apache.derby:derbytools` **10.16.1.1**

### Dashboard (.NET — `dashboard/Lightwell-Testing-Dashboard-2/`)
- Target framework **net8.0**, SDK `Microsoft.NET.Sdk.Web` (`Lightwell-Testing-Dashboard-2.csproj`)
- `Newtonsoft.Json` **13.0.4**, `Swashbuckle.AspNetCore` **10.1.7**, `AspNet.Security.OAuth.GitHub` **8.3.0**, `Microsoft.AspNetCore.Razor.Runtime` **2.3.0**
- Tool: `dotnet-ef` **6.0.0** (`.config/dotnet-tools.json`)
- Test project `Lightwell-Testing-Dashboard-Tests` (net8.0, MSTest). UNCERTAIN on exact test-package versions — I did not open that csproj myself (reported by a sub-agent as MSTest 4.2.1 / Microsoft.NET.Test.Sdk 18.4.0 / coverlet.collector 10.0.0; verify before relying on these).

## Module layout & dependency graph

All library modules are under `com.eliassen.crucible.<module>` and listed in the parent reactor (`pom.xml:12-22`). Crucible-internal dependencies only:

```
encryption            (leaf)
common                → encryption
core                  → common
db                    → common
frameworkbrowser      → common
stepdefinitionlibrary → frameworkbrowser
taglibrary            → frameworkbrowser, core
web                   → core
ai                    → core, web
demo-framework        → web, db     (groupId com.eliassen.testing; NOT in the reactor)
```

Role of each module:
- **encryption** — AES string encrypt/decrypt (`EncryptionHelper`) for credentials stored in `users.json`; Swing GUI `encryption.app.EncryptionApplication`. See known-issues for crypto weaknesses.
- **common** — cross-cutting utilities: `SystemHelper` (config/param resolver), `JsonHelper`, `FileHelper`, `UserHelper`, logging.
- **core** — driver-agnostic engine: `sharedobjects.MasterMind` (thread-local state registry), page-object model, `ApiHelper` (HTTP), data generation, `stepdefinitions.RunCucumberTestBase` (Cucumber CLI launcher), `ParallelHelper`.
- **web** — Selenium UI layer (primary consumer artifact): `drivers.*` (driver factory + browser wrappers + mock WebDriver stack), helpers (`NavHelper`, `DomHelper`, `TableHelper`, `ScreenShotter`), `sharedobjects.CurrentPage`, before/after hooks, web step definitions.
- **db** — multi-vendor JDBC database testing with its own step definitions.
- **ai** — LLM-assisted visual testing via AWS Bedrock (LangChain4j); also holds Cucumber-report POJOs.
- **frameworkbrowser** — resource crawler + Markdown generator; foundation for the two doc generators.
- **stepdefinitionlibrary / taglibrary** — Markdown documentation generators (each has a `Main`), NOT runtime test libraries.

## Execution flow (the "request" lifecycle)

There is no HTTP request cycle in the framework itself; the analogous flow is **scenario execution**. For a web/UI scenario:

1. **Entry** — A client runner (e.g. `demo-framework/.../stepDefinitions/RunCucumberTest`) extends `RunCucumberTestBase` (in `web`/`core`) and calls `mainLogic(args, cucumberOptions)` (`web/.../RunCucumberTestBase.java`). Running through an IDE's built-in Cucumber runner is explicitly **unsupported** (env/browser/tag wiring would be missing — `demo-framework/README.md`).
2. **Options assembly** — `mainLogic` joins the static `cucumberOptions` (report plugins + `--glue` packages) with runtime `args`, stores them as the `cucumber.expression` system property.
3. **Serial vs parallel** — If an `environments` (plural) parameter is present, `ParallelHelper.runInParallel` spawns one thread per environment (thread named `cucumber-<env>`), each calling `io.cucumber.core.cli.Main.run`. Otherwise a single `Main.run` executes (`web/.../RunCucumberTestBase.java:24-37`).
4. **Cucumber loads glue + features** — `--glue` points at Crucible step packages (`com.eliassen.crucible.core.stepdefinitions`, `...web.stepdefinitions`) plus the client's own step packages.
5. **Before hooks** (`web/.../BeforeHooks.java`, ordered): `order=0` records the feature name and, on feature change, clears thread state (optionally preserving a reusable driver); `order=1` parses scenario tags into persisted `key_tag` entries (tags can carry `@key=value`); `order=2` resolves a `PageObjectBase` from the `@pageObject` tag via `PageObjectResolver` (reflection using `settings.baseNameSpace`); `order=10001` launches the browser and navigates to the page URL.
6. **Steps execute** — Step definitions are thin and delegate to `CurrentPage` / `NavHelper` (web) or the relevant helpers. `CurrentPage` is a static facade over `MasterMind`. `MasterMind.checkProgress()` is invoked on element lookups to wait out spinners via the client's `ProgressHandler`.
7. **After hooks** (`web/.../AfterHooks.java`): `order=99999` optionally grabs browser console logs (`@grabConsoleLogs` / `GRAB_CONSOLE_LOGS`), and on failure attaches a screenshot + DOM dump (unless `@noDom`/`GRAB_DOM=false`); `order=0` quits non-reusable drivers and any extra drivers stored on the page object.

DB scenarios connect through JDBC via `db` step definitions; API scenarios build/send requests through `core`'s `ApiHelper` (raw `java.net.HttpURLConnection`).

## Where state lives

- **Runtime test state** is thread-scoped in `core/.../sharedobjects/MasterMind` — the current page object, driver, scenario, environment, per-scenario storage and cross-scenario "persisted" storage, keyed by thread name so parallel per-environment runs stay isolated. `web/.../sharedobjects/CurrentPage` and `common/.../SystemHelper` are static facades in front of it. (Detailed field inventory: see `data-model.md`.)
- **Configuration** resolves through `SystemHelper.getCommandLineParameter` with precedence: in-memory thread cache → `-D` system property → environment variable → `config.json` on the classpath (dotted paths like `settings.baseNameSpace`). Each module/consumer ships its own `config.json`.
- **Credentials** live in `users.json` / `testUsers.json` (per-environment, per-user-type, with a `default` fallback), read by `common/.../UserHelper`; passwords may be plaintext or `encryptedPassword` (decrypted at runtime by the `encryption` module).
- **Persistence to databases** only happens in the `db` module (JDBC), and only against systems under test — Crucible stores no data of its own in a database.
- **Dashboard state**: the .NET dashboard holds Jenkins-derived results in memory via background hosted services; DataProtection keys are persisted to disk (`./jenkins`). UNCERTAIN whether it uses a database — `dotnet-ef` is present as a tool but I did not confirm a live EF DbContext.

## Auth model

- **Test credentials**: abstract "users" per environment in `users.json`; passwords optionally AES-encrypted at rest and decrypted at runtime (`encryption` module). See known-issues for cipher weaknesses.
- **API auth**: `ApiHelper` sets an `Authorization` header (`core/.../ApiHelper.java:35`); the concrete scheme is supplied per request/step by the client framework.
- **Dashboard auth**: optional cookie + **GitHub OAuth** (`AspNet.Security.OAuth.GitHub`), toggled by an `authorization:authProviderName` setting (reported from `Startup`; verify in `dashboard/.../Startup.cs`).

## External integrations

- **Browsers / Selenium** — Chrome/Firefox/Edge via Selenium 4.41.0 (`web/.../drivers/`). A Selenium Grid hub address exists in config (`core/config.json` `selenium_hub.address`) but the **`remote` driver path is not implemented** in `DriverFactory` (see known-issues).
- **AWS Bedrock** — LLM chat/vision via LangChain4j `langchain4j-bedrock` (`ai/.../helpers/AiHelper.java`), selected by `settings.chatProvider`/`chatModelId`. Requires AWS credentials in the environment (UNCERTAIN: exact credential source — presumably the default AWS SDK chain).
- **Databases** — SQL Server, Oracle, Derby, MySQL, H2, PostgreSQL via JDBC (`db` module).
- **Jenkins** — the .NET dashboard polls a Jenkins server (config under `jenkins:` / `JENKINS_*` env vars) for CI results. The Java framework does not talk to Jenkins directly.
- **RabbitMQ** — only in `demo-framework` (`amqp-client`); UNCERTAIN what it is used for — appears to be example/consumer-specific.
- **Sample REST APIs** — the demo config references public APIs (restcountries, sampleapis, ESPN) for example scenarios only.
