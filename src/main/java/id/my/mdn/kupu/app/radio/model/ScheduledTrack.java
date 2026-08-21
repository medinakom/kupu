package id.my.mdn.kupu.app.radio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "RADIO_SCHEDULEDTRACK")
public class ScheduledTrack extends Track  {
    
    @Column(columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime scheduledTime;

    public ScheduledTrack() {
        super();
    }

    public ScheduledTrack(String title, String url, LocalDateTime scheduledTime) {
        super(title, url);
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
}
