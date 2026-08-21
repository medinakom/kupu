/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Stateless
public class VideoKajianFacade extends AbstractFacade<VideoKajian> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public VideoKajianFacade() {
        super(VideoKajian.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<VideoKajian> findAll(Integer startPosition, Integer maxResult,
            Map<String, Object> parameters, List<FilterTypes.FilterData> filters,
            List<SorterData> sorters, List<VideoKajian> defaultReturn,
            DefaultChecker defaultChecker) {
        return super.findAll(startPosition, maxResult, parameters, filters, sorters, Collections.<VideoKajian>emptyList(), defaultChecker);
    }

    @Override
    public Long countAll(Map<String, Object> parameters, List<FilterTypes.FilterData> filters, Long defaultCount, DefaultChecker defaultChecker) {
        return super.countAll(parameters, filters, 0L, defaultChecker);
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... from) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch (filterName) {
            case "kajian":
                return filterValue != null ? cb.equal(from[0].get("kajian"), filterValue)
                        : cb.isNull(from[0].get("kajian"));
            case "newest":
                Subquery<LocalDateTime> sq = cq.subquery(LocalDateTime.class);
                Root<VideoKajian> sqRoot = sq.from(VideoKajian.class);
                sq.select(cb.greatest(sqRoot.<LocalDateTime>get("publishedDate")));
                return cb.equal(from[0].get("publishedDate"), sq);
            case "published":
                boolean published = (boolean) filterValue;
                return published ? cb.isTrue(from[0].get("published"))
                        : cb.isFalse(from[0].get("published"));
            case "live":
                return cb.isTrue((from[0].get("live")));
            default:
                return null;
        }
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        return switch (field) {
            case "eventTime" ->
                from[0].get("eventTime");
            default ->
                super.orderExpression(field, from);
        };

    }

    public VideoKajian findNewestVideo() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("newest", true);
        filters.put("published", true);
        List<VideoKajian> ret = findAll(filters);
        if (ret.isEmpty()) {
            return null;
        } else {
            return ret.get(0);
        }
    }

    public List<VideoKajian> findNewestVideos() {
        List<FilterData> filters = new ArrayList<>();
        filters.add(FilterData.by("published", true));
        return findAll(0, 10, filters);
    }

    public boolean checkLiveTokenValidity(String liveToken) {

        VideoKajian videoKajian = findSingleByAttribute("liveToken", liveToken);

        if (videoKajian != null) {
            return true;
        } else {
            return false;
        }
    }

    public VideoKajian getLive() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VideoKajian> cq = cb.createQuery(VideoKajian.class);

        Root<VideoKajian> video = cq.from(VideoKajian.class);

        cq.select(video).where(cb.isTrue(video.get("live")));

        TypedQuery<VideoKajian> q = getEntityManager().createQuery(cq);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public VideoKajian getPublicLive() {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VideoKajian> cq = cb.createQuery(VideoKajian.class);

        Root<VideoKajian> video = cq.from(VideoKajian.class);

        cq.select(video).where(cb.isTrue(video.get("publicLive")));

        TypedQuery<VideoKajian> q = getEntityManager().createQuery(cq);

        try {
            return q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public void incVisitorCount(String videoId) {
        VideoKajian video = find(videoId);
        if (video != null) {
            video.setVisitorCount(video.getVisitorCount() + 1);
        }
        edit(video);
        em.flush();
    }

    @Override
    public Result<String> create(VideoKajian entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return super.create(entity);
    }

    @Override
    public Result<String> edit(VideoKajian entity) {
        if (entity.isPublished()) {
            entity.setPublishedDate(LocalDateTime.now());
        } else {
            entity.setPublishedDate(null);
        }
        return super.edit(entity);
    }

}
