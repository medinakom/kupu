/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.Tag;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Stateless
public class KajianFacade extends ContentFacade<Kajian> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public KajianFacade() {
        super(Kajian.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        return switch (field) {
            case "publishedDate" -> from[0].get("publishedDate");
            default -> super.orderExpression(field, from);
        };
        
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... from) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch(filterName) {
            case "title":
                String searchTerm = (String) filterValue;
                return cb.like(cb.lower(from[0].get("title")), "%" + searchTerm.toLowerCase() + "%");
            case "ustadz":
                return cb.equal(from[0].get(filterName).get("id"), filterValue);
            case "contentCategory":
                return cb.equal(from[0].get(filterName).get("id"), filterValue);
            case "contentTags":
                Join<Kajian, Tag> contentTag = from[0].join("tags");                
                return contentTag.in((List<Tag>)filterValue);
            case "published":
                boolean published = (boolean) filterValue;
                return published ? cb.isTrue(from[0].get("published"))
                        : cb.isFalse(from[0].get("published"));
            default:
                return null;
        }
    }
    
    public List<Kajian> getKajianTerbaru(int limit) {
        List<FilterData> filters = new ArrayList<>();
        filters.add(FilterData.by("published", Boolean.TRUE));
        return findAll(0, limit, filters);
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

    public List<Kajian> getKajianWithKitab() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Kajian> cq = cb.createQuery(Kajian.class);
        
        Root<Kajian> kajian = cq.from(Kajian.class);
        Join<Kajian, Kitab> kitab = kajian.join("kitab");
        
        cq.select(kajian).distinct(true);
        
        TypedQuery<Kajian> q = getEntityManager().createQuery(cq);
        
        return q.getResultList();
    }

    @Override
    public Result<String> create(Kajian entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return super.create(entity);
    }

    @Override
    public Result<String> edit(Kajian entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return super.edit(entity);
    }
    
}
