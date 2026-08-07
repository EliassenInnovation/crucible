# Known Issues, Fragility & Mid-Migration State

Honest inventory of things that look broken, fragile, incomplete, or in transition. Cited to specific files. Severity is my judgment, not the team's.

## Bugs / defects

### 1. `NavHelper.clickOn` — unbounded recursion on stale elements  (high)
`web/.../helpers/NavHelper.java:24-39`. On `StaleElementReferenceException` the method calls itself with no retry cap or backoff, so a persistently-stale element causes infinite recursion → `StackOverflowError`. It also re-locates the same element up to three times per call (`findElement` at lines 27, 28, 29), which itself induces staleness. Git history shows **five consecutive commits** titled "Hopefully fixing stale element exp in clickOn" — this has been patched repeatedly and is still fragile. This is the single most actively-troubled piece of code in the repo.

### 2. `DriverFactory` switch mis-maps two `DriverName` values  (medium)
`web/.../drivers/DriverFactory.java:48-70`. `case remote:` has a `//TODO transfer from Driver` and **falls through to `edge`**, so requesting `remote` silently builds an Edge driver. `DriverName.explorer` is not handled at all and hits the `default` (chrome). Both are latent defects — a caller asking for one browser gets another.

### 3. Selenium Grid / remote execution is unimplemented  (medium)
`core/config.json` defines `selenium_hub.address` and `web/README.md` documents a `selenium_hub` block, but nothing in `DriverFactory` constructs a `RemoteWebDriver` from it (see #2). Grid support appears configured-but-not-wired. UNCERTAIN whether an older `Driver` class (referenced in the TODO) once handled this.

### 4. `ConnectionString.setEncrypt` ignores its parameter  (medium)
`db/.../objects/ConnectionString.java` — `setEncrypt(...)` assigns `this.encrypt = encrypt` reading the field instead of the incoming parameter, so the setter is a no-op. TLS-encrypt configuration for DB connections may not take effect.

### 5. `demo-framework` executable-jar main class looks wrong  (medium)
`demo-framework/pom.xml:105` sets the assembly manifest `mainClass` to `stepDefinitions.com.eliassen.crucible.demo.RunCucumberTest`, but the actual class is `com.eliassen.crucible.demo.stepDefinitions.RunCucumberTest`. `java -jar` on the produced fat jar will likely fail with `ClassNotFoundException`. UNCERTAIN if the jar is actually used this way in practice.

### 6. Cucumber HTML report path typo  (low)
`demo-framework/.../stepDefinitions/RunCucumberTest.java:16` — plugin option `html:cucumber-rpoerts/cucumber.html` ("rpoerts"), so the HTML report is written to a misspelled directory separate from the json/junit `cucumber-reports/` output.

### 7. `demo-framework/src/main/resources/config.json` is not strict JSON  (low)
Trailing comma after `progressHandlerName` (line 17). Tolerated by the `org.json` parser Crucible uses, but would break strict parsers (Jackson without `ALLOW_TRAILING_COMMA`, most linters).

## Concurrency fragility

### 8. `ParallelHelper` shares non-thread-safe collections across threads  (medium)
`core/.../helpers/ParallelHelper.java`. Each per-environment thread writes to plain `ArrayList`s (`exitCodes`, `screenShotPaths`) on a shared inner-class instance without synchronization — a data race. It also swallows `AssertionError` (`catch (AssertionError a) { //do nothing }`), hiding real test failures from the parallel path.

### 9. Two divergent state models  (medium, design)
`MasterMind` (core/web) partitions state by thread name for parallel safety, but `CentralCommand` (`db/.../main/CentralCommand.java`) is **global static and not thread-partitioned**. Mixing DB steps into parallel per-environment runs would cross state between threads. UNCERTAIN whether parallel runs are ever used with DB scenarios in practice.

## Security weaknesses (encryption module)

### 10. Weak cipher and key derivation  (medium)
`encryption/.../EncryptionHelper.java`. Uses `AES/ECB/PKCS5Padding` — **ECB mode** leaks structure across identical plaintext blocks and is not semantically secure. The key is derived by `SHA-1` truncated to 16 bytes (128-bit). The module's own `README.md` carries a TODO to move to SHA-256. Failures are swallowed (`System.out.println` + `return null`). This protects `users.json` passwords at rest, so treat it as weak-but-present, not real secrecy.

## Pervasive smell: silent failure handling
Beyond the specific spots above, empty/ignore catches are widespread and actively hide problems: `BeforeHooks` (`catch NullPointerException { //we do not care }`), `AfterHooks` (`catch WebDriverException {/* we don't care */}`), `ParallelHelper`, `EncryptionHelper`. When debugging "nothing happened / no error" behavior, suspect a swallowed exception first. (Listed as an anti-pattern in `conventions.md`.)

## Incomplete / mid-migration

### 11. Playwright driver is an empty stub  (informational)
`web/.../drivers/CruciblePlaywrightDriver.java` is `class CruciblePlaywrightDriver extends CrucibleWebdriver {}` with no body. There is **no Playwright dependency in any POM**, no `playwright` value in the `DriverName` enum, and no factory branch. A Selenium→Playwright migration appears planned but not started. Do not assume Playwright works.

### 12. `frameworkbrowser.Main` is a stub  (informational)
`frameworkbrowser/.../Main.java` is a `Hello world!` placeholder. The module's real functionality is used via `stepdefinitionlibrary`/`taglibrary`; the standalone entry point does nothing.

### 13. `ApiHelper` mid-refactor  (informational)
`core/.../helpers/ApiHelper.java` has `@Deprecated` methods (e.g. `sendGetRequest`) alongside the newer `callApi`. Uses raw `java.net.HttpURLConnection` despite `httpclient` being on the classpath. `ApiResponse.executionTime` is declared but never set. Prefer `callApi` in new code.

### 14. `MasterMind.progressHandler` dead field  (informational)
The static `progressHandler` field in `MasterMind` appears unused; the live handler is stored in the per-thread table. Don't wire new code to the static field.

## Documentation & repo hygiene

### 15. Most module READMEs are empty templates  (low)
`core/README.md`, `common/README.md`, `stepdefinitionlibrary/README.md`, `taglibrary/README.md`, `frameworkbrowser/README.md` are the unfilled Azure DevOps "TODO: Give a short introduction…" template. Only `encryption`, `web` (brief), `db`, and `demo-framework` READMEs have real content. Do not trust module READMEs as a source of truth — read the code (or these docs).

### 16. Root README placeholders / no published artifacts  (low)
`README.md` License section is `[Add your license information here]` (though a top-level `LICENSE` file exists), Support is generic, and the Maven artifact feed is "Coming Soon" — so there is currently no remote repository; consumers must build from source and `mvn install` locally.

### 17. Version pins that bypass `versions:set`  (low)
`taglibrary/pom.xml:26` hardcodes its `core` dependency version as `2.0.1-SNAPSHOT` instead of `${project.version}` (other modules use the property). `demo-framework/pom.xml` hardcodes `version.crucible` as `2.0.1-SNAPSHOT`. A `mvn versions:set` will leave these behind → version drift.

### 18. Committed backup solution  (low)
`dashboard/Lightwell-Testing-Dashboard-2/Backup/Lightwell-Testing-Dashboard-2.sln` is a checked-in backup copy of the solution — stale duplicate that can confuse tooling/searches.

### 19. Low-signal git history  (informational)
39 commits total, many duplicated messages ("Adding AI" ×7, "Ignoring some tests" ×3, "Hopefully fixing stale element exp in clickOn" ×5). The bulk of the code arrived under a single "initial" commit. **Git history is not a reliable source of design rationale** for this repo. The two most recent commits ("Total Building and Total Queued get stuck…") are active bug-fixing on the dashboard's Jenkins queue models.

## No CI in repo
No `.github/workflows`, `Jenkinsfile`, `azure-pipelines.yml`, or `.gitlab-ci.yml`. See `build-and-test.md` — assume no automated gates run on your changes from within this repo.
