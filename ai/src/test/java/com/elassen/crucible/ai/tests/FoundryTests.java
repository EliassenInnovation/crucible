package com.elassen.crucible.ai.tests;

import com.eliassen.crucible.ai.imagetools.ImageTool;
import com.eliassen.crucible.ai.stepdefinitions.ImageSteps;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

@Ignore("relies on AZURE_FOUNDRY_API_KEY and AZURE_FOUNDRY_URL")
public class FoundryTests {
    String url = System.getenv("AZURE_FOUNDRY_URL");   ;
    String apiKey = System.getenv("AZURE_FOUNDRY_API_KEY");

    ChatModel metaModel = AzureOpenAiChatModel.builder()
            .apiKey(apiKey)
            .endpoint(url)
            .deploymentName("maverick")
            .build();

    @Test
    public void canTalkToMaverickModel(){
        ChatMessage chatMessage = UserMessage.from("Hello World");
        String response = metaModel.chat(chatMessage).aiMessage().text();
        System.out.println(response);
        assertTrue(response != null && !response.isEmpty());
    }

    @Test
    public void testingSendingMultipleImages() throws IOException {
        BufferedImage imageOne = ImageTool.loadImage("/home/barna/code/eliassen/crucible/ai/src/test/resources/image_03_23_2026_12_32_24.png");
        BufferedImage imageTwo = ImageTool.loadImage("/home/barna/code/eliassen/crucible/ai/src/test/resources/image_03_23_2026_12_33_15.png");

        String imageOneBase64 = ImageTool.convertImageToBase64(imageOne);
        String imageTwoBase64 = ImageTool.convertImageToBase64(imageTwo);

        ChatMessage chatMessage = UserMessage.from(
                TextContent.from(ImageSteps.COMPARE_SCREENSHOTS_PROMPT),
                ImageContent.from(imageOneBase64,"image/png"),
                ImageContent.from(imageTwoBase64,"image/png")
        );

        String response = metaModel.chat(chatMessage).aiMessage().text();
        System.out.println(response);
        assertTrue(response != null && !response.isEmpty());
    }

}
