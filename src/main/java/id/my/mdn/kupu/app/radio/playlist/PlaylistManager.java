package id.my.mdn.kupu.app.radio.playlist;

import id.my.mdn.kupu.app.radio.dao.TrackFacade;
import id.my.mdn.kupu.app.radio.model.ScheduledTrack;
import id.my.mdn.kupu.app.radio.model.Track;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
@ApplicationScoped
public class PlaylistManager {

    @Inject
    private TrackFacade trackFacade;

    private boolean loopEnabled = true;

    private Track currentTrack;

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
    }

    // Use CopyOnWriteArrayList for thread safety during iteration/UI updates
    private final List<ScheduledTrack> scheduledTracks = new CopyOnWriteArrayList<>();

    // =====================================================
    // ADD TRACK
    // =====================================================
    public synchronized void add(@ObservesAsync @TrackAdded Track track) {
        notifyAll();
    }

    // =====================================================
    // LOOP CONTROL
    // =====================================================
    public synchronized void setLoopEnabled(boolean enabled) {
        this.loopEnabled = enabled;
    }

    public synchronized boolean isLoopEnabled() {
        return loopEnabled;
    }

    // =====================================================
    // BLOCKING NEXT TRACK
    // =====================================================
    public synchronized Track nextBlocking(Track startTrack) throws InterruptedException {

        if (startTrack == null && currentTrack == null) {
            while ((currentTrack = trackFacade.getFirstTrack()) == null) { // Wait until first track added
                System.err.println("PLAYLIST MANAGER WAITING...");
                wait(); // ZERO CPU WAIT
            }
        } else if (startTrack != null && currentTrack == null) { // Start broadcast with custom start track         
            currentTrack = startTrack;
        } else {
            Track nextTrack = trackFacade.getNextTrack(currentTrack);
            if (nextTrack != null) {
                currentTrack = nextTrack;
            } else {
                if (loopEnabled) {
                    currentTrack = trackFacade.getFirstTrack();
                } else {
                    currentTrack = null;
                    throw new InterruptedException("Reached end of playlist");
                }
            }
        }

        return currentTrack;
    }

    public void addScheduled(ScheduledTrack track) {
        scheduledTracks.add(track);
    }

    public List<ScheduledTrack> getScheduledTracks() {
        return scheduledTracks;
    }

    public void removeScheduled(ScheduledTrack scheduledTrack) {
        scheduledTracks.remove(scheduledTrack);
    }

    public synchronized List<ScheduledTrack> pollDueTracks() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledTrack> due = new java.util.ArrayList<>();

        var iterator = scheduledTracks.iterator();
        while (iterator.hasNext()) {
            ScheduledTrack t = iterator.next();
            if (t.getScheduledTime().isBefore(now) || t.getScheduledTime().isEqual(now)) {
                due.add(t);
                scheduledTracks.remove(t);
            }
        }
        return due;
    }
}
