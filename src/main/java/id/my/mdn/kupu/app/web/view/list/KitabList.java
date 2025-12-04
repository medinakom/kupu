/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.view.filter.KitabFilter;
import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Dependent
public class KitabList extends AbstractMutablePagedValueList<Kitab> {

    @Inject
    private KitabFacade dao;
    
    @Inject
    private KitabFilter filterContent;

    public KitabList() {
        super(Kitab.class);
        filter.setContent(filterContent);
    }

    @Override
    protected List<Kitab> getPagedFetchedItemsInternal(int first, int pageSize, 
            Map<String, Object> parameters, List<FilterData> filters, 
            List<SorterData> sorters, DefaultList<Kitab> defaultList, DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, 
                filters, sorters, defaultList.get(), defaultChecker);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterData> filters, 
            DefaultCount defaultCount, DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters, defaultCount.get(), defaultChecker);
    }

    @Override
    protected Result<String> createInternal(Kitab entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> deleteInternal(Kitab entity) {
        return dao.remove(entity);
    }

    @Override
    protected Result<String> editInternal(Kitab entity) {
        return dao.edit(entity);
    }
    
    
    
}
