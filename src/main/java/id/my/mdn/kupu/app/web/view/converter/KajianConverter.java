/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="KajianConverter", managed=true)
public class KajianConverter implements Converter<Kajian> {
    
    @Inject
    private KajianFacade dao;

    @Override
    public Kajian getAsObject(FacesContext context, UIComponent component, String value) {
        return dao.find(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Kajian value) {
        return String.valueOf(value);
    }
    
}
