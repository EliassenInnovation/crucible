package com.eliassen.crucible.web.helpers;

import com.eliassen.crucible.core.helpers.ScreenShotterBase;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.IOException;

public class ScreenShotter extends ScreenShotterBase
{
    public byte[] takeScreenShot()
    {
    	TakesScreenshot screenShot = ((TakesScreenshot) CurrentPage.getDriver().getInstance());
        String todaysDirectory = ensureTodaysScreenshotDirectory();

        String screenShotName = todaysDirectory + "image_" + getDateString(true) + ".png";
        
        File screenShotFile = screenShot.getScreenshotAs(OutputType.FILE);
        byte[] screenshotData = screenShot.getScreenshotAs(OutputType.BYTES);

        File destination = new File(screenShotName);
        int version = 0;
        while(destination.exists()){
            screenShotName =
                    todaysDirectory + "image_" + getDateString(true) + "_" + version++ + ".png";
            destination = new File(screenShotName);
        }
        try 
        {
			FileUtils.copyFile(screenShotFile,destination);
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}

        CurrentPage.store(LATEST_SCREENSHOT_PATH,screenShotName);
        
        System.out.println("Screenshot name: " + screenShotName);
        return screenshotData;
    }
}
