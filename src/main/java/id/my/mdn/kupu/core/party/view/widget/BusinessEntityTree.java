/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.party.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutableTree;
import id.my.mdn.kupu.core.base.view.widget.AbstractPagedValueList.DefaultCount;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.party.dao.BusinessEntityFacade;
import id.my.mdn.kupu.core.party.entity.BusinessEntity;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Dependent
public class BusinessEntityTree extends AbstractMutableTree<BusinessEntity> implements Serializable {
    
    @Inject
    private BusinessEntityFacade dao;

    public BusinessEntityTree() {
        super(BusinessEntity.class);
    }

    @Override
    protected List<BusinessEntity> getFetchedItemsInternal(
            Map<String, Object> parameters, 
            List<FilterData> filters, List<SorterData> sorters, 
            DefaultList<BusinessEntity> defaultReturn, DefaultChecker defaultChecker
    ) {
        return dao.findAll(parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(
            Map<String, Object> parameters, List<FilterData> filters, 
            DefaultCount defaultCount, DefaultChecker defaultChecker
    ) {
        return dao.countAll(parameters, filters);
    }

    @Override
    protected Result<String> createInternal(BusinessEntity entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> deleteInternal(BusinessEntity entity) {
        return dao.remove(entity);
    }

    @Override
    protected Result<String> editInternal(BusinessEntity entity) {
        return dao.edit(entity);
    }

    
    
}
