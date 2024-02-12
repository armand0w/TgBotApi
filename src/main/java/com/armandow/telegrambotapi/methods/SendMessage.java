package com.armandow.telegrambotapi.methods;

import com.armandow.telegrambotapi.enums.ParseMode;
import com.armandow.telegrambotapi.exceptions.TelegramApiValidationException;
import com.armandow.telegrambotapi.http.RestClient;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * @see <a href="https://core.telegram.org/bots/api#sendmessage">sendmessage</a>
 */
@Getter
@Setter
@Slf4j
public class SendMessage {
    private Long chatId;
    private String text;
    private ParseMode parseMode;
    private Integer replyToMessageId;
    private Boolean disableWebPagePreview;
    private Boolean disableNotification;
    private Boolean protectContent;

    private JSONObject buildPayload() throws TelegramApiValidationException {
        var payload = new JSONObject();

        if ( chatId == null ) {
            throw new TelegramApiValidationException("ChatId parameter can't be empty");
        }

        if ( text == null || text.isBlank() ) {
            throw new TelegramApiValidationException("Text parameter can't be empty");
        }

        try {
            payload.put("chat_id", this.chatId);
            payload.put("text", this.text);

            if ( parseMode != null ) {
                payload.put("parse_mode", this.parseMode.toString());
            }

            if ( replyToMessageId != null ) {
                payload.put("reply_to_message_id", replyToMessageId);
            }

            if ( disableWebPagePreview != null && disableWebPagePreview ) {
                payload.put("disable_web_page_preview", true);
            }

            if ( disableNotification != null && disableNotification) {
                payload.put("disable_notification", true);
            }

            if ( protectContent != null && protectContent) {
                payload.put("protect_content", true);
            }
        } catch (Exception e) {
            log.error("buildPayload", e);
            throw new TelegramApiValidationException("TelegramApiValidationException " + e.getMessage());
        }

        return payload;
    }

    public JSONObject send() throws Exception {
        return new RestClient(TelegramApiUtils.getUrlBase() + "/sendMessage")
                .postJson(buildPayload())
                .getBody();
    }
}
