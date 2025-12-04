/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.security.view;

import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.FormPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.security.dao.ApplicationSecurityGroupSqlFacade;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import org.omnifaces.cdi.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author aphasan
 */
@Named(value = "groupEditorPage")
@ViewScoped
public class ApplicationSecurityGroupEditorPage extends FormPage<ApplicationSecurityGroup> {

    @Inject
    private ApplicationSecurityGroupSqlFacade groupFacade;

    @Bookmarked
    private String module;

    @Override
    protected ApplicationSecurityGroup newEntity() {
        ApplicationSecurityGroup group = groupFacade.createTransient(null);
//        group.setGroupname("New Group");
//        group.setModule(module);

        return group;
    }

    @Override
    protected Result<String> save(ApplicationSecurityGroup entity) {
        return groupFacade.create(entity);
    }

    @Override
    protected Result<String> edit(ApplicationSecurityGroup entity) {
        return groupFacade.edit(entity);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

}
