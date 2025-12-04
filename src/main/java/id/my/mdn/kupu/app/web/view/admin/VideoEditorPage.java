/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.service.AudioUploader;
import id.my.mdn.kupu.app.web.service.FileUploader;
import id.my.mdn.kupu.app.web.service.MediaService;
import id.my.mdn.kupu.app.web.service.MediaService.ProcessMessage;
import id.my.mdn.kupu.app.web.service.VideoUploader;
import id.my.mdn.kupu.app.web.service.YoutubeUploader;
import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmark;
import id.my.mdn.kupu.core.base.view.widget.Toast;
import static id.my.mdn.kupu.core.base.view.widget.Toast.KEEP_ALIVE;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author aphasan
 */
@Named(value = "videoEditorPage")
@ViewScoped
public class VideoEditorPage extends ChildPage implements Serializable {

    @Inject
    private MediaService mediaService;

    @Inject
    private FileUploader fileUploader;

    @Inject
    private YoutubeUploader ytUploader;

    @Inject
    private VideoUploader videoUploader;

    @Inject
    private AudioUploader audioUploader;

    @Bookmark(name = "v")
    private VideoKajian videoKajian;

    private String youtubeUrl;

    private String internalPath;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void load() {
        if (mediaService.isProcessingVideo(videoKajian)) {
            PrimeFaces.current().executeScript("PF('vPoll').start()");
        }
        if (mediaService.isProcessingAudio(videoKajian)) {
            PrimeFaces.current().executeScript("PF('aPoll').start()");
        }
    }
    
    public void test(ActionEvent evt) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TEST"));
    }

    public void handleFileUpload(FileUploadEvent evt) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("TEST"));
        try {

            InputStream is = evt.getFile().getInputStream();

            fileUploader.process(getVideoKajian(), is);

            PrimeFaces.current().executeScript("PF('vPoll').start()");
            PrimeFaces.current().executeScript("PF('aPoll').start()");

        } catch (IOException ex) {
            System.getLogger(VideoEditorPage.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void handleYtUpload(ActionEvent url) {

        if (youtubeUrl == null || youtubeUrl.isBlank()) {
            return;
        }

        ytUploader.process(getVideoKajian(), youtubeUrl);

        PrimeFaces.current().executeScript("PF('vPoll').start()");
        PrimeFaces.current().executeScript("PF('aPoll').start()");
    }

    public void handleInternalUpload(ActionEvent url) {

        try {

            InputStream is = new FileInputStream(internalPath);

            fileUploader.process(getVideoKajian(), is);

            PrimeFaces.current().executeScript("PF('vPoll').start()");
            PrimeFaces.current().executeScript("PF('aPoll').start()");

        } catch (IOException ex) {
            System.getLogger(VideoEditorPage.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void generateVideo(ActionEvent evt) {
        videoUploader.process(getVideoKajian(), null);
        PrimeFaces.current().executeScript("PF('vPoll').start()");
    }

    public void generateAudio(ActionEvent evt) {
        audioUploader.process(getVideoKajian(), null);
        PrimeFaces.current().executeScript("PF('aPoll').start()");
    }

    public void clearAudio() {
        audioUploader.clearAudio(getVideoKajian());
        PrimeFaces.current().executeScript("PF('aPoll').start()");
    }

    public void clearVideo() {
        videoUploader.clearVideo(getVideoKajian());
        PrimeFaces.current().executeScript("PF('vPoll').start()");
    }

    public void checkVideoCompleteness() {

        if (mediaService.isProcessingVideo(videoKajian)) {
            PrimeFaces.current().executeScript("PF('vBlocker').show()");
        } else {
            PrimeFaces.current().executeScript("PF('vPoll').stop()");
            PrimeFaces.current().executeScript("PF('vBlocker').hide()");
            PrimeFaces.current().executeScript("reloadVideo()");
        }
    }

    public void checkAudioCompleteness() {

        if (mediaService.isProcessingAudio(videoKajian)) {
            ProcessMessage message = mediaService.popAudioMessage(videoKajian);
            if (message != null) {
                switch (message.type) {
                    case ProcessMessage.INFO ->
                        Toast.info(message.message, KEEP_ALIVE);
                    case ProcessMessage.WARNING ->
                        Toast.warning(message.message, KEEP_ALIVE);
                    case ProcessMessage.ERROR ->
                        Toast.error(message.message, KEEP_ALIVE);
                }
            }
            PrimeFaces.current().executeScript("PF('aBlocker').show()");
        } else {
            PrimeFaces.current().executeScript("PF('aPoll').stop()");
            PrimeFaces.current().executeScript("PF('aBlocker').hide()");
            PrimeFaces.current().executeScript("reloadAudio()");
        }
    }

    public VideoKajian getVideoKajian() {
        return videoKajian;
    }

    public void setVideoKajian(VideoKajian videoKajian) {
        this.videoKajian = videoKajian;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getInternalPath() {
        return internalPath;
    }

    public void setInternalPath(String internalPath) {
        this.internalPath = internalPath;
    }

}
