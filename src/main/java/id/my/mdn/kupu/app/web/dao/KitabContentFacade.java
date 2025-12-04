/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.KitabContent;
import id.my.mdn.kupu.core.base.util.Result;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 *
 * @author aphasan
 */
@Stateless
public class KitabContentFacade extends ContentFacade<KitabContent> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public KitabContentFacade() {
        super(KitabContent.class);
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
        return switch (filterName) {
            case "kitab" -> cb.equal(from[0].get(filterName).get("id"), filterValue);
            default -> null;
        };
    }

    @Inject
    private KitabFacade kitabFacade;

    public void removeContent(Kitab selectedKitab) {

        Kitab kitab = kitabFacade.find(selectedKitab.getId());

        KitabContent content = kitab.getContent();
        content.setKitab(null);

        remove(content);

        kitab.setContent(null);

        kitabFacade.edit(kitab);
        getEntityManager().flush();
        getEntityManager().refresh(kitab);
    }

    public byte[] getContent(String id) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<KitabContent> root = cq.from(KitabContent.class);

        cq.select(root.get("content")).where(cb.equal(root.get("id"), id));

        Query q = getEntityManager().createQuery(cq); 

        try {
            return (byte[]) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Result<String> create(KitabContent newContent) {
        Kitab kitab = kitabFacade.find(newContent.getKitab().getId());
        KitabContent content = findSingleByAttribute("kitab", kitab);
        
        if(content != null) {
            content.setKitab(null);
            kitab.setContent(null);
            remove(content);
            kitabFacade.edit(kitab);
            getEntityManager().flush();
            getEntityManager().refresh(kitab);
        }
        
        kitab.setContent(newContent);
        return kitabFacade.edit(kitab);
    }

}
