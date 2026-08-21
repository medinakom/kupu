/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view;

import id.my.mdn.kupu.app.radio.broadcast.BroadcastEngine;
import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.app.radio.playlist.TrackPlayed;
import id.my.mdn.kupu.app.radio.view.list.TrackList;
import id.my.mdn.kupu.core.base.util.RequestedView;
import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import id.my.mdn.kupu.core.base.view.annotation.View;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.event.Event;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author aphasan
 */
@Named(value = "radioPage")
@ViewScoped @View("/app/radio/index.xhtml")
public class RadioPage extends Page implements Serializable {

    @Inject
    @Bookmarked
    private TrackList trackList;

    @Inject
    @TrackPlayed
    private Event<Track> trackPlayedEvent;

    @Inject
    private BroadcastEngine engine;

    @PostConstruct
    @Override
    protected void init() {
        super.init();

        trackList.setSelectionMode(() -> SINGLE);
        trackList.getSelector().setSelectionsLabel("ms");
        trackList.getPager().setPageSizeLabel("mp");
        trackList.getPager().setOffsetLabel("mo");
    }

    @Creator(of = "trackList")
    public void openCreator() {
        RequestedView requestedView = gotoChild(TrackEditorPage.class);

        if (trackList.getSelected() != null) {
            requestedView.addParam("previous")
                    .withValues(trackList.getSelected());
        }
        
        requestedView.open();
    }

    @Editor(of = "trackList")
    public void openEditor() {
        gotoChild(TrackEditorPage.class)
                .addParam("entity")
                .withValues(trackList.getSelected())
                .open();
    }

    @Deleter(of = "trackList")
    public void openDeleter() {
        trackList.deleteSelected();
    }

    public TrackList getTrackList() {
        return trackList;
    }

}
