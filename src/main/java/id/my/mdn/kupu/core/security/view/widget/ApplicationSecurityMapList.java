/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.security.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.view.widget.AbstractPagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.security.dao.ApplicationSecurityGroupSqlFacade;
import id.my.mdn.kupu.core.security.model.ApplicationSecurityGroup;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan57 at gmail.com>
 */
@Named(value = "applicationSecurityMapList")
@Dependent
public class ApplicationSecurityMapList extends AbstractPagedValueList<ApplicationSecurityGroup> {

    @Inject
    private ApplicationSecurityGroupSqlFacade dao;

    public ApplicationSecurityMapList() {
        super(ApplicationSecurityGroup.class);
    }

    @Override
    protected List<ApplicationSecurityGroup> getPagedFetchedItemsInternal(
            int first, int pageSize, Map<String, Object> parameters,
            List<FilterData> filters, List<SorterData> sorters,
            DefaultList<ApplicationSecurityGroup> defaultList,
            DefaultChecker defaultChecker) {
        
        List<ApplicationSecurityGroup> findAll = dao.findAll(first, pageSize, parameters, filters);
        return findAll;
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters,
            List<FilterData> filters,
            DefaultCount defaultCount, DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters);
    } 

}
