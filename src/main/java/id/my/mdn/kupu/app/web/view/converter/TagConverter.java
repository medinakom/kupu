/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.TagFacade;
import id.my.mdn.kupu.app.web.model.Tag;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="TagConverter", managed=true)
public class TagConverter implements Converter<Tag> {
    
    @Inject
    private TagFacade tagFacade;

    @Override
    public Tag getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? tagFacade.find(value) : null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Tag value) {
        return value != null ? value.getId() : null;
    }
    
}
