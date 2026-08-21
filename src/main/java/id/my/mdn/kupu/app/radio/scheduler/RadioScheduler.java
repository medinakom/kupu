package id.my.mdn.kupu.app.radio.scheduler;

import id.my.mdn.kupu.app.radio.broadcast.BroadcastEngine;
import id.my.mdn.kupu.app.radio.model.ScheduledTrack;
import id.my.mdn.kupu.app.radio.playlist.PlaylistManager;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import java.util.List;

@Singleton
//@Startup
public class RadioScheduler {

    @Inject
    private PlaylistManager playlistManager;

    @Inject
    private BroadcastEngine broadcastEngine;

//    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void checkSchedule() {
        if (!broadcastEngine.isRunning()) {
            return;
        }

        List<ScheduledTrack> dueTracks = playlistManager.pollDueTracks();
        if (!dueTracks.isEmpty()) {
            System.out.println("[Scheduler] Found " + dueTracks.size() + " due tracks. Interrupting current playback.");

            // Add due tracks to the front of the playlist
            // Reverse order so that the first one due is the first one played
            for (int i = dueTracks.size() - 1; i >= 0; i--) {
//                playlistManager.addFirst(dueTracks.get(i));
            }

            // Skip current track to play the scheduled one immediately
            broadcastEngine.skip();
        }
    }
}
