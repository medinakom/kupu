package id.my.mdn.kupu.app.web.util.ytdlp;

import id.my.mdn.kupu.app.web.util.ytdlp.VideoExtractor.VideoFormat;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class YoutubeService {

    private static final Logger LOGGER = Logger.getLogger(YoutubeService.class.getName());

    @Inject
    private VideoExtractor videoExtractor;

    @Inject
    private ProgressiveDownloader progressiveDownloader;

    @Inject
    private SegmentDownloader segmentDownloader;

    @Inject
    private HlsPlaylistParser hlsPlaylistParser;

    public void downloadBestFormatToTemp(String url, Path output) throws Exception {
        Optional<VideoFormat> bestFormatOpt = videoExtractor.extractBestFormat(url);
        if (bestFormatOpt.isEmpty()) {
            throw new RuntimeException("No suitable video format found for URL: " + url);
        }
        VideoFormat bestFormat = bestFormatOpt.get();
        LOGGER.info("Downloading best format: " + bestFormat);

        Path tempFile = Files.createTempFile("ytdlp-best-", ".mp4");

        if (bestFormat.isYtDlpExtracted) {
            LOGGER.info("Format requires yt-dlp direct download. Passing URL to yt-dlp binary...");
            downloadWithYtdlp(url, output);
        } else if (bestFormat.type == VideoFormat.Type.HLS) {
            boolean useYtdlpFallback = false;
            try {
                downloadHlsToPath(bestFormat.url, output, bestFormat.userAgent);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("403")) {
                    LOGGER.warning("Native HLS download blocked with 403 Forbidden. Falling back to direct yt-dlp execution over URL: " + url);
                    useYtdlpFallback = true;
                } else {
                    throw e;
                }
            }
            
            if (useYtdlpFallback) {
                Files.deleteIfExists(output);
                downloadWithYtdlp(url, output);
            }
        } else {
            // Progressive download to file
            boolean useYtdlpFallback = false;
            try (OutputStream out = new java.io.FileOutputStream(output.toFile())) {
                try {
                    progressiveDownloader.download(bestFormat, out);
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().contains("HTTP Status: 403")) {
                        LOGGER.warning("Native download blocked with 403 Forbidden. Falling back to direct yt-dlp execution over URL: " + url);
                        useYtdlpFallback = true;
                    } else {
                        throw e; // rethrow if it's not a 403
                    }
                }
            }
            
            if (useYtdlpFallback) {
                Files.deleteIfExists(output);
                downloadWithYtdlp(url, output);
            }
        }
    }

    public void download(VideoFormat format, OutputStream outputStream) throws Exception {
        if (format.type == VideoFormat.Type.HLS) {
            Path tempFile = Files.createTempFile("ytdlp-hls-", ".mp4");
            boolean useYtdlpFallback = false;
            try {
                try {
                    downloadHlsToPath(format.url, tempFile, format.userAgent);
                } catch (Exception e) {
                    if (e.getMessage() != null && e.getMessage().contains("403")) {
                        LOGGER.warning("Native HLS download blocked with 403 Forbidden. Falling back to direct yt-dlp stream simulation.");
                        useYtdlpFallback = true;
                    } else {
                        throw e;
                    }
                }

                if (useYtdlpFallback) {
                    // yt-dlp doesn't stream to OutputStream easily in this architecture without refactor
                    // For now, use the same logic as downloadBestFormatToTemp if they want a file
                    throw new RuntimeException("HTTP 403 when trying to stream HLS download. Use downloadBestFormatToTemp instead for yt-dlp fallback support.");
                }

                // Stream to output
                try (InputStream in = new FileInputStream(tempFile.toFile())) {
                    in.transferTo(outputStream);
                }
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } else {
            try {
                progressiveDownloader.download(format, outputStream);
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("HTTP Status: 403")) {
                    LOGGER.warning("Native streaming download blocked with 403 Forbidden. Cannot stream yt-dlp fallback directly to outputStream natively without temp file buffering.");
                    throw new RuntimeException("HTTP 403 when trying to stream progressive download. Use downloadBestFormatToTemp instead for yt-dlp fallback support.", e);
                } else {
                    throw e;
                }
            }
        }
    }
    
    private void downloadHlsToPath(String masterPlaylistUrl, Path targetFile, String userAgent) throws Exception {
        String masterPlaylistContent = fetch(masterPlaylistUrl, userAgent);

        List<HlsPlaylistParser.HlsStream> streams = hlsPlaylistParser.parseMasterPlaylist(masterPlaylistContent);

        if (streams.isEmpty()) {
            throw new RuntimeException("No streams found in master playlist.");
        }

        HlsPlaylistParser.HlsStream bestStream = hlsPlaylistParser.getBestStream(streams);
        LOGGER.info("Selected Best Stream: " + bestStream.resolution + " (" + bestStream.bandwidth + " bps)");

        String mediaPlaylistUrl = bestStream.url;
        // Handle relative URLs
        if (!mediaPlaylistUrl.startsWith("http")) {
            mediaPlaylistUrl = resolve(masterPlaylistUrl, mediaPlaylistUrl);
        }
        LOGGER.info("Media Playlist URL: " + mediaPlaylistUrl);

        String mediaPlaylistContent = fetch(mediaPlaylistUrl, userAgent);
        List<String> segmentUrls = hlsPlaylistParser.parseMediaPlaylist(mediaPlaylistContent);

        // For live streams, old segments expire and return 403. Only keep the last 100 segments.
        int maxSegmentsToDownload = 100;
        if (segmentUrls.size() > maxSegmentsToDownload) {
            segmentUrls = segmentUrls.subList(segmentUrls.size() - maxSegmentsToDownload, segmentUrls.size());
            LOGGER.info("Playlist is large. Only downloading the last " + maxSegmentsToDownload + " segments to avoid expired ones.");
        }

        // Resolve segment URLs
        String finalMediaPlaylistUrl = mediaPlaylistUrl;
        List<String> resolvedSegmentUrls = segmentUrls.stream()
                .map(url -> {
                    if (!url.startsWith("http")) {
                        return resolve(finalMediaPlaylistUrl, url);
                    }
                    return url;
                })
                .collect(Collectors.toList());

        LOGGER.info("Found " + resolvedSegmentUrls.size() + " segments. Downloading to " + targetFile);
        segmentDownloader.downloadSegments(resolvedSegmentUrls, targetFile, userAgent);
    }

    private String fetch(String url, String userAgent) throws Exception {
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
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch URL: " + url + " Status: " + response.statusCode());
        }
        return response.body();
    }

    private String resolve(String baseUrl, String relativeUrl) {
        try {
            URI base = URI.create(baseUrl);
            return base.resolve(relativeUrl).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error resolving URL: " + relativeUrl + " relative to " + baseUrl, e);
        }
    }
    
    private void downloadWithYtdlp(String videoUrl, Path targetFile) throws Exception {
        String ytDlpPath = findYtDlpPath();
        if (ytDlpPath == null) {
            throw new RuntimeException("yt-dlp binary not found. Cannot delegate download.");
        }
        
        // Command bypasses Java networking bugs completely and delegates to official python downloader
        ProcessBuilder pb = new ProcessBuilder(
            ytDlpPath, 
            "--force-overwrites",
            "-f", "best[ext=mp4]/best", 
            "-o", targetFile.toAbsolutePath().toString(), 
            videoUrl
        );
        
        pb.redirectErrorStream(true);
        Process p = pb.start();
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info("[yt-dlp wrapper]: " + line);
            }
        }
        
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp download failed with exit code: " + exitCode);
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
