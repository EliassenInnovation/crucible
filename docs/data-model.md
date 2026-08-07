# Data Model

Crucible is a test framework, so its "domain" is **test-execution state and configuration**, not business entities. There are two in-memory static state registries plus JSON config files; the only durable/external persistence is JDBC against systems under test. Nothing in the framework serializes its own domain state to a database or disk.

## State registries (in-memory, static)

### MasterMind — `core/.../sharedobjects/MasterMind.java`
The framework's global state hub for UI/API scenarios. `abstract`, all-static, never instantiated.

- Backed by a static `ThreadObjectTable _threadObjects` whose values are themselves per-thread `ThreadObjectTable`s, **keyed by lowercased thread name** (`Thread.currentThread().getName()`). This gives per-thread isolation for parallel Cucumber runs. It is **not** a `java.lang.ThreadLocal` — it is a shared static `Hashtable` partitioned by thread name.
- Per-thread keys (constants): `DRIVER`, `PAGE_OBJECT` (`"pageobject"`), `SCENARIO`, `PROGRESS_HANDLER`, `PERSISTED_STORAGE`, `PARAMETER`, `URL_PARAMETER`, `PROXY`, plus `LOCAL`/`NONE` defaults.
- Holds per thread: current `PageObjectBase`, Cucumber `Scenario`, `ProgressHandler`, environment (default `local`, from `-Denvironment`), page URL/URL parameter (`-Durl`), a page-scoped key/value store (delegated to the current page object's tables), and a `PERSISTED_STORAGE` table that survives page-object swaps.
- Accessors: `getPageObject/setPageObject`, `getScenario/setScenario`, `getEnvironment/setEnvironment`, `store/retrieve` (String), `storeObject/retrieveObject` (generic), `storePersisted/retrievePersisted/isPersisted`, `getPageObjectItem`, `checkProgress`.
- UNCERTAIN/dead: the static field `progressHandler` appears unused — the live handler is stored in the per-thread table.

`web/.../sharedobjects/CurrentPage.java` and `common/.../SystemHelper.java` are static facades in front of MasterMind. See `architecture.md` for the config-resolution precedence.

### CentralCommand — `db/.../main/CentralCommand.java`
The DB-module analogue of MasterMind. All-static, but **global — NOT thread-partitioned** (unlike MasterMind; relevant to concurrency, see known-issues). Holds: `HashMap<String,DBObject> dbObjects`, current `Results results`, `ArrayList<Results> resultSets`, `Scenario scenario`, `boolean keepConnectionOpen`, and a DB access token. Manages named `DBObject`s (`db()`, `db(name)`, `getDefaultDb`) and result sets.

## Page-object model — `core/.../pageobjects/`

Page objects are **not** stored in a registry or persisted; they are instantiated fresh **by reflection** from a fully-qualified class name (`PageObjectResolver.getPageObjectByName`, using config `baseNameSpace` + `additionalPageObjectPathsByEnvironment` + page name), then set as the single "current" page object on MasterMind.

- **PageObjectBase** (`abstract implements iPageObject`) — holds `ProgressHandler`, `PageObjectResolver`, `pageUrlPart`, a `PageObjectTable` (string k/v), an `ObjectMap` (object k/v), `pageName`. Subclasses implement `fillPageTable()` to register element xpaths and URLs. `getURL(env)` resolves key `url_<env>`.
- **PageObjectResolver** — stateless; `Class.forName(...).newInstance()` for page objects and progress handlers, driven by config.
- **Collection typedefs** (all case-insensitive, lowercasing keys): `ObjectMap extends HashMap<String,Object>`, `PageObjectTable extends HashMap<String,String>`, `ThreadObjectTable extends Hashtable<String,Object>` (null→`""`, adds `has(key)`).
- **iPageObject** — interface: `store`, `retrieve`, `getPageName`, `fillPageTable`, `getURL(env)`.

## API request/response — `core/.../sharedobjects/`
Transient (not persisted): `ApiRequest` (methodType, url, `Headers`, `Parameters`, `jsonPayload`, `stringPayload`, logging flags) built via fluent `ApiRequestBuilder`; `ApiResponse` (`int code`, `Hashtable headers`, `String payload`, `double executionTime`, `getJSONPayload()`); `ApiInfo` (apiUrl/method/appName). `Parameters` and `Headers` are `HashMap<String,String>` typedefs. `PreventAPILogging` enum controls request/response log redaction. UNCERTAIN: `ApiResponse.executionTime` is declared but not set in the constructor.

## User / credentials — `common/.../helpers/`
- **UserHelper** (static) reads a users JSON file via `JsonHelper`. Schema (persistence = JSON on disk/classpath):
  `{ <environment>: { <userType>: { username, password | encryptedPassword } } }`
  with fallback to the `"default"` environment/userType. Encrypted passwords are decrypted at runtime by `EncryptionHelper.decryptString`.
- **UserInfoRequest** — lookup-request POJO. Note the source misspells a field `EnvirontmentName`.

## Database entities — `db/.../objects/` and `db/.../workers/`
- **DBType** enum: `POSTGRESQL, ORACLE, ORACLE_TNS, MS_SQL_SERVER, MY_SQL, DERBY, H2`.
- **DBInfo** — full connection descriptor: driverString, protocol, port, hostName, dbName, userName, password, `DBType`, tnsEntryName, activeDirectory/encrypt/trustServerCertificate flags. Built by `DriverHelper.getInfo(DBType)` then populated from config/user data.
- **ConnectionString** — Jackson target for a `connection_strings.<name>` config block: host_name, db_name, tns_entry_name, and boolean flags. **Bug to flag**: `setEncrypt` assigns `this.encrypt = encrypt` from the field, not the parameter (see known-issues).
- **Results** `extends ArrayList<ResultRow>`; **ResultRow** `extends Hashtable<String,Object>` with typed accessors (`getInt`, `getString`, `getOracleTimeStamp`). **ObjectTable** `extends Hashtable<String,String>` (scratch store on `DBObject`).
- **DBObject** — one database + its operations; delegates execution to **DataWorker**.
- **DataWorker** — the actual JDBC persistence layer: loads the driver by reflection, opens connections via `SQLServerDataSource` / `OracleDataSource` / `DriverManager`, runs `PreparedStatement`s, maps `ResultSet` → `Results`/`ResultRow`. **This is the only genuine external persistence in the system.**
- **QueryTable** `extends Hashtable<String,String>` — static cache of named SQL loaded from `queries.json`.

## Report / AI models — `ai/.../models/` and `ai/.../models/cucumber/`
Read-only Lombok `@Data` POJOs (Jackson) that map report files; not persisted by the module.
- **Jenkins JUnit JSON**: `TestResult` → `Suite` → `TestCase` (className, duration, status, errorDetails/errorStackTrace, stdout/stderr, etc.).
- **Cucumber JSON** (standard schema): `Cucumber extends ArrayList<Feature>` → `Feature` → `Element` (scenario) → `Step` → `Result`/`Match`/`Argument`/`Embedding`, plus `Tag`/`TagLocation`.

## Dashboard models (C#) — `dashboard/Lightwell-Testing-Dashboard-2/Models/`
In-memory view models / DTOs consuming the **Jenkins REST API + RSS feed**; no DB persistence.
- **JenkinsBuild** — job/build node (`_class`, name, url, color, number, first/last build, actions, result, timestamp) with computed `IsFolder/IsBuild/IsBuilding/TestResults/Parent`.
- **BuildAction** — Jenkins action node (causes, user, FailCount/SkipCount/TotalCount).
- **BuildResult** / **BuildResultsCollection** / **Totals** — flattened per-build results and rolled-up totals by parent hierarchy.
- **JunitTestResults** (+ inner `TestSuite`/`TestCase`) — C# mirror of the Java JUnit POJOs above.
- **CucumberTrend** — feature/scenario/step pass-fail counters.
- **JobFeed / JobData / JobFeedEntry** (`Models/JobFeed/`) — parsed from Jenkins RSS; track broken/just-broke/just-fixed build states.
- **JenkinsQueue / JenkinsQueueItem / JenkinsTask** (`Models/Queue/`) — build-queue state (blocked, stuck, pending). The most recent commits ("Total Building/Queued get stuck…") patch this area.
- Trivial DTOs: `ApiResponse`, `SuccessResult`, `TriggerBuildsResult`, `ErrorViewModel`.

## Config / data files (schemas with paths)
- `demo-framework/src/main/resources/users.json` — `{ <env>: { <userType>: { username, password|encryptedPassword } } }`.
- `demo-framework/src/main/resources/config.json` — `connection_strings`, `settings` (`allowDriverReuse`, `baseNameSpace`, `progressHandlerName`), `urls` (flat name→URL; page objects resolve by `<name>_<env>`). Has a trailing comma (lenient parser only — see known-issues).
- `db/src/config.json` — `universal` (driver/protocol/port) + `connection_strings.<env>` (host_name/db_name).
- `db/src/queries.json` — flat `"<name>": "<SQL>"` map (Oracle/OMS SQL, some with `%s` placeholders).
- `db/src/users.json` — same users schema as above.
- `core/testUsers.json`, `web/testUsers.json` — identical: `{ "testEnvironment": {...}, "default": {...} }`.
- `core/config.json` — `selenium_hub.address` only.
