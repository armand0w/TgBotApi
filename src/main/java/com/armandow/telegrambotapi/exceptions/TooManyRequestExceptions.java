package com.armandow.telegrambotapi.exceptions;

import lombok.Getter;
import org.json.JSONObject;

@Getter
public class TooManyRequestExceptions extends TelegramApiException {
    private final transient JSONObject data;

    public TooManyRequestExceptions(String message, JSONObject data) {
        super(message);
        this.data = data;
    }
}
