/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDateTime;

/**
 *
 * @author aphasan
 */
@Named(value = "kitabEditorPage")
@ConversationScoped
public class KitabEditorPage extends FormPage<Kitab> {
    
    @Inject
    private KitabFacade dao; 
    
    @Bookmarked
    private Kajian kajian;

    @Override
    protected Kitab newEntity() {
        Kitab kitab = new Kitab();
        kitab.setKajian(kajian);
        
        return kitab;
    }

    @Override
    protected Result<String> save(Kitab entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return dao.create(entity);
    }

    @Override
    protected Result<String> edit(Kitab entity) {
        return dao.edit(entity);
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

    
    
}
