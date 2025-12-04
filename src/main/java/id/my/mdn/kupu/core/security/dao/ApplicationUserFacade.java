/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.security.dao;

import id.my.mdn.kupu.core.base.util.FilterTypes;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.party.dao.AbstractPartyRoleFacade;
import id.my.mdn.kupu.core.party.dao.PartyRoleTypeFacade;
import id.my.mdn.kupu.core.party.entity.Person;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityMap;
import id.my.mdn.kupu.core.security.model.ApplicationUser;
import id.my.mdn.kupu.core.security.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan at medinacom.id>
 */
@Stateless
public class ApplicationUserFacade extends AbstractPartyRoleFacade<ApplicationUser> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    @Inject
    private PartyRoleTypeFacade roleTypeFacade;

    @Inject
    ApplicationSecurityGroupFacade groupFacade;

    public ApplicationUserFacade() {
        super(ApplicationUser.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected PartyRoleTypeFacade getRoleTypeFacade() {
        return roleTypeFacade;
    }

    @Override
    protected From[] selectFind(CriteriaQuery cq, Map<String, Object> parameters) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<ApplicationUser> role = cq.from(entityClass);
        Join<ApplicationUser, Person> party = cb.treat(
                role.join("party"), Person.class);

        cq.select(role);

        return new From[]{role, party};
    }

    @Override
    protected Expression orderExpression(String field, From... from) {
        switch (field) {
            case "id":
                return from[0].get("id");
            default:
                return super.orderExpression(field, from);
        }
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue,
            CriteriaQuery cq, From... from) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch (filterName) {
            case "username":
                return cb.equal(from[0].get("username"), (String) filterValue);
            case "password":
                return cb.equal(from[0].get("password"), PasswordUtil.generate((String) filterValue));
            default:
                return super.applyFilter(filterName, filterValue, cq, from);
        }
    }

    @Override
    public Result<String> create(ApplicationUser entity) {
        entity.setPassword(PasswordUtil.generate(entity.getPassword()));
        ApplicationSecurityGroup userGroup = groupFacade.findSingleByAttributes(
                FilterTypes.FilterData.by("module", "security"),
                FilterTypes.FilterData.by("groupname", "User")
        );
        ApplicationSecurityMap securityMap = new ApplicationSecurityMap();
        securityMap.setUser(entity);
        securityMap.setGroup(userGroup);
        entity.getGroups().add(securityMap);
        return super.create(entity);
    }

    public Result<String> create(Person person, String username, String password, ApplicationSecurityGroup group) {
        ApplicationUser user = ApplicationUser.builder()
                .addToGroup(group)
                .withParty(person)
                .withUsername(username)
                .withPassword(password)
                .get();
        return create(user);
    }
}
