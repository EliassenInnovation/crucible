package com.eliassen.crucible.ai.helpers;

import com.eliassen.crucible.common.helpers.SystemHelper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.chat.ChatModel;

import java.util.Locale;

import static org.junit.Assert.fail;

public class AiHelper {
    public static final String CHAT_PROVIDER_SETTING = "chatProvider";
    public static final String CHAT_MODEL_ID_SETTING = "chatModelId";

    public static ChatModel getChatModel(){
        String chatProvider = SystemHelper.getApplicationSetting(CHAT_PROVIDER_SETTING);
        String chatModelId = SystemHelper.getApplicationSetting(CHAT_MODEL_ID_SETTING);
        switch (chatProvider.toLowerCase(Locale.ROOT)){
            case "bedrock":
                return BedrockChatModel.builder().modelId(chatModelId).build();
            default:
                fail("chat provider: " + chatProvider + " | chatModelId: " + chatModelId);
        }
        return null;
    }

    public static String compareScreenshots(String imageOneBase64, String imageTwoBase64, String prompt){
        ChatMessage chatMessage = UserMessage.from(
                TextContent.from(prompt),
                ImageContent.from(imageOneBase64,"image/png"),
                ImageContent.from(imageTwoBase64,"image/png")
        );

        return AiHelper.getChatModel().chat(chatMessage).aiMessage().text();
    }
}
