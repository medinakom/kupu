/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.model.Ustadz;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="UstadzConverter", managed=true)
public class UstadzConverter implements Converter<Ustadz> {
    
    @Inject
    private UstadzFacade dao;

    @Override
    public Ustadz getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? dao.find(Long.parseLong(value)) : null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Ustadz value) {
        return value != null ? String.valueOf(value.getId()) : null;
    }
    
}
