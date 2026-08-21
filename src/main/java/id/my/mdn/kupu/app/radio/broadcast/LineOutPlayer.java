package id.my.mdn.kupu.app.radio.broadcast;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.OutputStream;

@ApplicationScoped
public class LineOutPlayer {

    private Process ffmpeg;
    private OutputStream stdin;
    private boolean disabled = false;

    public void start() {
        if (ffmpeg != null || disabled) {
            return;
        }

        try {
            ffmpeg = new ProcessBuilder(
                    "ffmpeg",
                    "-re",
                    "-f", "mp3",
                    "-i", "pipe:0",
                    "-f", "alsa",
                    "default").redirectError(new java.io.File("/tmp/lineout_error.log"))
                    .start();

            stdin = ffmpeg.getOutputStream();
            System.out.println("Line-out started");
        } catch (Exception e) {
            System.err.println("Failed to start Line-out player (ALSA missing?): " + e.getMessage());
            disabled = true;
        }
    }

    public void write(byte[] mp3Frame) {
        if (disabled || stdin == null) {
            return;
        }
        try {
            stdin.write(mp3Frame);
            stdin.flush();
        } catch (Exception e) {
            // If writing fails (e.g. process died), disable it to prevent spam
            System.err.println("Line-out write failed, disabling: " + e.getMessage());
            stop();
            disabled = true;
        }
    }

    public void stop() {
        disabled = false; // Reset for next attempt
        try {
            if (stdin != null) {
                stdin.close();
            }
        } catch (Exception ignored) {
        }
        if (ffmpeg != null) {
            ffmpeg.destroy();
            try {
                Thread.sleep(100); // Give ALSA time to release
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ffmpeg = null;
            stdin = null;
        }
    }

    public void setVolume(int percent) {
        if (disabled)
            return;

        try {
            // Clamp value
            if (percent < 0)
                percent = 0;
            if (percent > 100)
                percent = 100;

            new ProcessBuilder("amixer", "-q", "sset", "Master", percent + "%")
                    .start()
                    .waitFor();

            System.out.println("Volume set to " + percent + "%");
        } catch (Exception e) {
            System.err.println("Failed to set volume: " + e.getMessage());
        }
    }
}
