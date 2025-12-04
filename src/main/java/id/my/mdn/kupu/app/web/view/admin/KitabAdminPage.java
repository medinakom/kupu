/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.KitabContentFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.KitabContent;
import id.my.mdn.kupu.app.web.view.filter.VideoKajianFilter;
import id.my.mdn.kupu.app.web.view.list.KajianList;
import id.my.mdn.kupu.app.web.view.list.KitabList;
import id.my.mdn.kupu.app.web.view.list.VideoKajianList;
import id.my.mdn.kupu.core.base.view.Page;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import jakarta.annotation.PostConstruct;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author aphasan
 */
@Named(value = "kitabAdminPage")
@ViewScoped
public class KitabAdminPage extends Page implements Serializable {

    @Inject
    private KajianList kajianList;

    @Inject
    private VideoKajianList videoList;

    private Kajian selectedKajian;

    private Kajian transientKajian;

    @Inject
    private KitabFacade kitabFacade;

    @Inject
    private KitabContentFacade kitabContentFacade;

    @Inject
    private KitabList kitabList;

    private Kitab selectedKitab;

    private Kitab transientKitab;

    private StreamedContent previewKitab;
    
    @PostConstruct
    @Override
    public void init() {
        super.init();
        kajianListInit();
        videoListInit();
    }

    private void kajianListInit() {
        kajianList.setSelectionMode(() -> SINGLE);
//        kajianList.setName("masterTbl");
        kajianList.getSelector().setSelectionsLabel("ms");
        kajianList.getPager().setPageSizeLabel("mp");
        kajianList.getPager().setOffsetLabel("mo");

        kajianList.getSelector().addListener((s) -> onSelectKajian((Kajian) s));
        kajianList.getSelector().addListenerInternal((s) -> onSelectKajian((Kajian) s));
    }

    private void videoListInit() {
        videoList.setSelectionMode(() -> SINGLE);
        videoList.setName("detailTbl");
        videoList.getSelector().setSelectionsLabel("ds");
        videoList.getFilter().setName("periodeFilter");

        videoList.setDefaultChecker(() -> kajianList.getSelected() == null);
    }

    private void onSelectKajian(Kajian kajian) {
        videoList.reset();
        videoList.getFilter().<VideoKajianFilter>getContent().setKajian(kajian);
    }

    public void saveKitab(ActionEvent evt) {
        LocalDateTime now = LocalDateTime.now();
        transientKitab.setPublishedDate(now);
        if (transientKitab.getId() == null) {
            transientKitab.setPublished(true);
            transientKitab.setPublishedDate(now);
            kitabFacade.create(transientKitab);
            selectedKitab = transientKitab;
        } else {
            kitabFacade.edit(transientKitab);
        }
        transientKitab = null;
    }

    public void onCreateKitab(ActionEvent evt) {
        Kitab kitab = new Kitab();
        kitab.setKajian(selectedKajian);
        selectedKajian.getKitab().add(kitab);

        transientKitab = kitab;
    }

    public void onEditKitab(ActionEvent evt) {
        transientKitab = selectedKitab;
    }

    public void onUnselectKitab() {
        selectedKitab = null;
    }

    public void onDeleteKitab(ActionEvent evt) {
        kitabFacade.remove(selectedKitab);
        selectedKitab = null;
    }

    public void onSelectKajian() {
        selectedKitab = null;
    }

    public void onUnselectKajian() {
        selectedKajian = null;
        selectedKitab = null;
    }

    public void handleKitabUpload(FileUploadEvent evt) {
        try {
            InputStream is = evt.getFile().getInputStream();
            handleKitabUpload(is);
        } catch (IOException ex) {
            Logger.getLogger(KitabAdminPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handleKitabUpload(InputStream is) {

        int BUFF_LEN = 1024;
        byte[] buffer = new byte[BUFF_LEN];
        int numBytesRead;

        KitabContent content = new KitabContent();

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            while ((numBytesRead = is.read(buffer, 0, BUFF_LEN)) != -1) {
                os.write(buffer, 0, numBytesRead);
            }

            content.setContent(os.toByteArray());
            content.setKitab(selectedKitab);
            content.setTitle(getSelectedKitab().getTitle());

            kitabContentFacade.create(content);

            selectedKitab = kitabFacade.find(selectedKitab.getId());
        } catch (IOException ex) {
            Logger.getLogger(KitabAdminPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isContentExists() {
        KitabContent content = kitabContentFacade.findSingleByAttribute("kitab", selectedKitab);
        return content != null;
    }

    public StreamedContent getPreviewKitab() {
        previewKitab = DefaultStreamedContent.builder()
                .contentType("application/pdf")
                .stream(() -> new ByteArrayInputStream(selectedKitab.getContent().getContent()))
                .build();

        return previewKitab;
    }

    public void clearContent() {
        kitabContentFacade.removeContent(selectedKitab);
    }

    public KajianList getKajianList() {
        return kajianList;
    }

    public VideoKajianList getVideoList() {
        return videoList;
    }

    public KitabList getKitabList() {
        return kitabList;
    }

    public Kajian getSelectedKajian() {
        return selectedKajian;
    }

    public void setSelectedKajian(Kajian selectedKajian) {
        this.selectedKajian = selectedKajian;
    }

    public Kajian getTransientKajian() {
        return transientKajian;
    }

    public void setTransientKajian(Kajian transientKajian) {
        this.transientKajian = transientKajian;
    }

    public Kitab getSelectedKitab() {
        return selectedKitab;
    }

    public void setSelectedKitab(Kitab selectedKitab) {
        this.selectedKitab = selectedKitab;
    }

    public Kitab getTransientKitab() {
        return transientKitab;
    }

    public void setTransientKitab(Kitab transientKitab) {
        this.transientKitab = transientKitab;
    }

}
