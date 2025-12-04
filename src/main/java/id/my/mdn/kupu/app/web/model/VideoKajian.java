/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.model;

import id.my.mdn.kupu.core.base.view.annotation.SorterField;
import id.my.mdn.kupu.core.base.view.annotation.SorterField.Sort;
import id.my.mdn.kupu.core.base.view.annotation.SorterFields;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author aphasan
 */
@Entity
@SorterFields({
    @SorterField(value = "eventTime", label = "Waktu Kajian"),
    @SorterField(value = "visitorCount", label = "Jumlah Penonton", sort = Sort.MANUAL)
})
public class VideoKajian implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    private Kajian kajian;

    private String description;

    private String notes;
    
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime eventTime;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime uploadTime;

    private boolean published;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime publishedDate;
    
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoSegment> segments;
    
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<AudioSegment> audioSegments;
    
    private boolean live;
    
    private String liveToken;
    
    private boolean publicLive;
    
    private long visitorCount;

    public boolean isPublicLive() {
        return publicLive;
    }

    public void setPublicLive(boolean publicLive) {
        this.publicLive = publicLive;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getLiveToken() {
        return liveToken;
    }

    public void setLiveToken(String liveToken) {
        this.liveToken = liveToken;
    }

    @PrePersist
    protected void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        
        if(eventTime == null) {
            eventTime = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<VideoSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<VideoSegment> segments) {
        this.segments = segments;
    }

    public List<AudioSegment> getAudioSegments() {
        return audioSegments;
    }

    public void setAudioSegments(List<AudioSegment> audioSegments) {
        this.audioSegments = audioSegments;
    }

    public long getVisitorCount() {
        return visitorCount;
    }

    public void setVisitorCount(long visitorCount) {
        this.visitorCount = visitorCount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VideoKajian)) {
            return false;
        }
        VideoKajian other = (VideoKajian) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

}
