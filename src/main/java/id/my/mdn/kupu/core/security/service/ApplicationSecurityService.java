/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package id.my.mdn.kupu.core.security.service;

import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.party.entity.Person;
import id.my.mdn.kupu.core.security.dao.AccessControlFacade;
import id.my.mdn.kupu.core.security.dao.ApplicationSecurityGroupFacade;
import id.my.mdn.kupu.core.security.dao.ApplicationSecurityMapFacade;
import id.my.mdn.kupu.core.security.dao.ApplicationUserFacade;
import id.my.mdn.kupu.core.security.dao.GroupAccessControlFacade;
import id.my.mdn.kupu.core.security.model.AccessControl;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityMap;
import id.my.mdn.kupu.core.security.model.ApplicationUser;
import id.my.mdn.kupu.core.security.model.GroupAccessControl;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author aphasan
 */
@Stateless
public class ApplicationSecurityService {

    @Inject
    private ApplicationUserFacade userFacade;

    @Inject
    private AccessControlFacade aclFacade;

    @Inject
    private ApplicationSecurityGroupFacade groupFacade;

    @Inject
    private ApplicationSecurityMapFacade securityMapFacade;

    @Inject
    private GroupAccessControlFacade groupAccessControlFacade;

    public void addGroupMembership(ApplicationUser user, ApplicationSecurityGroup group) {
        user = userFacade.find(user.getId());

        ApplicationSecurityMap map = findGroupMembership(user, group);

        if (map == null) {
            map = new ApplicationSecurityMap();
            map.setGroup(group);
            map.setUser(user);

            user.getGroups().add(map);

            userFacade.edit(user);

        }
    }

    public void removeGroupMembership(ApplicationUser user, ApplicationSecurityGroup group) {
        user = userFacade.find(user.getId());

        ApplicationSecurityMap securityMap = findGroupMembership(user, group);

        user.getGroups().remove(securityMap);

        securityMapFacade.remove(securityMap);
        userFacade.edit(user);
    }

    public void setDefaultGroupMembership(ApplicationUser user, ApplicationSecurityGroup group) {
        ApplicationSecurityMap securityMap = findGroupMembership(user, group);
        
        ApplicationSecurityMap defaultSecurityMap = findDefaultGroupMembership(user);
        if (defaultSecurityMap != null) {
            defaultSecurityMap.setMainGroup(false);
            securityMapFacade.edit(defaultSecurityMap);
        }
        
        securityMap.setMainGroup(true);

        securityMapFacade.edit(securityMap);
    }

    public void unsetDefaultGroupMembership(ApplicationUser user, ApplicationSecurityGroup group) {
        ApplicationSecurityMap securityMap = findGroupMembership(user, group);
        securityMap.setMainGroup(false);
        securityMapFacade.edit(securityMap);
    }

    public ApplicationSecurityMap findGroupMembership(ApplicationUser user, ApplicationSecurityGroup group) {
        return securityMapFacade.findSingleByAttributes(
                FilterData.by("group", group),
                FilterData.by("user", user)
        );
    }

    public ApplicationSecurityMap findDefaultGroupMembership(ApplicationUser user) {
        return securityMapFacade.findSingleByAttributes(
                FilterData.by("user", user),
                FilterData.by("mainGroup", true)
        );
    }

    public void openAccess(ApplicationSecurityGroup group, AccessControl acl) {
        acl = aclFacade.find(acl.getId());
        group = groupFacade.find(group.getId());

        GroupAccessControl gac = findAccessControl(group, acl);

        if (gac == null) {
            gac = new GroupAccessControl();

            gac.setSecurityGroup(group);
            group.getAccessControls().add(gac);

            gac.setAccessControl(acl);
            acl.getApplyingGroups().add(gac);

            groupAccessControlFacade.create(gac);
        }
    }

    public void closeAccess(ApplicationSecurityGroup group, AccessControl acl) {

        GroupAccessControl gac = findAccessControl(group, acl);

        group.getAccessControls().remove(gac);
//        acl.getApplyingGroups().remove(gac);

        groupAccessControlFacade.remove(gac);
    }

    public void openAccess(ApplicationSecurityGroup group, List<AccessControl> acls) {
        acls.stream().forEach(acl -> ApplicationSecurityService.this.openAccess(group, acl));
    }

    public GroupAccessControl findAccessControl(ApplicationSecurityGroup group, AccessControl acl) {

        return groupAccessControlFacade
                .findSingleByAttributes(
                        FilterData.by("securityGroup", group),
                        FilterData.by("accessControl", acl)
                );
    }

    public ApplicationUser findUserByUsername(String username) {
        return userFacade.findSingleByAttribute("username", username);
    }

    public ApplicationUser findUserByUsernameAndPassword(String username, String password) {
        return userFacade.findSingleByAttributes(
                FilterData.by("username", username),
                FilterData.by("password", password)
        );
    }

    public List<ApplicationSecurityMap> getSecurityMap(ApplicationUser user) {

        return securityMapFacade.findAll(FilterData.by("user", user))
                .stream().collect(Collectors.toList());
    }

    public List<ApplicationSecurityGroup> getUserGroups(ApplicationUser user) {

        return securityMapFacade.findAll(FilterData.by("user", user))
                .stream().map(e -> e.getGroup()).collect(Collectors.toList());
    }

    private List<AccessControl> getAccessControl(ApplicationSecurityGroup group) {

        return groupAccessControlFacade
                .findAll(
                        FilterData.by("securityGroup", group))
                .stream()
                .map(e -> e.getAccessControl())
                .collect(Collectors.toList());
    }

    public Set<String> getUserGroupsAsString(ApplicationUser user) {

        Set<String> result = new HashSet<>();
        getUserGroups(user).forEach(group -> {
            getAccessControl(group).stream()
                    .map(acl -> acl.getName())
                    .forEach(name -> result.add(name));
        });
        return result;
    }

    public boolean isUsernameAlreadyExist(String username) {
        List<ApplicationUser> findAll = userFacade.findAll(FilterData.by("username", username));
        return !findAll.isEmpty();
    }

    public boolean removeLogin(Person person) {
        ApplicationUser appUser = userFacade.findByParty(person);
        if (appUser != null) {
            userFacade.remove(appUser);
            return true;
        }
        return false;
    }

    public ApplicationSecurityGroup findGroupByGroupname(String module, String groupname) {
        List<ApplicationSecurityGroup> findAll = groupFacade.findAll(
                FilterData.by("module", module),
                FilterData.by("groupname", groupname)
        );
        return !findAll.isEmpty() ? findAll.get(0) : null;
    }

    public boolean isGroupnameAlreadyExist(String module, String groupname) {
        List<ApplicationSecurityGroup> findAll = groupFacade.findAll(
                FilterData.by("module", module),
                FilterData.by("groupname", groupname)
        );
        return !findAll.isEmpty();
    }

}
