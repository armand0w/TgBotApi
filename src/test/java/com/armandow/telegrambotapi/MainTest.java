package com.armandow.telegrambotapi;

import com.armandow.telegrambotapi.command.HelpCommand;
import com.armandow.telegrambotapi.command.StartCommand;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainTest {
    public static void main(String[] args) {
        var bot = new TelegramBot(System.getenv("BOT_TOKEN"));
        bot.registerCommand(new StartCommand());
        bot.registerCommand(new HelpCommand());
        bot.run();

        assertNotNull(bot);
    }

    @BeforeAll
    static void beforeAll() {
        var bot = new TelegramBot(System.getenv("BOT_TOKEN"));
        bot.registerCommand(new StartCommand());
        bot.registerCommand(new HelpCommand());
        bot.run();

        assertNotNull(bot);
    }

    @Test
    void name() {
        assertNotNull(TelegramApiUtils.getUrlBase());
        assertNotNull(TelegramApiUtils.getBotCommandMap());
    }
}
