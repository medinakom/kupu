/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.ContentCategory;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;

/**
 *
 * @author aphasan
 */
@Stateless
public class ContentCategoryFacade extends AbstractFacade<ContentCategory> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public ContentCategoryFacade() {
        super(ContentCategory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        return switch (field) {
            case "position" -> from[0].get("position");
            default -> super.orderExpression(field, from);
        };
        
    }
    
}
