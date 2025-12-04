/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.core.party.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.party.dao.PostalAddressPurposeTypeFacade;
import id.my.mdn.kupu.core.party.entity.PostalAddressPurposeType;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Dependent
public class PostalAddressPurposeTypeList
        extends AbstractMutablePagedValueList<PostalAddressPurposeType> {
    
    @Inject
    private PostalAddressPurposeTypeFacade dao;

    public PostalAddressPurposeTypeList() {
        super(PostalAddressPurposeType.class);
    }

    @Override
    protected List<PostalAddressPurposeType> getPagedFetchedItemsInternal(int first, int pageSize, Map<String, Object> parameters, List<FilterData> filters, List<SorterData> sorters, DefaultList<PostalAddressPurposeType> defaultList, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterData> filters, DefaultCount defaultCount, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters);
    }

    @Override
    protected Result<String> createInternal(PostalAddressPurposeType entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(PostalAddressPurposeType entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(PostalAddressPurposeType entity) {
        return dao.remove(entity);
    }

}
