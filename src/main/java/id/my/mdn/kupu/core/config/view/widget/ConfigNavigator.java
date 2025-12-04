/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.config.view.widget;

import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.widget.PageNavigator;
import id.my.mdn.kupu.core.config.view.ConfigPage;
import id.my.mdn.kupu.core.security.dao.ApplicationUserFacade;
import java.io.Serializable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "configNavigator")
@ApplicationScoped
public class ConfigNavigator extends PageNavigator implements Serializable {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private ApplicationUserFacade userFacade;

    @Override
    protected Class<? extends Page> pageMap(String pageId) {
        switch (pageId) {
            case "Configuration":
                return ConfigPage.class;
            default:
                return null;
        }
    }

    @Override
    protected String getHome() {
        return "/core/config/view/config.xhtml";
    }

}
