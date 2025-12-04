/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.security.dao;

import id.my.mdn.kupu.core.base.dao.AbstractSqlFacade;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import id.my.mdn.kupu.core.security.model.ApplicationUser;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan at medinacom.id>
 */
@Stateless
public class ApplicationSecurityGroupSqlFacade extends AbstractSqlFacade<ApplicationSecurityGroup> {

    private static final String FINDALL
            = """
            SELECT ID, ORGID, GROUPNAME, ACTIVE, MAINGROUP
            FROM (
                SELECT GRP1.ID AS ID, GRP1.ORG_ID AS ORGID, GRP1.GROUPNAME AS GROUPNAME, (USR1.USER_ID IS NOT NULL) AS ACTIVE, USR1.MAINGROUP
                FROM (
                     SELECT GRP0.ID, ORG.ID AS ORG_ID, ORG.FIRSTNAME AS GROUPNAME
                     FROM (SELECT GRP.ID, PR.PARTY_ID
                             FROM SECURITY_APPLICATIONSECURITYGROUP AS GRP
                             JOIN PARTY_PARTYROLE AS PR ON GRP.ID = PR.ID
                     ) AS GRP0
                     JOIN PARTY_PARTY AS ORG ON GRP0.PARTY_ID = ORG.ID
                ) AS GRP1
                LEFT JOIN (
                    SELECT USER_ID, GROUP_ID, MAINGROUP
                    FROM SECURITY_APPLICATIONUSER AS USR0
                    JOIN SECURITY_APPLICATIONSECURITYMAP AS MAP0
                    ON USR0.ID = MAP0.USER_ID
                    WHERE USR0.ID = ?
                ) AS USR1
                ON GRP1.ID = USR1.GROUP_ID
            ) AS GRP2
            """;

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ApplicationSecurityGroupSqlFacade() {
        super(ApplicationSecurityGroup.class);
    }

    @Override
    protected String getFindAllQuery(Map<String, Object> parameters) {
        return FINDALL;
    }

    @Override
    protected void setParameters(Query q, Map<String, Object> parameters) {
        ApplicationUser user = (ApplicationUser) parameters.get("user");
        q.setParameter(1, user.getId());
    }

}
