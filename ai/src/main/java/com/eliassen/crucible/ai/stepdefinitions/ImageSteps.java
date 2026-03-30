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
import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.fail;

public class ImageSteps {
    public static final String DIFFERENCES_REPORT_BASE = "differences_report_base";
    public static final String DIFFERENCES_REPORT_COMPLETE = "differences_report_complete";

    @And("I load and remember the latest screenshot as {string}")
    public void iLoadAndRememberTheLatestAScreenshotAs(String screenshotName) throws IOException {
        String latestScreenshotPath = CurrentPage.retrieve(ScreenShotter.LATEST_SCREENSHOT_PATH);
        if (latestScreenshotPath == null || latestScreenshotPath.isEmpty()) {
            fail("There is no 'latest screenshot' to load");
        } else {
            BufferedImage image = ImageTool.loadImage(latestScreenshotPath);
            CurrentPage.store(screenshotName, ImageTool.convertImageToBase64(image));
        }
    }

    @And("I load and remember the {string} screenshot as {string}")
    public void iLoadAndRememberTheLatestAScreenshotAs(String screenshotPath, String screenshotName) throws IOException {
        String screenshotDirectory = "";
        if (!screenshotPath.contains("screenshots")) {
            screenshotDirectory = CurrentPage.getScreenShotter().getScreenshotDirectory();
        }

        BufferedImage image = ImageTool.loadImage(screenshotDirectory + screenshotPath);
        CurrentPage.store(screenshotName, ImageTool.convertImageToBase64(image));
    }

    @And("I compare image {string} to image {string}")
    public void iCompareImageToImage(String imageOneName, String imageTwoName) throws IOException {
        String imageOnePath = CurrentPage.retrieve(imageOneName);
        String imageTwoPath = CurrentPage.retrieve(imageTwoName);

        String imageOneBase64 = ImageTool.loadImageBase64(imageOnePath);
        CurrentPage.store(imageOneName, imageOneBase64);

        String imageTwoBase64 = ImageTool.loadImageBase64(imageTwoPath);
        CurrentPage.store(imageTwoName, imageTwoBase64);

        String response = AiHelper.compareScreenshots(imageOneBase64,
                imageTwoBase64,
                COMPARE_SCREENSHOTS_PROMPT);

        Logger.log(response);
        CurrentPage.store(DIFFERENCES_REPORT_BASE, response);

        StringBuilder reportBuilder = new StringBuilder(response);
        reportBuilder.append("  \n");
        reportBuilder.append("### Image one  \n");
        reportBuilder.append("<img src='data:image/png;base64,");
        reportBuilder.append(CurrentPage.retrieve(imageOneName));
        reportBuilder.append("' alt='image one'>\n");

        reportBuilder.append("  \n");
        reportBuilder.append("### Image two  \n");
        reportBuilder.append("<img src='data:image/png;base64,");
        reportBuilder.append(CurrentPage.retrieve(imageTwoName));
        reportBuilder.append("' alt='image two'>");
        reportBuilder.append("  \n");

        String directory = "." + File.separator + "output";
        FileHelper.ensureDirectoryExists(directory);
        String comparisonReportName = "differenceReport_" + ScreenShotterBase.getDateString(true);
        FileHelper.writeTextToDisk(reportBuilder.toString(), "." + File.separator + "output" + File.separator +
                comparisonReportName + ".md");
    }

    @And("I create the comparison HTML report for {string} and {string}")
    public void iCreateTheComparisonHTMLReportForAnd(String imageOneName, String imageTwoName) throws IOException {
        String report = CurrentPage.retrieve(DIFFERENCES_REPORT_BASE);
        ChatMessage chatMessage = UserMessage.from(
                TextContent.from(CONVERT_MARKDOWN_TO_HTML),
                TextContent.from(report)
        );
        String htmlReport = AiHelper.callLLM(chatMessage);

        String reportHTMLTemplate = new FileHelper().getTextFileContent("differences-report.html");
        reportHTMLTemplate = reportHTMLTemplate.replace("{{report}}",htmlReport);
        reportHTMLTemplate = reportHTMLTemplate.replace("{{imageOne}}",CurrentPage.retrieve(imageOneName));
        reportHTMLTemplate = reportHTMLTemplate.replace("{{imageTwo}}",CurrentPage.retrieve(imageTwoName));

        String directory = "." + File.separator + "output";
        FileHelper.ensureDirectoryExists(directory);
        String comparisonReportName = "differenceReport_" + ScreenShotterBase.getDateString(true);
        FileHelper.writeTextToDisk(reportHTMLTemplate, "." + File.separator + "output" + File.separator +
                comparisonReportName + ".html");
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
            Include a header (##) for each numbered section above
            Only use UTF-8 
            """;

    public static final String CONVERT_MARKDOWN_TO_HTML = """
            I have attached the contents of a markdown file.
            Your job is to convert it to HTML.
            
            Preserve headings and characteristics like bold or italics
            
            Do not add any CSS classes or styling.
            Only use UTF-8 
            """;
}
