/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio;

import id.my.mdn.kupu.core.base.AbstractModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "radio")
@ApplicationScoped
public class RadioModule extends AbstractModule {

    @Override
    protected String getLabel() {
        return "Radio";
    }

    @Override
    protected int getOrder() {
        return 2;
    }

    
    
}
