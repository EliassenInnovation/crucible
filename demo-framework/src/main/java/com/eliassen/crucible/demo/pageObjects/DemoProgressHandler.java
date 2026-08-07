package com.eliassen.crucible.demo.pageObjects;

import com.eliassen.crucible.core.sharedobjects.ProgressHandler;

/**
 * Reference progress handler. Instead of sleeping a fixed amount between steps (which races the
 * app's update cycle and causes flaky failures), configure the framework to wait deterministically:
 * point {@link #PROGRESS_INDICATOR_XPATH} at the application's loading/spinner element so the
 * framework waits for it to disappear, and/or set {@code settings.waitForAngular=true} in config to
 * additionally wait for Angular change detection to settle. Both are handled by
 * {@link ProgressHandler#checkProgress()}.
 */
public class DemoProgressHandler extends ProgressHandler
{
    public DemoProgressHandler()
    {
        // Example: wait for the app's global spinner to clear between steps.
        // PROGRESS_INDICATOR_XPATH = "//*[@id='loading-spinner']";
    }
}
