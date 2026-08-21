package id.my.mdn.kupu.app.web.util.ytdlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStreamReader;
import java.io.BufferedReader;

@ApplicationScoped
public class VideoExtractor {

    private static final Logger LOGGER = Logger.getLogger(VideoExtractor.class.getName());

    public static class VideoFormat {

        public enum Type {
            HLS, DASH, PROGRESSIVE
        }

        public Type type;
        public String url;
        public String mimeType;
        public Integer width;
        public Integer height;
        public Long bitrate;
        public boolean hasAudio;
        public boolean hasVideo;
        public String userAgent;
        public String formatId;
        public String codecs;
        public String quality;
        public boolean isYtDlpExtracted = false;

        public VideoFormat() {
        }

        public VideoFormat(String url, Type type, String quality, String formatId, boolean hasVideo, boolean hasAudio, String codecs) {
            this.url = url;
            this.type = type;
            this.quality = quality;
            this.formatId = formatId;
            this.hasVideo = hasVideo;
            this.hasAudio = hasAudio;
            this.codecs = codecs;
        }

        public String getResolution() {
            if (width != null && height != null) {
                return width + "x" + height;
            }
            return "unknown";
        }

        @Override
        public String toString() {
            return String.format("%s [%s] %s - %s", type, getResolution(), mimeType,
                    hasAudio && hasVideo ? "video+audio" : hasVideo ? "video-only" : "audio-only");
        }
    }

    public Optional<VideoFormat> extractBestFormat(String videoUrl) {
        try {
            // 1. Fetch initial page to get API Key
            String html = fetchPage(videoUrl);
            Optional<String> apiKeyOpt = extractApiKey(html);

            if (apiKeyOpt.isEmpty()) {
                LOGGER.warning("Could not find INNERTUBE_API_KEY in HTML.");
                return Optional.empty();
            }

            String videoId = extractVideoId(videoUrl);
            LOGGER.info("Extracted API Key. Fetching from Innertube API for Video ID: " + videoId);

//          2. Fetch streaming data from API (with fallbacks)
            JsonNode streamingData = fetchStreamingData(videoId, apiKeyOpt.get(), html);
            if (streamingData == null) {
                LOGGER.warning("Could not retrieve streaming data.");
                return Optional.empty();
            }

            // 3. Parse available formats
            String usedUserAgent = streamingData.has("iosFallback") ? IOS_USER_AGENT : ANDROID_USER_AGENT;
            List<VideoFormat> formats = parseFormats(streamingData, usedUserAgent);
            if (formats.isEmpty()) {
                LOGGER.warning("No video formats found.");
                return Optional.empty();
            }

            // 4. Select best format
            Optional<VideoFormat> bestNative = selectBestFormat(formats);
            if (bestNative.isPresent()) {
                return bestNative;
            }
            
            // 5. Fallback to Yt-dlp if native decoding of progressive stream URL is blocked
            LOGGER.warning("Native parsing yielded no usable accessible formats, falling back to yt-dlp wrapper");
            return fetchFormatsWithYtdlp(videoUrl);

        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Error extracting native format, attempting yt-dlp fallback...", e);
            return fetchFormatsWithYtdlp(videoUrl);
        }
    }

    private static final String ANDROID_USER_AGENT = "com.google.android.youtube/19.29.35 (Linux; U; Android 11) gzip";
    private static final String IOS_USER_AGENT = "com.google.ios.youtube/19.29.1 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X;)";
    private static final String HTML_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";

    private JsonNode fetchStreamingData(String videoId, String apiKey, String html) {
        try {
            String apiUrl = "https://www.youtube.com/youtubei/v1/player?key=" + apiKey;

            // Use ANDROID client for better reliability
            String jsonBody = "{"
                    + "\"videoId\": \"" + videoId + "\","
                    + "\"context\": {"
                    + "   \"client\": {"
                    + "      \"clientName\": \"ANDROID\","
                    + "      \"clientVersion\": \"19.29.35\","
                    + "      \"osName\": \"Android\","
                    + "      \"osVersion\": \"11\","
                    + "      \"androidSdkVersion\": 30,"
                    + "      \"hl\": \"en\","
                    + "      \"gl\": \"US\","
                    + "      \"timeZone\": \"UTC\""
                    + "   }"
                    + "},"
                    + "\"playbackContext\": {"
                    + "   \"contentPlaybackContext\": {"
                    + "      \"html5Preference\": \"HTML5_PREF_WANTS\""
                    + "   }"
                    + "}"
                    + "}";

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", ANDROID_USER_AGENT)
                    .header("X-YouTube-Client-Name", "3")
                    .header("X-YouTube-Client-Version", "19.29.35")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            HttpRequest request = requestBuilder.build();

            HttpClient httpClient = HttpClient.newBuilder().localAddress(InetAddress.getByName("0.0.0.0")).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("playabilityStatus")) {
                JsonNode status = root.get("playabilityStatus");
                LOGGER.info("Playability Status: " + status.get("status").asText());
                if (status.has("reason")) {
                    LOGGER.info("Reason: " + status.get("reason").asText());
                }
            }

            if (root.has("streamingData")) {
                return root.get("streamingData");
            } else {
                LOGGER.warning("No streamingData found with ANDROID client for videoId: " + videoId + ". Attempting iOS fallback...");
                return fetchStreamingDataWithIos(videoId, apiKey, html);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching streaming data", e);
        }
        return null;
    }

    private JsonNode fetchStreamingDataWithIos(String videoId, String apiKey, String html) {
        try {
            String apiUrl = "https://www.youtube.com/youtubei/v1/player?key=" + apiKey;

            // IOS Client - Known to support HLS and bypass some restrictions
            String jsonBody = "{"
                    + "\"videoId\": \"" + videoId + "\","
                    + "\"context\": {"
                    + "   \"client\": {"
                    + "      \"clientName\": \"IOS\","
                    + "      \"clientVersion\": \"19.29.1\","
                    + "      \"deviceMake\": \"Apple\","
                    + "      \"deviceModel\": \"iPhone16,2\","
                    + "      \"userAgent\": \"com.google.ios.youtube/19.29.1 (iPhone16,2; U; CPU iOS 17_5_1 like Mac OS X;)\","
                    + "      \"osName\": \"iOS\","
                    + "      \"osVersion\": \"17.5.1.21F90\","
                    + "      \"hl\": \"en\","
                    + "      \"gl\": \"US\","
                    + "      \"utcOffsetMinutes\": 0"
                    + "   }"
                    + "},"
                    + "\"playbackContext\": {"
                    + "   \"contentPlaybackContext\": {"
                    + "      \"html5Preference\": \"HTML5_PREF_WANTS\""
                    + "   }"
                    + "}"
                    + "}";

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", IOS_USER_AGENT)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

            HttpRequest request = requestBuilder.build();

            HttpClient httpClient = HttpClient.newBuilder().localAddress(InetAddress.getByName("0.0.0.0")).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);

            if (root.has("streamingData")) {
                LOGGER.info("Successfully retrieved streamingData using iOS fallback.");
                JsonNode streamingNode = root.get("streamingData");
                // Inject a marker so we know this came from the iOS fallback
                if (streamingNode.isObject()) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) streamingNode).put("iosFallback", true);
                }
                return streamingNode;
            } else {
                LOGGER.warning("No streamingData found in iOS fallback response. Attempting HTML parsing fallback...");
                if (root.has("playabilityStatus")) {
                    JsonNode status = root.get("playabilityStatus");
                    LOGGER.info("iOS Fallback Playability Status: " + status.get("status").asText());
                    if (status.has("reason")) {
                        LOGGER.info("Reason: " + status.get("reason").asText());
                    }
                }
                if (root.has("error")) {
                    LOGGER.warning("iOS Fallback API Error: " + root.get("error").toString());
                }
                return parseStreamingDataFromHtml(html);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching streaming data with iOS fallback", e);
        }
        return parseStreamingDataFromHtml(html);
    }

    private JsonNode parseStreamingDataFromHtml(String html) {
        try {
            LOGGER.info("Attempting to parse streamingData directly from HTML...");
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

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode root = objectMapper.readTree(json);

                    if (root.has("playabilityStatus")) {
                        JsonNode status = root.get("playabilityStatus");
                        LOGGER.info("HTML Fallback Playability Status: " + status.get("status").asText());
                    }

                    if (root.has("streamingData")) {
                        LOGGER.info("Successfully parsed streamingData from HTML.");
                        JsonNode streamingNode = root.get("streamingData");
                        if (streamingNode.isObject()) {
                            ((com.fasterxml.jackson.databind.node.ObjectNode) streamingNode).put("htmlFallback", true);
                        }
                        return streamingNode;
                    } else {
                        LOGGER.warning("streamingData not found in HTML ytInitialPlayerResponse.");
                    }
                }
            } else {
                LOGGER.warning("ytInitialPlayerResponse not found in HTML.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing streamingData from HTML", e);
        }
        return null;
    }

    private List<VideoFormat> parseFormats(JsonNode streamingData, String userAgentFallback) {
        List<VideoFormat> formats = new ArrayList<>();

        // Check for HLS manifest
        if (streamingData.has("hlsManifestUrl")) {
            VideoFormat hlsFormat = new VideoFormat();
            hlsFormat.type = VideoFormat.Type.HLS;
            hlsFormat.url = streamingData.get("hlsManifestUrl").asText();
            hlsFormat.hasAudio = true;
            hlsFormat.hasVideo = true;
            hlsFormat.userAgent = userAgentFallback;
            formats.add(hlsFormat);
            LOGGER.info("Found HLS format: " + hlsFormat.url);
        }

        // Check for progressive formats
        if (streamingData.has("formats")) {
            JsonNode formatsArray = streamingData.get("formats");
            for (JsonNode format : formatsArray) {
                VideoFormat vf = parseFormat(format, userAgentFallback);
                if (vf != null) {
                    formats.add(vf);
                }
            }
        }

        // Check for adaptive formats (video-only or audio-only)
        if (streamingData.has("adaptiveFormats")) {
            JsonNode adaptiveFormats = streamingData.get("adaptiveFormats");
            for (JsonNode format : adaptiveFormats) {
                VideoFormat vf = parseFormat(format, userAgentFallback);
                if (vf != null) {
                    formats.add(vf);
                }
            }
        }

        LOGGER.info("Found " + formats.size() + " total formats");
        return formats;
    }

    private VideoFormat parseFormat(JsonNode format, String userAgentFallback) {
        if (!format.has("url")) {
            return null; // Skip formats without direct URL
        }

        VideoFormat vf = new VideoFormat();
        vf.type = VideoFormat.Type.PROGRESSIVE;
        vf.url = format.get("url").asText();
        vf.mimeType = format.has("mimeType") ? format.get("mimeType").asText() : "unknown";
        vf.width = format.has("width") ? format.get("width").asInt() : null;
        vf.height = format.has("height") ? format.get("height").asInt() : null;
        vf.bitrate = format.has("bitrate") ? format.get("bitrate").asLong() : null;

        // Extract User-Agent
        if (format.has("http_headers") && format.get("http_headers").has("User-Agent")) {
            vf.userAgent = format.get("http_headers").get("User-Agent").asText();
        }

        // Determine if format has audio/video
        String mimeType = vf.mimeType.toLowerCase();
        vf.hasVideo = mimeType.contains("video");
        vf.hasAudio = mimeType.contains("audio")
                || (format.has("audioQuality") && !format.get("audioQuality").isNull());

        // Set User-Agent from fallback if missing
        if (vf.userAgent == null) {
            vf.userAgent = userAgentFallback;
        }

        return vf;
    }

    public List<VideoFormat> extractAudioFormats(String videoUrl) {
        List<VideoFormat> formats = new ArrayList<>();
        try {
            String html = fetchPage(videoUrl);
            Optional<String> apiKeyOpt = extractApiKey(html);
            if (apiKeyOpt.isPresent()) {
                String videoId = extractVideoId(videoUrl);
                JsonNode streamingData = fetchStreamingData(videoId, apiKeyOpt.get(), html);
                if (streamingData != null) {
                    String usedUserAgent = streamingData.has("htmlFallback") ? HTML_USER_AGENT
                            : streamingData.has("iosFallback") ? IOS_USER_AGENT : ANDROID_USER_AGENT;
                    formats = parseFormats(streamingData, usedUserAgent).stream()
                            .filter(f -> f.hasAudio)
                            .sorted(Comparator.comparing((VideoFormat f) -> f.type == VideoFormat.Type.HLS ? 1 : 0)
                                    .reversed()
                                    .thenComparing(f -> f.bitrate != null ? f.bitrate : 0L)
                                    .reversed())
                            .collect(java.util.stream.Collectors.toList());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error extracting audio formats", e);
        }

        return formats;
    }

    private Optional<VideoFormat> selectBestFormat(List<VideoFormat> formats) {
        // Priority:
        // 1. HLS (if available)
        // 2. Progressive with both audio and video (prefer higher resolution)
        // 3. Progressive video-only (prefer higher resolution)

        Optional<VideoFormat> hls = formats.stream()
                .filter(f -> f.type == VideoFormat.Type.HLS)
                .findFirst();
        if (hls.isPresent()) {
            LOGGER.info("Selected: HLS format (live stream)");
            return hls;
        }

        // Prefer formats with both audio and video
        Optional<VideoFormat> progressive = formats.stream()
                .filter(f -> f.type == VideoFormat.Type.PROGRESSIVE)
                .filter(f -> f.hasAudio && f.hasVideo)
                .max(Comparator.comparing((VideoFormat f) -> f.height != null ? f.height : 0)
                        .thenComparing(f -> f.bitrate != null ? f.bitrate : 0L));

        if (progressive.isPresent()) {
            LOGGER.info("Selected: " + progressive.get());
            return progressive;
        }

        // Fallback to video-only format
        Optional<VideoFormat> videoOnly = formats.stream()
                .filter(f -> f.type == VideoFormat.Type.PROGRESSIVE)
                .filter(f -> f.hasVideo)
                .max(Comparator.comparing((VideoFormat f) -> f.height != null ? f.height : 0)
                        .thenComparing(f -> f.bitrate != null ? f.bitrate : 0L));

        if (videoOnly.isPresent()) {
            LOGGER.info("Selected: " + videoOnly.get() + " (WARNING: No audio)");
        }

        return videoOnly;
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

    private String fetchPage(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36")
                .build();
        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .localAddress(InetAddress.getByName("0.0.0.0"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private Optional<VideoFormat> fetchFormatsWithYtdlp(String videoUrl) {
        String ytDlpPath = findYtDlpPath();
        if (ytDlpPath == null) {
            LOGGER.severe("yt-dlp binary not found in PATH or standard locations. Cannot fallback.");
            return Optional.empty();
        }

        try {
            LOGGER.info("Executing yt-dlp to dump JSON for: " + videoUrl);
            ProcessBuilder pb = new ProcessBuilder(ytDlpPath, "-J", videoUrl);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                LOGGER.severe("yt-dlp execution failed with code " + exitCode);
                return Optional.empty();
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(output.toString());
            List<VideoFormat> formats = new ArrayList<>();

            if (root.has("formats")) {
                for (JsonNode formatNode : root.get("formats")) {
                    if (formatNode.has("url") && formatNode.has("vcodec") && formatNode.has("acodec")) {
                        VideoFormat vf = new VideoFormat();
                        vf.url = formatNode.get("url").asText();
                        vf.formatId = formatNode.has("format_id") ? formatNode.get("format_id").asText() : "unknown";
                        
                        String vcodec = formatNode.get("vcodec").asText();
                        String acodec = formatNode.get("acodec").asText();
                        
                        vf.hasVideo = !vcodec.equals("none");
                        vf.hasAudio = !acodec.equals("none");
                        vf.codecs = vcodec + ", " + acodec;
                        
                        vf.width = formatNode.has("width") && !formatNode.get("width").isNull() 
                                ? formatNode.get("width").asInt() : null;
                        vf.height = formatNode.has("height") && !formatNode.get("height").isNull() 
                                ? formatNode.get("height").asInt() : null;
                        vf.bitrate = formatNode.has("tbr") && !formatNode.get("tbr").isNull() 
                                ? (long)(formatNode.get("tbr").asDouble() * 1024) : null;
                                
                        vf.quality = formatNode.has("format_note") ? formatNode.get("format_note").asText() : "";
                        
                        if (formatNode.has("protocol")) {
                            String protocol = formatNode.get("protocol").asText().toLowerCase();
                            if (protocol.contains("m3u8")) {
                                vf.type = VideoFormat.Type.HLS;
                                vf.mimeType = "application/x-mpegURL";
                            } else {
                                vf.type = VideoFormat.Type.PROGRESSIVE;
                                vf.mimeType = formatNode.has("ext") ? "video/" + formatNode.get("ext").asText() : "video/mp4";
                            }
                        } else {
                            vf.type = VideoFormat.Type.PROGRESSIVE;
                            vf.mimeType = "video/mp4";
                        }
                        
                        // Extract User-Agent from yt-dlp http_headers if available
                        if (formatNode.has("http_headers") && formatNode.get("http_headers").has("User-Agent")) {
                            vf.userAgent = formatNode.get("http_headers").get("User-Agent").asText();
                        } else {
                            vf.userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
                        }
                        
                        vf.isYtDlpExtracted = true;
                        formats.add(vf);
                    }
                }
            }

            if (formats.isEmpty()) {
                LOGGER.warning("yt-dlp dumped JSON but no usable formats were parsed.");
                return Optional.empty();
            }

            LOGGER.info("yt-dlp parsed " + formats.size() + " formats. Selecting best native match...");
            return selectBestFormat(formats);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to execute or parse yt-dlp JSON fallback processing", e);
            return Optional.empty();
        }
    }

    private String findYtDlpPath() {
        String[] possiblePaths = {
            "yt-dlp",
            "/usr/local/bin/yt-dlp",
            "/usr/bin/yt-dlp",
            "/opt/homebrew/bin/yt-dlp"
        };
        
        for (String path : possiblePaths) {
            try {
                Process p = new ProcessBuilder(path, "--version").start();
                if (p.waitFor() == 0) {
                    return path;
                }
            } catch (Exception e) {
                // Ignore, try next path
            }
        }
        return null;
    }
}
