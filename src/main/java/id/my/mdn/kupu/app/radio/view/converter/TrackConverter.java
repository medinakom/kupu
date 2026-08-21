/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view.converter;

import id.my.mdn.kupu.app.radio.dao.TrackFacade;
import id.my.mdn.kupu.app.radio.model.Track;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="TrackConverter", managed=true)
public class TrackConverter implements Converter<Track> {
    
    @Inject
    private TrackFacade dao;

    @Override
    public Track getAsObject(FacesContext context, UIComponent component, String value) {
        return value != null ? dao.find(Long.valueOf(value)) : null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Track value) {
        return value != null ? String.valueOf(value.getId()) : null;
    }
    
}
