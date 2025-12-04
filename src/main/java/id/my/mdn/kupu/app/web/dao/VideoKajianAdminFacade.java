/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.model.VideoSegment;
import id.my.mdn.kupu.app.web.util.LiveUtil;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Stateless
public class VideoKajianAdminFacade extends AbstractFacade<VideoKajian> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public VideoKajianAdminFacade() {
        super(VideoKajian.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<VideoKajian> findAll(Integer startPosition, Integer maxResult, Map<String, Object> parameters, List<FilterData> filters, List<SorterData> sorters, List<VideoKajian> defaultReturn, DefaultChecker defaultChecker) {
        return super.findAll(startPosition, maxResult, parameters, filters, sorters, Collections.<VideoKajian>emptyList(), defaultChecker);
    }

    @Override
    public Long countAll(Map<String, Object> parameters, List<FilterData> filters, Long defaultCount, DefaultChecker defaultChecker) {
        return super.countAll(parameters, filters, 0L, defaultChecker);
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... from) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        return switch (filterName) {
            case "kajian" -> cb.equal(from[0].get("kajian"), filterValue);
            case "published" -> cb.isTrue(from[0].get("published"));
            case "live" -> cb.isTrue((from[0].get("live")));
            case "publicLive" -> cb.isTrue((from[0].get("publicLive")));
            default -> null;
        };
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        return switch (field) {
            case "eventTime" -> from[0].get("eventTime");
            default -> super.orderExpression(field, from);
        };
        
    }

    public void refresh(VideoKajian selectedVideoKajian) {
        VideoKajian vk = find(selectedVideoKajian.getId());
        getEntityManager().refresh(vk);
    }

    @Override
    public Result<String> remove(VideoKajian entity) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<VideoSegment> cd = cb.createCriteriaDelete(VideoSegment.class);
        Root<VideoSegment> segment = cd.from(VideoSegment.class);
        cd.where(cb.equal(segment.get("video"), entity));

        Query q = getEntityManager().createQuery(cd);

        q.executeUpdate();

        return super.remove(entity);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removeSegments(VideoKajian entity) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<VideoSegment> cd = cb.createCriteriaDelete(VideoSegment.class);
        Root<VideoSegment> segment = cd.from(VideoSegment.class);
        cd.where(cb.equal(segment.get("video"), entity));

        Query q = getEntityManager().createQuery(cd);

        q.executeUpdate();
    }

    public void toggleLive(VideoKajian selectedVideoKajian) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("live", Boolean.TRUE);
        findAll(filters).stream()
                .filter(videoKajian -> !videoKajian.equals(selectedVideoKajian))
                .filter(VideoKajian::isLive)
                .forEach(videoKajian -> {
                    videoKajian.setLive(false);
                    videoKajian.setLiveToken(null);
                    edit(videoKajian);
                });
        selectedVideoKajian.setLive(!selectedVideoKajian.isLive());
        if(selectedVideoKajian.isLive()) {
            selectedVideoKajian.setLiveToken(LiveUtil.generateToken());
        } else {
            selectedVideoKajian.setLiveToken(null);
        }
        edit(selectedVideoKajian);
    }

    public void togglePublicLive(VideoKajian selectedVideoKajian) {        
        Map<String, Object> filters = new HashMap<>();
        filters.put("publicLive", Boolean.TRUE);
        findAll(filters).stream()
                .filter(videoKajian -> !videoKajian.equals(selectedVideoKajian))
                .filter(VideoKajian::isPublicLive)
                .forEach(videoKajian -> {
                    videoKajian.setPublicLive(false);
                    edit(videoKajian);
                });
        selectedVideoKajian.setPublicLive(!selectedVideoKajian.isPublicLive());
        edit(selectedVideoKajian);
    }

}
