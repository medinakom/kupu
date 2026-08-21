/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Entity
public class Kajian extends Content {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    private Ustadz ustadz;

    @OneToMany(mappedBy = "kajian", cascade = CascadeType.ALL)
    @OrderBy("eventTime ASC")
    private List<VideoKajian> videos;

    @OneToMany(mappedBy = "kajian", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kitab> kitab;

    public Ustadz getUstadz() {
        return ustadz;
    }

    public void setUstadz(Ustadz ustadz) {
        this.ustadz = ustadz;
    }

    public List<VideoKajian> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoKajian> videos) {
        this.videos = videos;
    }

    public List<Kitab> getKitab() {
        return kitab;
    }

    public void setKitab(List<Kitab> kitab) {
        this.kitab = kitab;
    }

    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();
    }

}
