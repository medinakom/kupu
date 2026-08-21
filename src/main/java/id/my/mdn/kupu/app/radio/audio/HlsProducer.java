package id.my.mdn.kupu.app.radio.audio;

import id.my.mdn.kupu.app.radio.broadcast.LineOutPlayer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class HlsProducer {

    @Inject
    private AudioBuffer buffer;

    @Inject
    private LineOutPlayer player;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    private Process ffmpeg;

    // =========================
    // PUBLIC CONTROL
    // =========================

    public void start(String url) {
        if (!running.compareAndSet(false, true)) {
            return; // already running
        }

        try {
            runProducer(url);
        } finally {
            if (!paused.get()) { // Only reset if not just paused? No, pause is different.
                // If we return from runProducer, the track is done or stopped.
                // We must release the 'running' lock so the next track can start.
                running.set(false);
            }
        }
    }

    public void stop() {
        running.set(false);
        paused.set(false);

        if (ffmpeg != null) {
            ffmpeg.destroy();
            ffmpeg = null;
        }

        player.stop();
    }

    public void pause() {
        paused.set(true);
    }

    public void resume() {
        paused.set(false);
    }

    // =========================
    // INTERNAL LOOP
    // =========================

    public void startLive() {
        if (!running.compareAndSet(false, true)) {
            return; // already running
        }

        // Run in live mode (RTMP Listener)
        new Thread(() -> runLiveProducer()).start();
    }

    private void runLiveProducer() {
        System.out.println("Entering RTMP Server Loop on port 1935...");

        // Loop to allow repeated connections (OBS Stop/Start)
        while (running.get()) {
            try {
                System.out.println("(Re)Starting ffmpeg listener...");
                ffmpeg = new ProcessBuilder(
                        "ffmpeg",
                        "-listen", "1",
                        "-timeout", "30000", // 30s timeout to wait for connection
                        "-i", "rtmp://0.0.0.0:1935/live/s3cr3t57",
                        "-vn",
                        "-ac", "2",
                        "-ar", "44100",
                        "-f", "mp3",
                        "-")
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start();

                player.start();

                try (InputStream in = ffmpeg.getInputStream()) {
                    byte[] chunk = new byte[16384];
                    int read;

                    // Read loop for CURRENT connection
                    while (running.get()) {
                        if (paused.get()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            continue;
                        }

                        if ((read = in.read(chunk)) == -1) {
                            System.out.println("RTMP Stream ended (Client disconnected)");
                            break; // Connection lost, break inner loop to restart listener
                        }

                        byte[] frame = new byte[read];
                        System.arraycopy(chunk, 0, frame, 0, read);

                        buffer.write(frame);
                        try {
                            player.write(frame);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Process finished (client disconnected or timeout)
                if (ffmpeg != null) {
                    ffmpeg.waitFor(); // Ensure process is dead
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Avoid spin-loop if start fails repeatedly
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            } finally {
                // Partial cleanup between connections (don't stop 'running')
                if (ffmpeg != null) {
                    ffmpeg.destroy();
                    ffmpeg = null;
                }
            }
        }

        // Final cleanup when running is false
        stopAll();
    }

    private void runProducer(String url) {
        try {
            ffmpeg = new ProcessBuilder(
                    "ffmpeg",
                    "-re",
                    "-i", url,
                    "-vn",
                    "-ac", "2",
                    "-ar", "44100",
                    "-f", "mp3",
                    "-")
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();

            player.start();

            try (InputStream in = ffmpeg.getInputStream()) {

                byte[] chunk = new byte[16384];
                int read;

                while (running.get()) {
                    if (paused.get()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        continue;
                    }

                    if ((read = in.read(chunk)) == -1) {
                        break;
                    }

                    byte[] frame = new byte[read];
                    System.arraycopy(chunk, 0, frame, 0, read);

                    buffer.write(frame);
                    try {
                        player.write(frame);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (java.io.IOException e) {
            if (running.get()) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanupProcess();
            // IMPORTANT: In playlist mode, a track ending (read == -1) should NOT set
            // running=false.
            // running=false only happens if stop() is called.
            // So we call cleanupProcess() instead of stopAll().
        }
    }

    private void cleanupProcess() {
        if (ffmpeg != null) {
            ffmpeg.destroy();
            ffmpeg = null;
        }
        player.stop();
        // and reset pause state for next track
        paused.set(false);
    }

    private void stopAll() {
        running.set(false);
        cleanupProcess();
    }
}
