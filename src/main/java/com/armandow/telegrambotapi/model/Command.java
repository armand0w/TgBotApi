package com.armandow.telegrambotapi.model;

import com.armandow.telegrambotapi.interfaces.IBotCommand;

public record Command(String name, String description, IBotCommand botCommandInstance) {}
