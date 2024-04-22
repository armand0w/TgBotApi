package com.armandow.telegrambotapi.methods;

import com.armandow.telegrambotapi.TelegramBot;
import com.armandow.telegrambotapi.command.HelpCommand;
import com.armandow.telegrambotapi.command.StartCommand;
import com.armandow.telegrambotapi.enums.ParseMode;
import com.armandow.telegrambotapi.exceptions.TelegramApiValidationException;
import com.armandow.telegrambotapi.utils.Emoji;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SendMessageTest {
    private static Long userId = null;

    @BeforeAll
    static void beforeAll() {
        var bot = new TelegramBot(System.getenv("BOT_TOKEN"));
        bot.registerCommand(new StartCommand());
        bot.registerCommand(new HelpCommand());
        bot.run();

        TelegramApiUtils.getBotCommandMap().forEach((k, c) -> {
            assertNotNull(k);

            assertNotNull(c);
            assertNotNull(c.getName());
            assertNotNull(c.getDescription());
            assertNotNull(c.getBotCommandInstance());
            assertNotNull(c.toString());
        });

        assertNotNull(bot);

        userId = Long.parseLong(System.getenv("USER_ID"));
        assertNotNull(userId);
    }

    @Test
    void testSendMessageMarkdown() throws Exception {
        var resp = new JSONObject();
        var msg = new SendMessage();
        msg.setChatId(userId);
        msg.setParseMode(ParseMode.MARKDOWN);
        msg.setDisableNotification(true);
        msg.setDisableWebPagePreview(true);
        msg.setProtectContent(true);

        msg.setText("""
                == MARKDOWN ==
                *bold text*
                _italic text_
                [inline URL](https://github.com/armand0w/TgBotApi)
                [inline mention of a user](tg://user?id=5462882796)
                `inline fixed-width code`
                ```
                pre-formatted fixed-width code block
                ```
                ```python
                pre-formatted fixed-width code block written in the Python programming language
                ```
                """);
        resp = msg.send();
        assertNotNull(resp);
        assertTrue(resp.getBoolean("ok"));
    }

    @Test
    void testSendMessageMarkdownV2() throws Exception {
        var resp = new JSONObject();
        var msg = new SendMessage();
        msg.setChatId(userId);
        msg.setParseMode(ParseMode.MARKDOWNV2);
        msg.setDisableNotification(true);
        msg.setDisableWebPagePreview(true);
        msg.setProtectContent(true);

        msg.setText("""
                \\=\\= MARKDOWNV2 \\=\\=
                *bold \\*text*
                _italic \\*text_
                __underline__
                ~strikethrough~
                ||spoiler||
                *bold _italic bold ~italic bold strikethrough ||italic bold strikethrough spoiler||~ __underline italic bold___ bold*
                [inline URL](https://www.example.com/)
                [inline mention of a user](tg://user?id=5462882796)
                ![\uD83D\uDC4D](tg://emoji?id=5368324170671202286)
                `inline fixed-width code`
                ```
                pre-formatted fixed-width code block
                ```
                ```python
                pre-formatted fixed-width code block written in the Python programming language
                ```
                >Block quotation started
                >Block quotation continued
                >The last line of the block quotation
                """+TelegramApiUtils.scapeTelegramString("Linea. 'con' \"caracteres\" especiales-_ para: escapar string @#$%^&*()_+{}[]"));
        resp = msg.send();
        assertNotNull(resp);
        assertTrue(resp.getBoolean("ok"));

        msg.setText(" "
                + "*Titulo Negritas* " + Emoji.NO_MOBILE_PHONES + " " + Emoji.GREEN_COLOR + " " + Emoji.RED_COLOR + " \n\n"
                + "Texto de la nota bla bla bla bla\n\n"
                + "[Leer mas](" +TelegramApiUtils.scapeUrl("https://github.com/armand0w/TgBotApi")+ ")");
        resp = msg.send();
        assertNotNull(resp);
        assertTrue(resp.getBoolean("ok"));
    }

    @Test
    void testSendMessageHTML() throws Exception {
        var resp = new JSONObject();
        var msg = new SendMessage();
        msg.setChatId(userId);
        msg.setParseMode(ParseMode.HTML);
        msg.setDisableNotification(true);
        msg.setDisableWebPagePreview(true);
        msg.setProtectContent(true);

        msg.setText("""
                == HTML ==
                <b>bold</b>, <strong>bold</strong>
                <i>italic</i>, <em>italic</em>
                <u>underline</u>, <ins>underline</ins>
                <s>strikethrough</s>, <strike>strikethrough</strike>, <del>strikethrough</del>
                <span class="tg-spoiler">spoiler</span>, <tg-spoiler>spoiler</tg-spoiler>
                <b>bold <i>italic bold <s>italic bold strikethrough <span class="tg-spoiler">italic bold strikethrough spoiler</span></s> <u>underline italic bold</u></i> bold</b>
                <a href="https://github.com/armand0w/TgBotApi">inline URL</a>
                <a href="tg://user?id=5462882796">inline mention of a user</a>
                <tg-emoji emoji-id="5368324170671202286">👍</tg-emoji>
                <code>inline fixed-width code</code>
                <pre>pre-formatted fixed-width code block</pre>
                <pre><code class="language-python">pre-formatted fixed-width code block written in the Python programming language</code></pre>
                <blockquote>Block quotation started\\nBlock quotation continued\\nThe last line of the block quotation</blockquote>
                """);
        resp = msg.send();
        assertNotNull(resp);
        assertTrue(resp.getBoolean("ok"));
    }

    @Test
    void testThrowSendMessage() {
        var exception = assertThrows(TelegramApiValidationException.class, () -> new SendMessage().send());
        assertNotNull(exception);

        var exception2 = assertThrows(TelegramApiValidationException.class, () -> {
            var msg = new SendMessage();
            msg.setChatId(1L);
            msg.send();
        });
        assertNotNull(exception2);
    }

}
