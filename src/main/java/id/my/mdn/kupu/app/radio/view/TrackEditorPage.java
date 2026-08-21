/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view;

import id.my.mdn.kupu.app.radio.dao.TrackFacade;
import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.app.radio.view.list.KajianSelector;
import id.my.mdn.kupu.app.radio.view.list.VideoKajianSelector;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Named
@ConversationScoped
public class TrackEditorPage extends FormPage<Track> {

    @Inject
    private TrackFacade dao;

    @Inject
    private KajianSelector kajianList;

    @Bookmarked
    private Kajian selectedKajian;

    @Bookmarked(name = "previous")
    private Track previousTrack;

    @Inject
    private VideoKajianSelector videoList;

    @Bookmarked
    private VideoKajian selectedVideo;

    @PostConstruct
    protected void init() {
        super.init();
    }

    @Override
    public void load() {
        super.load();
        videoList.getFilter().setStaticFilter(this::filterVideoKajian);
    }

    private List<FilterData> filterVideoKajian() {
        return List.of(FilterData.by("kajian", selectedKajian));
    }

    @Override
    protected Track newEntity() {
        Track track = new Track();
        if (previousTrack != null) {
            track.setPrevious(previousTrack);
        }

        return track;
    }

    @Override
    protected Result<String> save(Track entity) {
        entity.setTitle("[%s] [%s] %s".formatted(selectedKajian.getUstadz().getFullname(), selectedKajian.getTitle(), selectedVideo.getNotes()));
        entity.setUrl("/api/audiokajian/%s/stream".formatted(selectedVideo.getId()));
        Result<String> created = dao.create(entity);

        return created;
    }

    @Override
    protected Result<String> edit(Track entity) {
        entity.setTitle("[%s] [%s] %s".formatted(selectedKajian.getUstadz().getFullname(), selectedKajian.getTitle(), selectedVideo.getNotes()));
        entity.setUrl("/api/audiokajian/%s/stream".formatted(selectedVideo.getId()));
        return dao.edit(entity);
    }

    public Track getPreviousTrack() {
        return previousTrack;
    }

    public void setPreviousTrack(Track previousTrack) {
        this.previousTrack = previousTrack;
    }

    public KajianSelector getKajianList() {
        return kajianList;
    }

    public VideoKajianSelector getVideoList() {
        return videoList;
    }

    public Kajian getSelectedKajian() {
        return selectedKajian;
    }

    public void setSelectedKajian(Kajian selectedKajian) {
        this.selectedKajian = selectedKajian;
    }

    public VideoKajian getSelectedVideo() {
        return selectedVideo;
    }

    public void setSelectedVideo(VideoKajian selectedVideo) {
        this.selectedVideo = selectedVideo;
    }

}
