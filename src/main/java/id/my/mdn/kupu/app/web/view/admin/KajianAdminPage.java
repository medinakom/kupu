/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.VideoKajianAdminFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.view.filter.VideoKajianFilter;
import id.my.mdn.kupu.app.web.view.list.KajianList;
import id.my.mdn.kupu.app.web.view.list.VideoKajianList;
import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author aphasan
 */
@Named(value = "kajianAdminPage")
@ViewScoped
public class KajianAdminPage extends Page implements Serializable {

    @Bookmarked
    @Inject
    private KajianList kajianList;

    @Bookmarked
    @Inject
    private VideoKajianList videoList;

    @Inject
    private VideoKajianAdminFacade videoKajianFacade;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        kajianListInit();
        videoListInit();
    }

    private void kajianListInit() {
        kajianList.setSelectionMode(() -> SINGLE);
        kajianList.getSelector().setSelectionsLabel("ms");
        kajianList.getPager().setPageSizeLabel("mp");
        kajianList.getPager().setOffsetLabel("mo");

        kajianList.getSelector().addListener((s) -> onSelectKajian((Kajian) s));
        kajianList.getSelector().addListenerInternal((s) -> onSelectKajian((Kajian) s));
    }

    private void videoListInit() {
        videoList.setSelectionMode(() -> SINGLE);
        videoList.getSelector().setSelectionsLabel("ds");
        videoList.getSorter().setName("videoSorter");
        videoList.getPager().setPageSizeLabel("dp");
        videoList.getPager().setOffsetLabel("do");

        videoList.setDefaultChecker(() -> kajianList.getSelected() == null);
    }

    private void onSelectKajian(Kajian kajian) {
        videoList.reset();
        videoList.getFilter().<VideoKajianFilter>getContent().setKajian(kajian);
    }

    @Creator(of = "kajianList")
    public void onCreateKajian() {
        gotoChild(KajianEditorPage.class)
                .open();
    }

    @Editor(of = "kajianList")
    public void onEditKajian() {
        gotoChild(KajianEditorPage.class)
                .addParam("entity")
                .withValues(kajianList.getSelected())
                .open();
    }

    @Deleter(of = "kajianList")
    public void onDeleteKajian() {
        kajianList.deleteSelected();
        videoList.reset();
    }

    @Creator(of = "videoList")
    public void onCreateVideo() {
        gotoChild(VideoKajianEditorPage.class)
                .addParam("kajian")
                .withValues(kajianList.getSelected())
                .open();
    }

    @Editor(of = "videoList")
    public void onEditVideo() {
        gotoChild(VideoKajianEditorPage.class)
                .addParam("kajian")
                .withValues(kajianList.getSelected())
                .addParam("entity")
                .withValues(videoList.getSelected())
                .open();
    }

    @Deleter(of = "videoList")
    public void onDeleteVideo() {
        videoList.deleteSelected();
    }
    
    public void goToVideoEditor(VideoKajian video) {
        gotoChild(VideoEditorPage.class)
                .addParam("v")
                .withValues(video)
                .open();
    }

    
    public void onToggleLive(AjaxBehaviorEvent evt) {
        videoKajianFacade.toggleLive(videoList.getSelected());
    }
    
    public void onTogglePublicLive(AjaxBehaviorEvent evt) {
        videoKajianFacade.togglePublicLive(videoList.getSelected());
    }
    
    public String getUnlistedLink() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        String scheme = ec.getRequestScheme();
        String server = ec.getRequestServerName();
        int port = ec.getRequestServerPort();
        String context = ec.getRequestContextPath();
        return scheme + "://" 
                + server 
                + ((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443) ? "/" : ":" + port + "/") 
                + ((context != null && !context.isEmpty()) ? (context + "/") : "") 
                + "app/web/index.xhtml?c=live&t=" + videoList.getSelected().getLiveToken();
    }

//    public void onTagUnselect(UnselectEvent event) {
//        Tag tag = (Tag) event.getObject();
//        transientKajian.getTags().remove(tag);
//        tag.getContents().remove(transientKajian);
//    }

    public KajianList getKajianList() {
        return kajianList;
    }

    public VideoKajianList getVideoList() {
        return videoList;
    }

}
