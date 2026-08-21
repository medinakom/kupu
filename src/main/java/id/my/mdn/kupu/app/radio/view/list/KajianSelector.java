/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view.list;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.core.base.view.widget.IValueList;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Named
@Dependent
public class KajianSelector implements IValueList<Kajian> {
    
    @Inject
    private KajianFacade dao;

    @Override
    public List<Kajian> getFetchedItems() {
        return dao.findAll();
    }
    
}
