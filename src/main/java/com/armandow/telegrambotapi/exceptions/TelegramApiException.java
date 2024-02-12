package com.armandow.telegrambotapi.exceptions;

public class TelegramApiException extends Exception {
    public TelegramApiException() {
        super();
    }

    public TelegramApiException(String message) {
        super(message);
    }
}
