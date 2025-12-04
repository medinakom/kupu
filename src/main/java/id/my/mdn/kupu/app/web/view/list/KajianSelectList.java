/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.view.widget.AbstractValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Named("kajianSelectList")
@RequestScoped
public class KajianSelectList extends AbstractValueList<Kajian> {
    
    @Inject
    private KajianFacade dao;

    public KajianSelectList() {
        super(Kajian.class);
    }

    @Override
    protected List<Kajian> getFetchedItemsInternal(Map<String, Object> parameters, 
            List<FilterData> filters, List<SorterData> sorters, DefaultList<Kajian> defaultList, DefaultChecker defaultChecker) {
        return dao.findAll(parameters, filters, sorters);
    }

    
    
}
