/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Tag;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Stateless
public class KajianAdminFacade extends ContentFacade<Kajian> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public KajianAdminFacade() {
        super(Kajian.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        return switch (field) {
            case "created" -> from[0].get("created");
            default -> super.orderExpression(field, from);
        };
        
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... from) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch(filterName) {
            case "contentCategory":
                return cb.equal(from[0].get(filterName).get("id"), filterValue);
            case "contentTags":
                Join<Kajian, Tag> contentTag = from[0].join("tags");                
                return contentTag.in((List<Tag>)filterValue);
            default:
                return null;
        }
    }

    @Override
    protected List<Predicate> addStaticPredicates(List<Predicate> predicates, CriteriaQuery cq, From... from) {
        return predicates;
    }
    
    public List<Kajian> getKajianTerbaru(int limit) {
        return findAll(0, limit, null);
    } 

    public VideoKajian getFirstVideo(Kajian kajian) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VideoKajian> cq = cb.createQuery(VideoKajian.class);
        Root<Kajian> kajians = cq.from(Kajian.class);
        Join<Kajian, VideoKajian> videos = kajians.join("videos");
        
        Subquery<LocalDateTime> sq = cq.subquery(LocalDateTime.class);
        Root<VideoKajian> sqVideo = sq.from(VideoKajian.class);
        sq.select(cb.least(sqVideo.<LocalDateTime>get("eventTime"))).where(cb.equal(sqVideo.get("kajian"), kajian));
        
        cq.select(videos).where(
                cb.equal(kajians, kajian),
                cb.isTrue(kajians.get("published")),
                cb.equal(videos.get("eventTime"), sq)
        );
        
        TypedQuery<VideoKajian> q = getEntityManager().createQuery(cq);
        
        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
        
    }
    
}
