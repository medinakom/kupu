/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.KitabContent;
import id.my.mdn.kupu.app.web.service.KajianService;
import id.my.mdn.kupu.app.web.view.list.KitabList;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import static id.my.mdn.kupu.core.base.view.widget.Selector.SINGLE;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
public class KitabAdminPage extends ChildPage implements Serializable {

    @Bookmarked
    @Inject
    private KitabList kitabList;

    @Bookmarked
    private Kajian kajian;

    private StreamedContent previewKitab;
    
    @Inject
    private KajianService kajianService;

    @PostConstruct
    @Override
    public void init() {
        super.init();
        kitabList.setSelectionMode(() -> SINGLE);
        kitabList.getSelector().setSelectionsLabel("ms");
        kitabList.getPager().setPageSizeLabel("mp");
        kitabList.getPager().setOffsetLabel("mo");

        kitabList.getSelector().addListener(s -> onSelectKitab((Kitab) s));
        kitabList.getSelector().addListenerInternal(s -> onSelectKitab((Kitab) s));
        
        kitabList.getFilter().setStaticFilter(this::kitabFilter);
    }
    
    public List<FilterData> kitabFilter() {
        List<FilterData> filters = new ArrayList<>();
        filters.add(FilterData.by("kajian", kajian));
        
        return filters;
    }

    private void onSelectKitab(Kitab kajian) {

    }

    @Creator(of = "kitabList")
    public void onCreateKitab() {
        gotoChild(KitabEditorPage.class)
                .addParam("kajian")
                .withValues(kajian)
                .open();
    }

    @Editor(of = "kitabList")
    public void onEditKitab() {
        gotoChild(KitabEditorPage.class)
                .addParam("entity")
                .withValues(kitabList.getSelected())
                .addParam("kajian")
                .withValues(kajian)
                .open();
    }

    @Deleter(of = "kitabList")
    public void onDeleteKitab() {
        kitabList.deleteSelected();
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

            kajianService.updateKitabContent(kitabList.getSelected(), os.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(KitabAdminPage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public boolean isContentExists() {
//        KitabContent content = kitabContentFacade.findSingleByAttribute("kitab", selectedKitab);
//        return content != null;
//    }
    public StreamedContent getPreviewKitab() {
        previewKitab = DefaultStreamedContent.builder()
                .contentType("application/pdf")
                .stream(() -> new ByteArrayInputStream(kitabList.getSelected().getContent().getContent()))
                .build();

        return previewKitab;
    }

    public KitabList getKitabList() {
        return kitabList;
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

}
