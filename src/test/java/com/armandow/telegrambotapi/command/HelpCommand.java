package com.armandow.telegrambotapi.command;

import com.armandow.telegrambotapi.annotations.BotCommand;
import com.armandow.telegrambotapi.enums.ParseMode;
import com.armandow.telegrambotapi.interfaces.IBotCommand;
import com.armandow.telegrambotapi.methods.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Arrays;

@Slf4j
@BotCommand(value = "/helps", description = "Solo para probar que esta vivo")
public class HelpCommand implements IBotCommand {
    @Override
    public void execute(JSONObject user, JSONObject chat, String[] arguments) {
        try {
            log.trace("user: {}", user.toString(2));
            log.trace("chat: {}", chat.toString(2));
            log.trace("arguments: {}", Arrays.toString(arguments));
            var sendMessage = new SendMessage();
            sendMessage.setText("Respuesta desde: *HelpCommand*");
            sendMessage.setChatId(chat.getLong("id"));
            sendMessage.setParseMode(ParseMode.MARKDOWNV2);

            var response = sendMessage.send();
            log.trace(response.toString(2));
        } catch (Exception e) {
            log.error("HelpCommand", e);
        }
    }
}
