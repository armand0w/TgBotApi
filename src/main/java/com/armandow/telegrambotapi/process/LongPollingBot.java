package com.armandow.telegrambotapi.process;

import com.armandow.telegrambotapi.exceptions.TooManyRequestExceptions;
import com.armandow.telegrambotapi.http.RestClient;
import com.armandow.telegrambotapi.methods.SendMessage;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @see <a href="https://core.telegram.org/bots/api#getting-updates">getting-updates</a>
 */
@Slf4j
public class LongPollingBot extends Thread {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Object lock;

    public LongPollingBot(Object lock) {
        this.lock = lock;
    }

    @Override
    public synchronized void start() {
        log.trace("LongPollingBot.start()");
        this.running.set(true);
        super.start();
    }

    @Override
    @SneakyThrows
    public void run() {
        log.trace("LongPollingBot.run()");
        setPriority(Thread.MIN_PRIORITY);
        while ( this.running.get() ) {
            synchronized (lock) {
                try {
                    var offset = new JSONObject();
                    if (TelegramApiUtils.getLastUpdateId() != null) {
                        offset.put("offset", TelegramApiUtils.getLastUpdateId());
                    }

                    log.debug("/getUpdates: {}", TelegramApiUtils.getLastUpdateId());
                    var response = new RestClient(TelegramApiUtils.getUrlBase() + "/getUpdates").postJson(offset).getBody();
                    var updates = response.optJSONArray("result");

                    if ( updates == null || updates.isEmpty() ) {
                        TelegramApiUtils.setLastUpdateId(null);
                        lock.wait(2499);
                    } else {
                        log.trace(updates.toString(4));
                        iterateResults(updates);
                    }
                } catch (InterruptedException ie) {
                    log.warn("Interrupted!!! {}", ie.getMessage());
                    Thread.currentThread().interrupt();
                } catch (TooManyRequestExceptions too) {
                    log.error("Too many Request, wait...");
                    lock.wait(10000);
                } catch (Exception e) {
                    lock.wait(30000);
                    if ( e.getMessage() != null ) {
                        log.error("Exception: {}", e.getMessage());
                    }
                } finally {
                    if ( TelegramApiUtils.getLastUpdateId() != null ) {
                        TelegramApiUtils.setLastUpdateId(TelegramApiUtils.getLastUpdateId() + 1);
                    }
                }
            }
        }
        log.warn("LongPollingBot thread has being closed");
    }

    @Override
    public void interrupt() {
        log.trace("LongPollingBot.interrupt()");
        this.running.set(false);
        super.interrupt();
    }

    private void iterateResults(JSONArray updates) {
        var size = updates.length();

        for ( var i = 0; i < size; i++ ) {
            var update = updates.getJSONObject(i);
            try {
                TelegramApiUtils.setLastUpdateId(update.getLong("update_id"));
                var message = update.optJSONObject("message");

                if ( message != null ) {
                    if ( message.has("entities") ) {
                        var entityValue = update.optQuery("/message/entities/0/type").toString();
                        if ( entityValue != null && entityValue.equals("bot_command") ) {
                            processBotCommand(update);
                        }
                    } else {
                        processText(message);
                    }
                }
            } catch (Exception e1) {
                log.error("Error get updates {}", e1.getMessage());
                log.error("JSON {}", update);
            }
        }
    }

    private void processBotCommand(JSONObject update) {
        var text = update.optQuery("/message/text").toString();
        log.trace("processBotCommand: {}", text);
        var command = text.split(" ");

        if ( command[0].equals("/help") && !TelegramApiUtils.getBotCommandMap().containsKey("/help") ) {
            sendHelp(update);
        } else if ( TelegramApiUtils.getBotCommandMap().containsKey(command[0]) ) {
            TelegramApiUtils.getBotCommandMap().get(command[0]).botCommandInstance().execute(
                    new JSONObject(update.optQuery("/message/from").toString()),
                    new JSONObject(update.optQuery("/message/chat").toString()),
                    command.length > 1 ? Arrays.copyOfRange(command, 1, command.length) : null);
        } else {
            sendHelp(update, true);
        }
    }

    private void sendHelp(JSONObject update) {
        sendHelp(update, false);
    }

    private void sendHelp(JSONObject update, boolean extraHelp) {
        try {
            log.trace("sendHelp: {}", update.toString(2));
            var strBuilder = new StringBuilder();
            if ( extraHelp ) {
                strBuilder.append("Command not found\n\n");
            }

            strBuilder.append("Command list:\n\n/help\t\tList commands\n");

            TelegramApiUtils.getBotCommandMap()
                    .forEach((k, c) -> strBuilder.append(k)
                            .append("\t\t")
                            .append(c.description())
                            .append("\n"));

            var msg = new SendMessage();
            msg.setText(strBuilder.toString());
            msg.setChatId(Long.valueOf(update.optQuery("/message/chat/id").toString()));
            msg.send();
        } catch (Exception e) {
            log.error("sendHelp: {}", e.getMessage());
        }
    }

    private void processText(JSONObject message) {
        log.trace("processText: {}", message.toString(2));
        sendHelp(new JSONObject().put("message", message));
    }
}
