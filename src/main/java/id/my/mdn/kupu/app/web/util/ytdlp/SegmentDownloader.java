package id.my.mdn.kupu.app.web.util.ytdlp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class SegmentDownloader {

    private static final Logger LOGGER = Logger.getLogger(SegmentDownloader.class.getName());
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        this.executor = Executors.newFixedThreadPool(8); // 4 concurrent downloads
    }

    @PreDestroy
    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public void downloadSegments(List<String> segmentUrls, Path outputFile, String userAgent) {
        LOGGER.info("Downloading " + segmentUrls.size() + " segments to " + outputFile);

        try (FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {

            int batchSize = 10;
            for (int i = 0; i < segmentUrls.size(); i += batchSize) {
                int end = Math.min(i + batchSize, segmentUrls.size());
                List<String> batch = segmentUrls.subList(i, end);

                List<CompletableFuture<byte[]>> futures = batch.stream()
                        .map(url -> CompletableFuture.supplyAsync(() -> downloadSegment(url, userAgent), executor))                    
                        .collect(Collectors.toList());

                for (CompletableFuture<byte[]> future : futures) {
                    try {
                        byte[] data = future.join();
                        if (data != null) {
                            fos.write(data);
                        }
                    } catch (Exception e) {
                        Throwable cause = e.getCause();
                        String message = cause != null ? cause.getMessage() : e.getMessage();
                        if (message != null && message.contains("HTTP Status: 403")) {
                            throw new RuntimeException("Error downloading segment: 403", e);
                        }
                        LOGGER.severe("Failed to download segment batch starting at " + i + ": " + message);
                    }
                }

                if ((i + batchSize) % 50 == 0 || i + batchSize >= segmentUrls.size()) {
                    LOGGER.info("Progress: " + Math.min(i + batchSize, segmentUrls.size()) + "/" + segmentUrls.size()
                            + " segments...");
                }
            }

            LOGGER.info("Download complete!");

        } catch (IOException e) {
            LOGGER.severe("Error writing to file: " + e.getMessage());
        }
    }

    private byte[] downloadSegment(String url, String userAgent) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-us,en;q=0.5")
                    .header("Sec-Fetch-Mode", "navigate");
                    
            if (userAgent != null && !userAgent.isEmpty()) {
                builder.header("User-Agent", userAgent);
            }
            
            HttpRequest request = builder.build();
            HttpClient httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .localAddress(InetAddress.getByName("0.0.0.0"))
                .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                LOGGER.warning("Error downloading segment: " + response.statusCode() + " " + url);
                throw new RuntimeException("HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getMessage().startsWith("HTTP Status:")) {
                throw (RuntimeException) e;
            }
            LOGGER.warning("Exception downloading segment: " + url + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
