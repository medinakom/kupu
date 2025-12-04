/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.core.security.dao;

import id.my.mdn.kupu.core.base.dao.AbstractSqlFacade;
import id.my.mdn.kupu.core.security.model.AccessControl;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Stateless
public class GroupAccessControlSqlFacade extends AbstractSqlFacade<AccessControl> {

    private static final String FIND_ALL
            = """
            SELECT ID, MODULE_NAME, NAME, DESCRIPTION, ACTIVE
            FROM (
                SELECT AC0.ID, AC0.MODULE_NAME, AC0.NAME, AC0.DESCRIPTION, (GAC1.ACCESSCONTROL_ID IS NOT NULL) AS ACTIVE
                FROM SECURITY_ACCESSCONTROL AS AC0
                LEFT JOIN (
                    SELECT GAC0.ACCESSCONTROL_ID
                    FROM SECURITY_GROUPACCESSCONTROL AS GAC0
                    WHERE GAC0.SECURITYGROUP_ID = ?
                ) AS GAC1
                ON AC0.ID = GAC1.ACCESSCONTROL_ID
            ) AS AC1
              """;

    private static final String FIND
            = """
            SELECT AC0.ID, AC0.MODULE_NAME, AC0.NAME, AC0.DESCRIPTION, (GAC0.ACCESSCONTROL_ID IS NOT NULL) AS ACTIVE
            FROM SECURITY_ACCESSCONTROL AS AC0
            LEFT JOIN  SECURITY_GROUPACCESSCONTROL AS GAC0
            ON AC0.ID = GAC0.ACCESSCONTROL_ID
            WHERE AC0.ID = ?
            """;

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public GroupAccessControlSqlFacade() {
        super(AccessControl.class);
    }

    @Override
    protected String getFindAllQuery(Map<String, Object> parameters) {
        return FIND_ALL;
    }

    @Override
    protected String getFindQuery() {
        return FIND;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected void setParameters(Query q, Map<String, Object> parameters) {
        q.setParameter(1, ((ApplicationSecurityGroup) parameters.get("securityGroup")).getId());
    }

    @Override
    protected String translateOrderField(String fieldName) {
        switch (fieldName) {
            case "id":
                return "ID";
            default:
                return null;
        }
    }

}
