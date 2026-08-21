package id.my.mdn.kupu.app.radio.broadcast;

import id.my.mdn.kupu.app.radio.audio.HlsProducer;
import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.app.radio.playlist.PlaylistManager;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Named
@ApplicationScoped
public class BroadcastEngine {

    @Inject
    private PlaylistManager playlistManager;

    @Inject
    private HlsProducer producer;

    @Inject
    private LineOutPlayer lineOutPlayer;

    private ExecutorService executor;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private int volume = 100;

    public enum BroadcastMode {
        PLAYLIST,
        LIVE
    }

    private BroadcastMode mode = BroadcastMode.PLAYLIST;

    // =====================================================
    // CONTROL PANEL METHODS
    // =====================================================
    public synchronized void startLive(ActionEvent evt) {
        if (running.get()) {
            if (mode == BroadcastMode.PLAYLIST) {
                return; // Must stop playlist first if running
            }
            return;
        }

        mode = BroadcastMode.LIVE;
        running.set(true);
        paused.set(false);

        // No executor needed for live mode as HlsProducer manages its own thread
        producer.startLive();
    }

    public synchronized void start(Track starTrack) {
        if (running.get()) {
            if (mode == BroadcastMode.LIVE) {
                return; // Must stop live first if running
            }
            if (paused.get()) {
                resume(null);
            }
            return;
        }

        mode = BroadcastMode.PLAYLIST;
        running.set(true);
        paused.set(false);

        executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "broadcast-engine"));
//        currentTrack = starTrack;
        executor.submit(() -> {
            runLoop(starTrack);
        });
    }

    // =====================================================
    // CORE ENGINE LOOP
    // =====================================================

//    private volatile Track currentTrack;

    private void runLoop(Track startTrack) {

        try {
            while (running.get()) {

                if (paused.get()) {
                    Thread.sleep(200);
                    continue;
                }

                Track nowPlayingTrack = playlistManager.nextBlocking(startTrack);
                if (!running.get()) {
                    break;
                }

//                this.currentTrack = track;
                System.out.println("Now playing: " + nowPlayingTrack.getTitle());
                producer.start("http://localhost:8080" + nowPlayingTrack.getUrl()); // must be blocking
//                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            this.currentTrack = null;
            playlistManager.setCurrentTrack(null);
            producer.stop();
            System.out.println("Broadcast engine stopped.");
        }
    }


    public synchronized void stop(ActionEvent evt) {

        running.set(false);
        paused.set(false);

        producer.stop();

        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public void pause(ActionEvent evt) {
        paused.set(true);
        producer.pause();
    }

    public void skip() {
        producer.stop();
    }

    public void resume(ActionEvent evt) {
        if (!running.get()) {
            return;
        }
        paused.set(false);
        producer.resume();
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isPaused() {
        return paused.get();
    }

    public BroadcastMode getMode() {
        return mode;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        lineOutPlayer.setVolume(volume);
    }

//    public Track getCurrentTrack() {
//        return currentTrack;
//    }
    
    @PreDestroy
    public void shutdown() {
        stop(null);
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
