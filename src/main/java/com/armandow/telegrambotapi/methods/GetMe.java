package com.armandow.telegrambotapi.methods;

import com.armandow.telegrambotapi.http.RestClient;
import com.armandow.telegrambotapi.utils.TelegramApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * @see <a href="https://core.telegram.org/bots/api#getme">getme</a>
 */
@Slf4j
public class GetMe {
    public JSONObject getMe() throws Exception {
        return new RestClient(String.format("%s/getMe", TelegramApiUtils.getUrlBase()))
                .getJson()
                .getBody();
    }
}
