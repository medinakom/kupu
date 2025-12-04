/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.ContentCategoryFacade;
import id.my.mdn.kupu.app.web.model.ContentCategory;
import id.my.mdn.kupu.core.base.view.widget.IValueList;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Named(value = "contentCategoryList")
@RequestScoped
public class ContentCategoryList implements IValueList<ContentCategory> {
    
    @Inject
    private ContentCategoryFacade dao;

    @Override
    public List<ContentCategory> getFetchedItems() {
        return dao.findAll();
    }

    
    
}
