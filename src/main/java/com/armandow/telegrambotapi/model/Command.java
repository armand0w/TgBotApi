package com.armandow.telegrambotapi.model;

import com.armandow.telegrambotapi.interfaces.IBotCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Command {
    private IBotCommand botCommandInstance;
    private String name;
    private String description;
}
