package com.eliassen.crucible.ai.stepdefinitions;

import com.eliassen.crucible.ai.helpers.AiHelper;
import com.eliassen.crucible.ai.imagetools.ImageTool;
import com.eliassen.crucible.common.helpers.FileHelper;
import com.eliassen.crucible.core.helpers.Logger;
import com.eliassen.crucible.core.helpers.ScreenShotterBase;
import com.eliassen.crucible.web.helpers.ScreenShotter;
import com.eliassen.crucible.web.sharedobjects.CurrentPage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import io.cucumber.java.en.And;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class ImageSteps {
    public static final String DIFFERENCES_REPORT = "differences_report";

    @And("I load and remember the latest screenshot as {string}")
    public void iLoadAndRememberTheLatestAScreenshotAs(String screenshotName) throws IOException {
        String latestScreenshotPath = CurrentPage.retrieve(ScreenShotter.LATEST_SCREENSHOT_PATH);
        if(latestScreenshotPath == null || latestScreenshotPath.isEmpty()){
            fail("There is no 'latest screenshot' to load");
        } else {
            BufferedImage image = ImageTool.loadImage(latestScreenshotPath);
            CurrentPage.store(screenshotName,ImageTool.convertImageToBase64(image));
        }
    }

    @And("I load and remember the {string} screenshot as {string}")
    public void iLoadAndRememberTheLatestAScreenshotAs(String screenshotPath, String screenshotName) throws IOException {
        String screenshotDirectory = "";
        if(!screenshotPath.contains("screenshots")){
            screenshotDirectory = CurrentPage.getScreenShotter().getScreenshotDirectory();
        }

        BufferedImage image = ImageTool.loadImage(screenshotDirectory + screenshotPath);
            CurrentPage.store(screenshotName,ImageTool.convertImageToBase64(image));
    }

    @And("I compare image {string} to image {string}")
    public void iCompareImageToImage(String imageOneName, String imageTwoName) throws IOException {
        String imageOnePath = CurrentPage.retrieve(imageOneName);
        String imageTwoPath = CurrentPage.retrieve(imageTwoName);

        String imageOneBase64 = ImageTool.loadImageBase64(imageOnePath);
        String imageTwoBase64 = ImageTool.loadImageBase64(imageTwoPath);

        String response = AiHelper.compareScreenshots(imageOneBase64,
                imageTwoBase64,
                COMPARE_SCREENSHOTS_PROMPT);

        Logger.log(response);
        CurrentPage.store(DIFFERENCES_REPORT,response);

        String directory = "." + File.separator + "output";
        FileHelper.ensureDirectoryExists(directory);
        FileHelper.writeTextToDisk(response,"." + File.separator + "output" + File.separator +
                "differenceReport_" + ScreenShotterBase.getDateString(true) + ".md");
    }

    public static final String COMPARE_SCREENSHOTS_PROMPT = """
            I have two screen captures of what appears to be the same interface. I need you to analyze them in detail and list all differences you can detect. Please provide a comprehensive, itemized list of differences including but not limited to:
            
            1. Color differences (hex codes if possible)
            2. Position changes (relative or absolute)
            3. Text differences (content, font properties)
            4. Element size changes
            5. Missing or added elements
            6. Alignment changes
            7. Spacing differences
            8. Visual hierarchy changes
            9. Any other visual discrepancies
            
            Please be extremely detailed and specific. Do not summarize - list each difference as a separate item. If there are no differences in a particular category, you may state that category shows no differences.
            
            Please analyze these images and provide your detailed comparison.
            Return valid Markdown
            """;
}
