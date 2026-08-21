/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.core.base.view.converter.SelectionsConverter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value="UstadzListConverter", managed=true)
public class UstadzListConverter extends SelectionsConverter<Ustadz> {
    
    @Inject
    private UstadzFacade dao;

    @Override
    public Ustadz getAsObject(String value) {
        return value != null ? dao.find(Long.valueOf(value)) : null;
    }

    @Override
    public String getAsString(Ustadz value) {
        return value != null ? String.valueOf(value.getId()) : null;
    }
    
}
