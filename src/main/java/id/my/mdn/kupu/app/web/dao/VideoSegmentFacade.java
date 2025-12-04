/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.model.VideoSegment;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;

/**
 *
 * @author aphasan
 */
@Stateless
public class VideoSegmentFacade extends AbstractFacade<VideoSegment> {    
    
    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public VideoSegmentFacade() {
        super(VideoSegment.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int removeVideo(VideoKajian videoKajian) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<VideoSegment> cd = cb.createCriteriaDelete(entityClass);
        Root<VideoSegment> segment = cd.from(entityClass);
        cd.where(cb.equal(segment.get("video").get("id"), videoKajian.getId()));

        Query q = getEntityManager().createQuery(cd);
        return q.executeUpdate();
    }
    
}
