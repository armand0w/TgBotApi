package com.armandow.telegrambotapi.enums;

public enum ParseMode {
    MARKDOWN("Markdown"),
    MARKDOWNV2("MarkdownV2"),
    HTML("html");

    private final String name;

    ParseMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
