import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GeneratedTests {
    private static HttpClient client;
    private static final Map<String, String> DEFAULT_HEADERS = new LinkedHashMap<>();
    private static final String BASE_URL = "https://httpbin.org";

    @BeforeAll
    static void setupClient() {
        client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        DEFAULT_HEADERS.put("Content-Type", "application/json");
    }

    @Test
    void login() throws IOException, InterruptedException {
        String resolvedUrl = resolveUrl("/anything/api/login");
        Map<String, String> headers = new LinkedHashMap<>(DEFAULT_HEADERS);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(resolvedUrl));
        for (Map.Entry<String, String> h : headers.entrySet()) {
            requestBuilder.header(h.getKey(), h.getValue());
        }
        requestBuilder.method("POST", HttpRequest.BodyPublishers.ofString("{\"username\":\"san\",\"password\":\"1234\"}"));

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(998, response.statusCode());
        assertTrue(response.body().contains("/anything/api/login"));
    }

    @Test
    void get_user() throws IOException, InterruptedException {
        String resolvedUrl = resolveUrl("/anything/api/users/42");
        Map<String, String> headers = new LinkedHashMap<>(DEFAULT_HEADERS);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(resolvedUrl));
        for (Map.Entry<String, String> h : headers.entrySet()) {
            requestBuilder.header(h.getKey(), h.getValue());
        }
        requestBuilder.GET();

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().allValues("Content-Type").stream().anyMatch(v -> v.contains("application/json")));
    }

    @Test
    void update_user() throws IOException, InterruptedException {
        String resolvedUrl = resolveUrl("/anything/api/users/42");
        Map<String, String> headers = new LinkedHashMap<>(DEFAULT_HEADERS);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(resolvedUrl));
        for (Map.Entry<String, String> h : headers.entrySet()) {
            requestBuilder.header(h.getKey(), h.getValue());
        }
        requestBuilder.method("PUT", HttpRequest.BodyPublishers.ofString("{\"name\":\"Updated User\"}"));

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Updated"));
    }

    private static String resolveUrl(String path) {
        if (path.startsWith("/")) {
            return BASE_URL + path;
        }
        return path;
    }
}
