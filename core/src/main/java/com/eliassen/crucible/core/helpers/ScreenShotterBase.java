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

public abstract class ScreenShotterBase
{
	private static String screenshotDirectory = "." + File.separator + "screenshots" + File.separator;
	
    public String getScreenshotDirectory()
    {
        return screenshotDirectory;
    }
    
    public void setScreenShotDirectory(String value)
    {
    	screenshotDirectory = value;
    }

    public abstract byte[] takeScreenShot();

    public void safeAttachScreenshot(Scenario scenario){
        safeAttachScreenshot(scenario, "screenshot");
    }

    public void safeAttachScreenshot(Scenario scenario, String screenshotName)
    {
        try
        {
            byte[] screenshotData = takeScreenShot();
            scenario.attach(screenshotData, "image/png", screenshotName);
        }
        catch (Exception e)
        {
            Logger.log(e.getMessage());
            BufferedImage image = null;

            try
            {
                image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            }
            catch (AWTException ex)
            {
                ex.printStackTrace();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try
            {
                ImageIO.write(image, "png", outputStream);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }

            byte[] screenshotData = outputStream.toByteArray();
            scenario.log(e.getMessage());
            scenario.attach(screenshotData, "image/png","screenshot");
        }
    }
    
    public static String getDateString(boolean full)
    {
    	String pattern;
    	
    	if(full)
    	{
    		pattern = "MM_dd_yyyy_hh_mm_ss";
    	}
    	else
    	{
    		pattern = "MM_dd_yyyy";
    	}
    	
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        
        return simpleDateFormat.format(Calendar.getInstance().getTime());
    }

    public String ensureTodaysScreenshotDirectory()
    {
        String date = getDateString(false);
        String directory = getScreenshotDirectory() + date + File.separator;
        File dir = new File(directory);
        dir.mkdir();

        return directory;
    }
}
