/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.base.view;

import id.my.mdn.kupu.core.base.util.ModuleInfo;
import id.my.mdn.kupu.core.base.util.SubModuleInfo;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Arief Prihasanto <aphasan at mdnx.dev>
 */
@Named(value = "dashboard")
@ViewScoped
public class DashboardPage extends Page implements Serializable {

    @PostConstruct
    @Override
    protected void init() {
        super.init();
    }

    public String itemPath(ModuleInfo moduleInfo) {
        if(moduleInfo instanceof SubModuleInfo) {
            return moduleInfo.getName();
        } else {
            return "app/" + moduleInfo.getName();
        }
    }
}
