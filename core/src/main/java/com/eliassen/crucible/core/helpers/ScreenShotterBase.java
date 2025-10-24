package com.eliassen.crucible.core.helpers;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import io.cucumber.java.Scenario;

import javax.imageio.ImageIO;

/**
 * Abstract base class for screenshot functionality.
 */
public abstract class ScreenShotterBase {
    /**
     * Default directory for storing screenshots.
     */
    private static String screenshotDirectory = "." + File.separator + "screenshots" + File.separator;

    /**
     * Gets the current screenshot directory.
     * @return The screenshot directory.
     */
    public String getScreenshotDirectory() {
        return screenshotDirectory;
    }

    /**
     * Sets the screenshot directory.
     * @param value The new screenshot directory.
     */
    public void setScreenShotDirectory(String value) {
        screenshotDirectory = value;
    }

    /**
     * Takes a screenshot and returns it as a byte array.
     * @return The screenshot data.
     */
    public abstract byte[] takeScreenShot();

    /**
     * Attaches a screenshot to the given scenario.
     * @param scenario The scenario to attach the screenshot to.
     */
    public void safeAttachScreenshot(Scenario scenario) {
        safeAttachScreenshot(scenario, "screenshot");
    }

    /**
     * Attaches a screenshot to the given scenario with a specified name.
     * @param scenario The scenario to attach the screenshot to.
     * @param screenshotName The name of the screenshot.
     */
    public void safeAttachScreenshot(Scenario scenario, String screenshotName) {
        try {
            byte[] screenshotData = takeScreenShot();
            scenario.attach(screenshotData, "image/png", screenshotName);
        } catch (Exception e) {
            Logger.log(e.getMessage());
            BufferedImage image = null;

            try {
                image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            } catch (AWTException ex) {
                ex.printStackTrace();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                ImageIO.write(image, "png", outputStream);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            byte[] screenshotData = outputStream.toByteArray();
            scenario.log(e.getMessage());
            scenario.attach(screenshotData, "image/png", "screenshot");
        }
    }

    /**
     * Gets a date string in the specified format.
     * @param full Whether to include the time in the date string.
     * @return The date string.
     */
    public static String getDateString(boolean full) {
        String pattern;

        if (full) {
            pattern = "MM_dd_yyyy_hh_mm_ss";
        } else {
            pattern = "MM_dd_yyyy";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Ensures that the screenshot directory for the current day exists.
     * @return The path to the screenshot directory.
     */
    public String ensureTodaysScreenshotDirectory() {
        String date = getDateString(false);
        String directory = getScreenshotDirectory() + date + File.separator;
        File dir = new File(directory);
        dir.mkdir();

        return directory;
    }
}
