/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Named(value = "lazyUstadzDataModel")
@Dependent
public class UstadzList extends AbstractMutablePagedValueList<Ustadz> {

    @Inject
    private UstadzFacade dao;

    public UstadzList() {
        super(Ustadz.class);
    }

    @Override
    protected List<Ustadz> getPagedFetchedItemsInternal(int first, int pageSize, 
            Map<String, Object> parameters, List<FilterData> filters, 
            List<SorterData> sorters, DefaultList<Ustadz> defaultList, 
            DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, 
                filters, sorters, defaultList.get(), defaultChecker);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, 
            List<FilterData> filters, DefaultCount defaultCount, DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters, defaultCount.get(), defaultChecker);
    }

    @Override
    protected Result<String> createInternal(Ustadz entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> deleteInternal(Ustadz entity) {
        return dao.remove(entity);
    }

    @Override
    protected Result<String> editInternal(Ustadz entity) {
        return dao.edit(entity);
    }
    
}
