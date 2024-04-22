package com.armandow.telegrambotapi.utils;

import com.armandow.telegrambotapi.model.Command;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Map;

public class TelegramApiUtils {

    private TelegramApiUtils() {
        throw new IllegalStateException("Utility Class");
    }

    private static Long lastUpdateId = null;
    @Getter @Setter private static String urlBase;
    @Getter @Setter private static Map<String, Command> botCommandMap;

    public static String scapeTelegramString(String text) {
        return text
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("-", "\\-")
                .replace("_", "\\_")
                .replace(".", "\\.")
                .replace(",", "\\,")
                .replace(":", "\\:")
                .replace("+", "\\+")
                .replace("*", "\\*")
                .replace("!", "\\!")
                .replace("|", "\\|")
                .replace("#", "\\#")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("[", "\\[")
                .replace("]", "\\]")
                ;
    }

    public static String scapeUrl(String url) {
        return url
                .replace("-", "\\-")
                .replace("_", "\\_")
                .replace(".", "\\.");
    }

    public static synchronized Long getLastUpdateId() {
        return lastUpdateId;
    }

    public static synchronized void setLastUpdateId(Long lastUpdateId) {
        TelegramApiUtils.lastUpdateId = lastUpdateId;
    }

    public static int getInitialStart(int refresh) {
        var cs = Calendar.getInstance().get(Calendar.SECOND);

        if ( refresh < 60 ) {
            return ( 60 - cs );
        }

        refresh = ( refresh / 60 );

        if ( refresh < 60 ) {
            var cm = Calendar.getInstance().get(Calendar.MINUTE);
            return ((( (((cm / refresh) + 1 )) * refresh ) - cm) * 60) - cs;
        }

        return 0;
    }
}
