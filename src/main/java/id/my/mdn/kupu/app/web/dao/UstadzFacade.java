/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author aphasan
 */
@Stateless
public class UstadzFacade extends AbstractFacade<Ustadz> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public UstadzFacade() {
        super(Ustadz.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
