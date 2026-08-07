# Conventions

These are patterns **inferred from the existing code**, plus an explicit list of anti-patterns you should NOT reproduce. When in doubt, match the surrounding file.

## Package & module layout
- Packages are `com.eliassen.crucible.<module>.<layer>`. Layers seen: `helpers`, `sharedobjects`, `pageobjects`/`pageObjects`, `stepdefinitions`/`stepDefinitions`, `drivers`, `objects`, `workers`, `main`, `constants`, `shared`, `models`.
- **Casing of layer package names is inconsistent** across modules: `core` uses `pageobjects`/`stepdefinitions` (lowercase), `web` uses `pageObjects` but `stepdefinitions`, `db` uses `stepDefinitions`. Follow the casing already used in the module you are editing; do not "fix" it in unrelated files.
- The `ai` module places report POJOs under `com.eliassen.crucible.models` (no `.ai` segment) — an exception to the rule.

## Static-facade / static-utility style
- Helpers are almost always **all-static classes with a private constructor** (`NavHelper(){}`, `SystemHelper(){}`, `UserHelper`, `ApiHelper`). Do not add instance state to a helper.
- **`CurrentPage` (web) and `SystemHelper` (common) are static facades over `MasterMind`.** New web capabilities are typically exposed as a static method on `CurrentPage` that delegates to a helper, which reads/writes `MasterMind`. Keep that indirection rather than reaching into `MasterMind` from a step definition.
- State is never stored in fields on helpers/steps — it goes into `MasterMind` (thread-partitioned) or, for DB, `CentralCommand` (global static).

## Step definitions
- Step-def classes are **thin**: each step delegates to a helper/facade in one or two lines (see `web/.../stepdefinitions/NavigationSteps.java`).
- **Two annotation styles coexist** and both are in active use:
  - Regex style: `@And("^I click on (?:the )?\"([^\"]*)\"$")`
  - Cucumber-expression style: `@Given("I wait \"{double}\" seconds")`
  Prefer matching the style already used in the file you are extending. Element/text arguments are conventionally quoted in the Gherkin (`"elementName"`), and `(?:the )?` optional articles are common.
- Feature files live under `src/test/resources/.../features/**` and are lowercased by convention (`demo-framework/README.md`). Client frameworks remap them to `features/` via a `<testResource>` targetPath.

## Tags as data
- Cucumber tags are parsed in `@Before(order=1)` into persisted storage as `<tagname>_tag` (`web/.../BeforeHooks.java`, `CurrentPage.curateTagNameForStorage`). Tags may carry a value: `@key=value` is stored as key `key_tag` → `value`.
- The `@pageObject=<Name>` tag drives reflective page-object selection in `@Before(order=2)`. Feature-name tags and prefixes like `gtst-`, `na-`, `ns-`, `gca-` are filtered by the doc generators (`taglibrary/config.json`).
- Hooks are explicitly **ordered** (`order = 0, 1, 2, 10001` before; `0, 99999` after). Respect the ordering contract when adding hooks.

## Element resolution
- `CurrentPage.element(String)` distinguishes an xpath from a page-object item name by checking for `//` (`web/.../sharedobjects/CurrentPage.java:54-59`). Page-object item names map to xpaths registered in the page object's `fillPageTable()`.

## Waiting / synchronization
- Every element look-up runs `MasterMind.checkProgress()` first to let the app finish updating. **Configure this, don't sleep.** A `ProgressHandler` subclass sets `PROGRESS_INDICATOR_XPATH` (spinner to wait out) and/or relies on `settings.waitForAngular=true` (waits for Angular change detection to settle); the base `ProgressHandler.checkProgress()` implements both, bounded by `PROGRESS_HANDLER_MAX_WAIT`. Do not override `checkProgress()` with a fixed `Thread.sleep`/`TestHelper.wait(...)` — that races the render cycle and is a known cause of flaky failures.
- Explicit waits go through `WaitManager.getWaitDuration(...)` (millisecond-accurate; fractional/sub-second values are preserved). Wait for the *right* condition: use `visibilityOfElementLocated` when the next action needs the element visible, not `presenceOfElementLocated` (in-DOM but possibly not rendered).

## Configuration & credentials
- Read config/params through `SystemHelper.getCommandLineParameter` / `getApplicationSetting*` / `getConfigSetting*` (dotted paths, precedence: thread cache → `-D` → env var → `config.json`). Do not read `System.getProperty`/`System.getenv` directly in new code.
- Credentials come from `users.json` via `UserHelper`; never hardcode. Encrypted passwords use the `encryption` module.
- Config JSON is parsed with `org.json` (lenient — tolerates trailing commas). Do not rely on that leniency; write strict JSON.

## Naming
- Classes `PascalCase`; methods/fields `camelCase`; constants `SCREAMING_SNAKE_CASE`. Interface `iPageObject` uses a lowercase-`i` prefix (unusual; local to that one interface).
- Constant strings for keys/headers/HTTP methods are pulled out as `public static final` fields (e.g. `ApiHelper.GET`, `MasterMind.DRIVER`). Follow this rather than inlining magic strings.
- Javadoc is present and encouraged on library APIs (`ApiHelper`, page-object base). Step-definition doc comments feed the `stepdefinitionlibrary` generator, so document steps you add.

## Testing patterns
- **Behavioral tests** are `.feature` files executed through a `RunCucumberTest` (extends `RunCucumberTestBase`), NOT the IDE Cucumber runner. This is the primary "test" mechanism and is what `-DskipTests` skips during install.
- **Unit tests** are JUnit-5 classes named `*Tests` under `src/test/.../unitTests/` (see `demo-framework`). The root `ModuleTestSuite` (`src/test/.../ModuleTestSuite.java`) is a JUnit-platform suite matching `.*Test`.
- Mock browser runs use `DriverName.mock` and the `web/.../drivers/mocks/*` stack for driverless tests.

## Anti-patterns observed — do NOT reproduce these

These exist in the codebase but should not be copied into new code:

1. **Silent exception swallowing.** Pervasive empty/near-empty catches: `catch (AssertionError a) { //do nothing }` (`ParallelHelper`), `catch (NullPointerException n) { //we do not care }` (`BeforeHooks`), `catch (WebDriverException w) {/* we don't care */}` (`AfterHooks`), and `catch (Exception e) { System.out.println(...); return null; }` (`EncryptionHelper`). New code should let failures surface or log with context and rethrow.
2. **Unbounded recursive retry.** `NavHelper.clickOn` calls itself on `StaleElementReferenceException` with no retry cap or backoff (`web/.../helpers/NavHelper.java:30-31`) — a StackOverflow risk that has been "fixed" repeatedly. Use a bounded retry/explicit wait instead.
3. **Repeated `findElement` for the same locator.** `clickOn` re-locates the element up to three times in one call, which itself invites staleness. Locate once, act, retry deliberately.
4. **Non-thread-safe shared collections in parallel code.** `ParallelHelper` writes to plain `ArrayList`s from multiple threads without synchronization. Use concurrent collections when crossing threads.
5. **`catch (Exception)` / catch-and-fallback for control flow.** Prefer specific exceptions and explicit conditions.
6. **`printStackTrace()` / `System.out.println` for errors.** Use the framework `Logger`.
7. **Switch fall-through that silently mis-maps.** `DriverFactory` lets `remote` fall through to `edge`, and `explorer` fall through to the `default` (chrome) — a real defect (see known-issues). Handle every enum case explicitly.
8. **Returning `null` on failure** (`EncryptionHelper.encrypt/decrypt`, various helpers) instead of failing loudly. Callers then NPE far from the cause.
9. **Wildcard imports** (`import org.openqa.selenium.*;`). Prefer explicit imports.
10. **`@Deprecated` methods left alongside their replacements** (`ApiHelper.sendGetRequest` → `callApi`). If you add a replacement, migrate callers; don't leave both as equal options.
