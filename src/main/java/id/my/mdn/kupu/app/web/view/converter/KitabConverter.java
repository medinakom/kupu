/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.model.Kitab;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value = "KitabConverter", managed = true)
public class KitabConverter implements Converter<Kitab> {
    
    @Inject
    private KitabFacade dao;

    @Override
    public Kitab getAsObject(FacesContext context, UIComponent component, String value) {
        return dao.find(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Kitab value) {
        return value != null ? String.valueOf(value) : null;
    }
    
}
