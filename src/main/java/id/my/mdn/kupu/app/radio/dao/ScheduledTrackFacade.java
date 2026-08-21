/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.dao;

import id.my.mdn.kupu.app.radio.model.ScheduledTrack;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author aphasan
 */
@Stateless
public class ScheduledTrackFacade extends AbstractFacade<ScheduledTrack> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ScheduledTrackFacade() {
        super(ScheduledTrack.class);
    }
    
}
