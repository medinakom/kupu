/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.party.view.converter;

import id.my.mdn.kupu.core.base.util.K.KLong;
import id.my.mdn.kupu.core.party.dao.PartyRoleFacade;
import id.my.mdn.kupu.core.party.entity.PartyRole;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Dependent
@FacesConverter(managed = true, value = "PartyRoleConverter")
public class PartyRoleConverter implements Converter<PartyRole> {
    
    @Inject
    private PartyRoleFacade dao;

    @Override
    public PartyRole getAsObject(FacesContext context, UIComponent component, String value) { 
        PartyRole find = dao.find(KLong.valueOf(value));      
        
        return find;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, PartyRole value) {
        return value != null ? value.toString() : null;
    }   
    
}
