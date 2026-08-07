package com.eliassen.crucible.core.sharedobjects;

import com.eliassen.crucible.common.helpers.SystemHelper;
import com.eliassen.crucible.core.helpers.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Waits out the application's update cycle between Cucumber steps so element look-ups do not race a
 * still-rendering UI. This is invoked (via {@link MasterMind#checkProgress()}) at the top of every
 * element look-up and before every explicit wait.
 *
 * <p>The default behaviour is deterministic rather than a fixed sleep:
 * <ul>
 *   <li><b>Progress indicator (generic, default):</b> if {@link #PROGRESS_INDICATOR_XPATH} is set,
 *       the framework waits for that element to disappear (or, when
 *       {@link #PROGRESS_INDICATOR_FINISHED_STATE_XPATH} is set, for the finished-state element to
 *       appear). This works for any SPA that shows a loading/progress element.</li>
 *   <li><b>Angular stability (opt-in):</b> when the {@code settings.waitForAngular} config flag is
 *       {@code true}, the framework additionally waits for Angular to report that all change
 *       detection / rendering has settled via {@code window.getAllAngularTestabilities()}. On
 *       non-Angular pages this is a no-op.</li>
 * </ul>
 *
 * <p>Subclasses configure behaviour by setting the two xpath fields (typically in their
 * constructor); most consumers no longer need to override {@link #checkProgress()}. Settling is
 * best-effort: this method never throws, so it can never fail a run on its own.
 */
public abstract class ProgressHandler
{
    /**
     * XPath of the application's progress / spinner element. When set, {@link #checkProgress()}
     * waits for it to become invisible between steps. Empty (the default) disables the wait.
     */
    protected String PROGRESS_INDICATOR_XPATH = "";

    /**
     * Optional XPath whose visibility marks "finished". When set, {@link #checkProgress()} waits for
     * this element to become visible instead of waiting for {@link #PROGRESS_INDICATOR_XPATH} to
     * disappear.
     */
    protected String PROGRESS_INDICATOR_FINISHED_STATE_XPATH = "";

    /** Config setting (seconds) bounding how long settling may wait. Documented default is 15s. */
    public static final String PROGRESS_HANDLER_MAX_WAIT = "PROGRESS_HANDLER_MAX_WAIT";

    /** Config setting ({@code settings.waitForAngular}) enabling the opt-in Angular stability wait. */
    public static final String WAIT_FOR_ANGULAR = "waitForAngular";

    private static final double DEFAULT_MAX_WAIT_SECONDS = 15d;

    /**
     * Waits for the application to finish updating before the next look-up / action. Best-effort:
     * this method never throws, so settling can never fail a run.
     */
    public void checkProgress()
    {
        WebDriver driver = getDriver();
        if (driver == null) {
            return;
        }

        Timeouts timeouts = null;
        Duration previousImplicit = null;
        try {
            // The explicit waits below re-locate on every poll; the driver's implicit wait would
            // otherwise add its full timeout to each poll where the element is absent. Drop it to
            // zero for the duration of settling, then restore whatever was configured.
            timeouts = driver.manage().timeouts();
            previousImplicit = timeouts.getImplicitWaitTimeout();
            timeouts.implicitlyWait(Duration.ZERO);

            waitForProgressIndicator(driver);
            if (angularWaitEnabled()) {
                waitForAngular(driver);
            }
        } catch (Exception e) {
            Logger.logError("Progress settle wait did not complete (continuing anyway): " + e.getMessage());
        } finally {
            if (timeouts != null && previousImplicit != null) {
                try {
                    timeouts.implicitlyWait(previousImplicit);
                } catch (Exception ignored) {
                    // Restoring the implicit wait must not be able to fail the run.
                }
            }
        }
    }

    /**
     * @return the current thread's driver as a {@link WebDriver}, or {@code null} when no driver is
     *         set (e.g. non-UI scenarios).
     */
    protected WebDriver getDriver()
    {
        Object driver = MasterMind.getCurrentThreadObjects().get(MasterMind.DRIVER);
        return driver instanceof WebDriver ? (WebDriver) driver : null;
    }

    private void waitForProgressIndicator(WebDriver driver)
    {
        if (isBlank(PROGRESS_INDICATOR_XPATH)) {
            return;
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(getMaxWaitMillis()));
        if (!isBlank(PROGRESS_INDICATOR_FINISHED_STATE_XPATH)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(PROGRESS_INDICATOR_FINISHED_STATE_XPATH)));
        } else {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.xpath(PROGRESS_INDICATOR_XPATH)));
        }
    }

    private void waitForAngular(WebDriver driver)
    {
        if (!(driver instanceof JavascriptExecutor)) {
            return;
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Returns true once every Angular testability reports stable, or immediately on pages that
        // are not Angular (or where testability is unavailable, e.g. production mode).
        String script =
                "try {"
                        + "  if (!window.getAllAngularTestabilities) { return true; }"
                        + "  return window.getAllAngularTestabilities().every(function (t) { return t.isStable(); });"
                        + "} catch (e) { return true; }";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(getMaxWaitMillis()));
        wait.until(d -> Boolean.TRUE.equals(js.executeScript(script)));
    }

    protected boolean angularWaitEnabled()
    {
        return SystemHelper.getApplicationSettingBoolean(WAIT_FOR_ANGULAR);
    }

    protected long getMaxWaitMillis()
    {
        Double configured = SystemHelper.getConfigSettingDouble(PROGRESS_HANDLER_MAX_WAIT);
        double seconds = (configured != null && configured >= 0) ? configured : DEFAULT_MAX_WAIT_SECONDS;
        return Math.round(seconds * 1000);
    }

    private static boolean isBlank(String value)
    {
        return value == null || value.isEmpty();
    }
}
