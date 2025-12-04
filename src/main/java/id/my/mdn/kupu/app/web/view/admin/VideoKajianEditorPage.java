/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;

/**
 *
 * @author aphasan
 */
@Named(value = "videoKajianEditorPage")
@ConversationScoped
public class VideoKajianEditorPage extends FormPage<VideoKajian> {
    
    @Inject
    private VideoKajianFacade dao;
    
    private Kajian kajian;

    @Override
    protected VideoKajian newEntity() {
        VideoKajian videoKajian = new VideoKajian();
        videoKajian.setKajian(kajian);
        
        return videoKajian;
    }

    @Override
    protected Result<String> save(VideoKajian entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return dao.create(entity);
    }

    @Override
    protected Result<String> edit(VideoKajian entity) {
        return dao.edit(entity);
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

    
    
}
