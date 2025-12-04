/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Tag;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author aphasan
 */
@Stateless
public class TagFacade extends AbstractFacade<Tag> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public TagFacade() {
        super(Tag.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
