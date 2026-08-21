/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view.converter;

import id.my.mdn.kupu.app.radio.dao.TrackFacade;
import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.core.base.view.converter.SelectionsConverter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="TrackListConverter", managed=true)
public class TrackListConverter extends SelectionsConverter<Track> {
    
    @Inject
    private TrackFacade dao;

    @Override
    public Track getAsObject(String value) {
        return value != null ? dao.find(Long.valueOf(value)) : null;
    }

    @Override
    public String getAsString(Track value) {
        return value != null ? String.valueOf(value.getId()) : null;
    }
    
}
