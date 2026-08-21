package id.my.mdn.kupu.app.web.util.ytdlp;

import id.my.mdn.kupu.app.web.util.ytdlp.VideoExtractor.VideoFormat;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

@ApplicationScoped
public class ProgressiveDownloader {

    private static final Logger LOGGER = Logger.getLogger(ProgressiveDownloader.class.getName());
//    private HttpClient httpClient;

//    @PostConstruct
//    public void init() {
//        this.httpClient = HttpClient.newHttpClient();
//    }

    public void download(VideoFormat format, OutputStream outputStream) throws Exception {
        LOGGER.info("Downloading from: " + format.url);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(format.url))
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-us,en;q=0.5")
                .header("Sec-Fetch-Mode", "navigate");

        if (format.userAgent != null && !format.userAgent.isEmpty()) {
            requestBuilder.header("User-Agent", format.userAgent);
        } else {
            requestBuilder.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
        }

        HttpRequest request = requestBuilder.build();

        HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download. HTTP Status: " + response.statusCode());
        }

        long contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        if (contentLength > 0) {
            LOGGER.info("File size: " + formatBytes(contentLength));
        }

        try (InputStream in = response.body()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
        LOGGER.info("Download complete!");
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
