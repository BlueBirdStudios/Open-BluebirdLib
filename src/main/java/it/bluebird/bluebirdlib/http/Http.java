package it.bluebird.bluebirdlib.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.bluebird.bluebirdlib.BluebirdLib;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Http {
    private static final String BASE_URL = "https://api-lsbb.lighsync.org/api/v1/players";
    // private static final String BASE_URL = "http://127.0.0.1:8080/api/v1/players";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final Gson GSON = new Gson();

    public static CompletableFuture<Boolean> checkWhitelist(String uuid) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + uuid + "/check"))
                .GET().build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        AllowedResponse data = GSON.fromJson(response.body(), AllowedResponse.class);
                        return data != null && data.allowed;
                    }
                    return false;
                }).exceptionally(ex -> {
                    BluebirdLib.LOGGER.error("[HTTP]: Error with whitelist checking: " + ex.getMessage());
                    return true;
                });
    }

    public static CompletableFuture<List<String>> getPlayerIcons(String uuid) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + uuid + "/cosmetics"))
                .GET().build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        List<String> icons = GSON.fromJson(response.body(), new TypeToken<List<String>>(){}.getType());
                        return icons != null ? icons : List.<String>of();
                    }
                    return List.<String>of();
                }).exceptionally(ex -> {
                    BluebirdLib.LOGGER.error("[HTTP]: Failed to load icons for: " + uuid + " | code: " + ex);
                    return List.<String>of();
                });
    }

    private static class AllowedResponse {
        boolean allowed;
    }
}