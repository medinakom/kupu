/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.TagFacade;
import id.my.mdn.kupu.app.web.model.Tag;
import id.my.mdn.kupu.core.base.view.widget.IValueList;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Named(value = "tagSelectList")
@RequestScoped
public class TagSelectList implements IValueList<Tag> {
    
    @Inject
    private TagFacade dao;

    @Override
    public List<Tag> getFetchedItems() {
        return dao.findAll();
    }

    
    
}
