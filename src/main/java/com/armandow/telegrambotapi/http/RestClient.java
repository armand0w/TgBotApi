package com.armandow.telegrambotapi.http;

import com.armandow.telegrambotapi.exceptions.TooManyRequestExceptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Slf4j
public class RestClient {
    private final HttpClient httpClient;

    private final String url;
    @Getter private int statusCode;
    @Getter private JSONObject body;

    public RestClient(String url) {
        this.url = url;
        this.httpClient = createHttpClient();
    }

    public RestClient postJson(JSONObject data) throws Exception {
        log.trace("URL: {}", this.url);
        var request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(25))
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .uri(URI.create(url))
                .setHeader("User-Agent", "TgBotApi v0.0.5")
                .header("Content-Type", "application/json")
                .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        this.statusCode = response.statusCode();
        this.body = new JSONObject(response.body());

        if ( this.statusCode != 200 ) {
            log.warn(this.body.toString(2));
        }

        var headers = response.headers();
        headers.map().forEach((k, v) -> log.trace(k + ":" + v));
        log.trace("--------------------------------------------------------------------------------------------------");

        if ( this.statusCode == 429 ) {
            throw new TooManyRequestExceptions("Too Many Requests", this.body);
        }

        return this;
    }

    private HttpClient createHttpClient() {
        HttpClient client;

        try {
            var context = SSLContext.getInstance("TLSv1.3");
            context.init(null, null, null);

            client = HttpClient.newBuilder()
                    .sslContext(context)
                    .connectTimeout(Duration.ofSeconds(75))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }

        return client;
    }
}
