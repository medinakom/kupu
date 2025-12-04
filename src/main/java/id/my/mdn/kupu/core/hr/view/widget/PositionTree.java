/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.hr.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutableTree;
import id.my.mdn.kupu.core.base.view.widget.AbstractPagedValueList.DefaultCount;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.hr.dao.PositionFacade;
import id.my.mdn.kupu.core.hr.entity.Position;
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
public class PositionTree extends AbstractMutableTree<Position> implements Serializable {

    @Inject
    private PositionFacade dao;

    public PositionTree() {
        super(Position.class);
    }

    @Override
    protected List<Position> getFetchedItemsInternal(
            Map<String, Object> parameters, List<FilterData> filters, 
            List<SorterData> sorters, DefaultList<Position> defaultList, 
            DefaultChecker defaultChecker) {
        
        return dao.findAll(parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(
            Map<String, Object> parameters, List<FilterData> filters, 
            DefaultCount defaultCount, DefaultChecker defaultChecker) {
        
        return dao.countAll(parameters, filters);
    }

    @Override
    protected Result<String> createInternal(Position entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(Position entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(Position entity) {
        return dao.remove(entity);
    }
    
}
