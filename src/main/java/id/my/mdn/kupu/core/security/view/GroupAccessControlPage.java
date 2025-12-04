/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.security.view;

import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.base.view.widget.Selector;
import id.my.mdn.kupu.core.security.model.AccessControl;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import id.my.mdn.kupu.core.security.service.ApplicationSecurityService;
import id.my.mdn.kupu.core.security.view.widget.GroupAccessControlList;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "groupAccessControlPage")
@ViewScoped
public class GroupAccessControlPage extends ChildPage implements Serializable {

    @Inject
    @Bookmarked
    private GroupAccessControlList dataView;

    @Bookmarked(name = "sg")
    private ApplicationSecurityGroup securityGroup;
    
    @Inject
    private ApplicationSecurityService service;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
        dataView.setHiddenParameters(this::parameters);
        dataView.setSelectionMode(() -> Selector.SINGLE);
    }

    private Map<String, Object> parameters() {   
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("securityGroup", securityGroup);
        return parameters;
    }

    public ApplicationSecurityGroup getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(ApplicationSecurityGroup securityGroup) {
        this.securityGroup = securityGroup;
    }

    public GroupAccessControlList getDataView() {
        return dataView;
    }

    public void toggleActivation(AccessControl acl) {
        if (acl.isActive()) {
            service.closeAccess(securityGroup, acl);
        } else {
            service.openAccess(securityGroup, acl);
        }
    }
    
}
