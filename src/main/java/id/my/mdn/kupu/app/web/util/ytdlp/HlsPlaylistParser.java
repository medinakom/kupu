package id.my.mdn.kupu.app.web.util.ytdlp;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class HlsPlaylistParser {

    public static class HlsStream {
        public int bandwidth;
        public String resolution;
        public String url;

        public HlsStream(int bandwidth, String resolution, String url) {
            this.bandwidth = bandwidth;
            this.resolution = resolution;
            this.url = url;
        }
    }

    public List<HlsStream> parseMasterPlaylist(String content) {
        List<HlsStream> streams = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            int currentBandwidth = 0;
            String currentResolution = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#EXT-X-STREAM-INF:")) {
                    // Parse attributes
                    Pattern bandPattern = Pattern.compile("BANDWIDTH=(\\d+)");
                    Matcher bandMatcher = bandPattern.matcher(line);
                    if (bandMatcher.find()) {
                        currentBandwidth = Integer.parseInt(bandMatcher.group(1));
                    }

                    Pattern resPattern = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
                    Matcher resMatcher = resPattern.matcher(line);
                    if (resMatcher.find()) {
                        currentResolution = resMatcher.group(1);
                    }
                } else if (!line.startsWith("#") && !line.isEmpty()) {
                    if (currentBandwidth > 0) {
                        streams.add(new HlsStream(currentBandwidth, currentResolution, line));
                        currentBandwidth = 0;
                        currentResolution = "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return streams;
    }

    public List<String> parseMediaPlaylist(String content) {
        List<String> segments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    segments.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return segments;
    }

    public HlsStream getBestStream(List<HlsStream> streams) {
        return streams.stream()
                .max(Comparator.comparingInt(s -> s.bandwidth))
                .orElse(null);
    }
}
