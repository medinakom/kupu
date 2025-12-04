/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.core.security.dao;

import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityMap;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

/**
 *
 * @author aphasan
 */
@Stateless
public class ApplicationSecurityMapFacade extends AbstractFacade<ApplicationSecurityMap> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public ApplicationSecurityMapFacade() {
        super(ApplicationSecurityMap.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... froms) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch (filterName) {
            case "user":
                return cb.equal(froms[0].get("user"), filterValue);
            case "group":
                return cb.equal(froms[0].get("group"), filterValue);
            case "mainGroup":
                return cb.equal(froms[0].get("mainGroup"), filterValue);
            default:
                return super.applyFilter(filterName, filterValue, cq, froms);
        }
    }

}
