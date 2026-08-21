package id.my.mdn.kupu.app.web.util.ytdlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HlsExtractor {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String accessToken;

    public HlsExtractor(String accessToken) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.accessToken = accessToken;
    }

    public Optional<String> extract(String videoUrl) {
        try {
            // 1. Fetch initial page to get API Key
            String html = fetchPage(videoUrl, null);
            Optional<String> apiKeyOpt = extractApiKey(html);

            Optional<String> result = Optional.empty();

            if (apiKeyOpt.isPresent()) {
                String videoId = extractVideoId(videoUrl);
                System.out.println("Extracted API Key. Fetching from Innertube API for Video ID: " + videoId);
                result = fetchHlsFromApi(videoId, apiKeyOpt.get());
                if (result.isPresent())
                    return result;
            } else {
                System.out.println("Could not find INNERTUBE_API_KEY in HTML.");
            }

            // Fallback (HTML parsing)
            return parseHlsUrl(html);

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String extractVideoId(String url) {
        Pattern p = Pattern.compile("v=([^&#]*)");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        p = Pattern.compile("youtu\\.be/([^&#]*)");
        m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        }
        return url;
    }

    private Optional<String> extractApiKey(String html) {
        String keyPattern = "\"INNERTUBE_API_KEY\":\"([^\"]+)\"";
        Pattern p = Pattern.compile(keyPattern);
        Matcher m = p.matcher(html);
        if (m.find()) {
            return Optional.of(m.group(1));
        }
        return Optional.empty();
    }

    private Optional<String> fetchHlsFromApi(String videoId, String apiKey) {
        try {
            String apiUrl = "https://www.youtube.com/youtubei/v1/player?key=" + apiKey;
            // IOS Client - Known to support HLS
            String jsonBody = "{" +
                    "\"videoId\": \"" + videoId + "\"," +
                    "\"context\": {" +
                    "   \"client\": {" +
                    "      \"clientName\": \"IOS\"," +
                    "      \"clientVersion\": \"19.29.1\"," +
                    "      \"deviceMake\": \"Apple\"," +
                    "      \"deviceModel\": \"iPhone16,2\"," +
                    "      \"userAgent\": \"com.google.ios.youtube/19.29.1 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X;)\","
                    +
                    "      \"osName\": \"iOS\"," +
                    "      \"osVersion\": \"17.5.1.21F90\"," +
                    "      \"hl\": \"en\"," +
                    "      \"gl\": \"US\"," +
                    "      \"utcOffsetMinutes\": 0" +
                    "   }" +
                    "}," +
                    "\"playbackContext\": {" +
                    "   \"contentPlaybackContext\": {" +
                    "      \"html5Preference\": \"HTML5_PREF_WANTS\"" +
                    "   }" +
                    "}" +
                    "}";

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("User-Agent",
                            "com.google.ios.youtube/19.29.1 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X;)")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            // Add OAuth token if available
            if (accessToken != null && !accessToken.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + accessToken);
                System.out.println("DEBUG: Using OAuth authentication");
            }

            HttpRequest request = requestBuilder.build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            JsonNode root = objectMapper.readTree(jsonResponse);
            if (root.has("streamingData")) {
                JsonNode streamingData = root.get("streamingData");
                if (streamingData.has("hlsManifestUrl")) {
                    return Optional.of(streamingData.get("hlsManifestUrl").asText());
                } else {
                    System.out.println("DEBUG: hlsManifestUrl not found in API response streamingData");
                }
            } else {
                System.out.println("DEBUG: streamingData not found in API response");
                if (root.has("playabilityStatus")) {
                    System.out.println(
                            "DEBUG: Playability Status: " + root.get("playabilityStatus").get("status").asText());
                    if (root.get("playabilityStatus").has("reason")) {
                        System.out.println("DEBUG: Reason: " + root.get("playabilityStatus").get("reason").asText());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String fetchPage(String url, String userAgent) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url));
        if (userAgent != null) {
            requestBuilder.header("User-Agent", userAgent);
        } else {
            requestBuilder.header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
        }
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private Optional<String> parseHlsUrl(String html) {
        try {
            String startPattern = "var ytInitialPlayerResponse = {";
            int startIndex = html.indexOf(startPattern);
            if (startIndex == -1) {
                startPattern = "var ytInitialPlayerResponse={";
                startIndex = html.indexOf(startPattern);
            }

            if (startIndex != -1) {
                startIndex += startPattern.length() - 1;
                int braceCount = 0;
                int endIndex = -1;

                for (int i = startIndex; i < html.length(); i++) {
                    char c = html.charAt(i);
                    if (c == '{') {
                        braceCount++;
                    } else if (c == '}') {
                        braceCount--;
                        if (braceCount == 0) {
                            endIndex = i + 1;
                            break;
                        }
                    }
                }
                if (endIndex != -1) {
                    String json = html.substring(startIndex, endIndex);
                    JsonNode root = objectMapper.readTree(json);
                    if (root.has("streamingData")) {
                        JsonNode streamingData = root.get("streamingData");
                        if (streamingData.has("hlsManifestUrl")) {
                            return Optional.of(streamingData.get("hlsManifestUrl").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
