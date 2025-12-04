/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.ContentCategoryFacade;
import id.my.mdn.kupu.app.web.model.ContentCategory;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value = "ContentCategoryConverter", managed = true)
public class ContentCategoryConverter implements Converter<ContentCategory>{
    
    @Inject
    private ContentCategoryFacade facade;

    @Override
    public ContentCategory getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? facade.find(value) : null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ContentCategory value) {
        return value != null ? value.getId() : null;
    }
    
}
