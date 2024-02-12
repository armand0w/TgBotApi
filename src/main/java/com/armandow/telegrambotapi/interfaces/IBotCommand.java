package com.armandow.telegrambotapi.interfaces;

import org.json.JSONObject;

public interface IBotCommand {
    void execute(JSONObject user, JSONObject chat, String[] arguments);
}
