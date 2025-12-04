/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.core.party.dao;

import id.my.mdn.kupu.core.party.entity.BusinessEntity;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Stateless
public class BusinessEntityFacade extends AbstractHierarchicalPartyRoleFacade<BusinessEntity> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    @Inject
    private PartyRoleTypeFacade roleTypeFacade;

    public BusinessEntityFacade() {
        super(BusinessEntity.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected PartyRoleTypeFacade getRoleTypeFacade() {
        return roleTypeFacade;
    }

//    @Inject
//    private OrganizationFacade orgFacade;

//    @Override
//    public Result<String> create(BusinessEntity entity) {
//        Result<String> create = super.create(entity);
//        if (create.isSuccess()) {
//            Organization org = entity.getOrganization();
//
//            ApplicationSecurityGroup.builder()
//                    .module("accounting")
//                    .organization(org);
//
//            create.copy(orgFacade.create(org));
//        }
//
//        return create;
//    }

//    @Inject
//    private ApplicationUserFacade userFacade;

//    public BusinessEntity getByAppUsername(String username) {
//        ApplicationUser user = userFacade.findByUsername(username);
//        List<ApplicationSecurityGroup> groups = userFacade.getGroups(user);
//        List<BusinessEntity> entities = findAll();
//
//        for (ApplicationSecurityGroup group : groups) {
//            for (BusinessEntity entity : entities) {
//                if (entity.getOrganization().equals(group.getOrganization())) {
//                    return entity;
//                }
//            }
//        }
//
//        return null;
//
//    }

}
