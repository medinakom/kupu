/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author aphasan
 */
@Named(value = "kajianEditorPage")
@ConversationScoped
public class KajianEditorPage extends FormPage<Kajian> {
    
    @Inject
    private KajianFacade dao; 

    @Override
    protected Kajian newEntity() {
        return new Kajian();
    }

    @Override
    protected Result<String> save(Kajian entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> edit(Kajian entity) {
        return dao.edit(entity);
    }

    
    
}
