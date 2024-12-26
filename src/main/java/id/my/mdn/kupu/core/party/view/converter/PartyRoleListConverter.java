/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.party.view.converter;

import id.my.mdn.kupu.core.base.view.converter.SelectionsConverter;
import id.my.mdn.kupu.core.party.dao.PartyRoleFacade;
import id.my.mdn.kupu.core.party.entity.PartyRole;
import jakarta.faces.convert.FacesConverter;
import id.my.mdn.kupu.core.common.util.K.KLong;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 *
 * @author aphasan
 */
@Singleton
@FacesConverter(value = "PartyRoleListConverter", managed = true)
public class PartyRoleListConverter extends SelectionsConverter<PartyRole> {
    
    @Inject
    private PartyRoleFacade service;

    @Override
    public PartyRole getAsObject(String value) {
        return service.find(KLong.valueOf(value));
    }

    @Override
    public String getAsString(PartyRole value) {
        return value != null ? value.toString() : null;
    }
    
}
