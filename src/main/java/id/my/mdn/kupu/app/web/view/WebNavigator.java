/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view;

import id.my.mdn.kupu.app.radio.view.RadioPage;
import id.my.mdn.kupu.app.web.view.admin.UstadzPage;
import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.widget.PageNavigator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named
@ApplicationScoped
public class WebNavigator extends PageNavigator implements Serializable {
    
    @Override
    protected Class<? extends Page> pageMap(String pageId) {
        return switch (pageId) {
            case "Ustadz" -> UstadzPage.class;
            case "Radio" -> RadioPage.class;
            default -> null;
        };
    }

    @Override
    protected String getHome() {
        return "/app/web/view/admin/index.xhtml";
    }

}
