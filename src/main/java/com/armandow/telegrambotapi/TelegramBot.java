package com.armandow.telegrambotapi;

import com.armandow.telegrambotapi.annotations.BotCommand;
import com.armandow.telegrambotapi.interfaces.IBotCommand;
import com.armandow.telegrambotapi.model.Command;
import com.armandow.telegrambotapi.process.LongPollingBot;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.HashMap;

@Slf4j
public class TelegramBot {

    public TelegramBot(String token) {
        TelegramApiUtils.setUrlBase("https://api.telegram.org/bot" + token);
        TelegramApiUtils.setBotCommandMap(new HashMap<>());
    }

    public void registerCommand(IBotCommand commandClass) {
        try {
            var annotations = Class.forName(commandClass.getClass().getName()).getAnnotations();

            for ( Annotation annotation: annotations ) {
                if ( annotation instanceof BotCommand bc) {
                    TelegramApiUtils.getBotCommandMap().put(bc.value(), new Command(bc.value(), bc.description(), commandClass));
                }
            }
        } catch (Exception e) {
            log.debug("registerCommand", e);
        }
    }

    public void run() {
        try {
            var longPolling = new LongPollingBot(this);
            longPolling.start();
        } catch (Exception e) {
            log.debug("run()", e);
        }
    }
}
