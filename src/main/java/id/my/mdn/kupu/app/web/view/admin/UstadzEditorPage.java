/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author aphasan
 */
@Named(value = "ustadzEditorPage")
@ConversationScoped
public class UstadzEditorPage extends FormPage<Ustadz> {
    
    @Inject
    private UstadzFacade dao; 

    @Override
    protected Ustadz newEntity() {
        return new Ustadz();
    }

    @Override
    protected Result<String> save(Ustadz entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> edit(Ustadz entity) {
        return dao.edit(entity);
    }

    
    
}
