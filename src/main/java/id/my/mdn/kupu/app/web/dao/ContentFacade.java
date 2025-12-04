/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.Content;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;

/**
 *
 * @author aphasan
 * @param <T>
 */
public abstract class ContentFacade<T extends Content> extends AbstractFacade<T> {
    
    public ContentFacade(Class<T> entityClass) {
        super(entityClass);
    }

//    @Override
//    protected List<Predicate> addStaticPredicates(List<Predicate> predicates, CriteriaQuery cq, From... from) {
//        predicates.add(getEntityManager().getCriteriaBuilder().isTrue(from[0].<Boolean>get("published")));
//        return predicates;
//    }
    
}
