/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Stateless
public class KitabFacade extends ContentFacade<Kitab> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public KitabFacade() {
        super(Kitab.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<Kitab> findAll(Integer startPosition, Integer maxResult, List<FilterData> filters) {
        return super.findAll(startPosition, maxResult, null,  filters, null, Collections.<Kitab>emptyList());
    }

    @Override
    public Long countAll(List<FilterData> filters) {
        return super.countAll(filters, 0L);
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
        switch (filterName) {
            case "kajian":
                return cb.equal(from[0].get(filterName).get("id"), filterValue);
            default:
                return null;
        }
    }

}
