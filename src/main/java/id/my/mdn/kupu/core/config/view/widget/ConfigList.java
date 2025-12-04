package id.my.mdn.kupu.core.config.view.widget;

import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import id.my.mdn.kupu.core.config.dao.ConfigFacade;
import id.my.mdn.kupu.core.config.model.Config;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Arief Prihasanto <aphasan at medinacom.id>
 */
@Named(value = "configList")
@Dependent
public class ConfigList extends AbstractMutablePagedValueList<Config> implements Serializable {

    @Inject
    private ConfigFilter filterContent;

    @Inject
    private ConfigFacade dao;

    public ConfigList() {
        super(Config.class);
    }

    @PostConstruct
    public void init() {
        filter.setContent(filterContent);
    }

    @Override
    protected List<Config> getPagedFetchedItemsInternal(int first, int pageSize,
            Map<String, Object> parameters, List<FilterData> filters,
            List<SorterData> sorters, DefaultList<Config> defaultList,
            DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, filters, sorters);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters,
            List<FilterData> filters, DefaultCount defaultCount,
            DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters);
    }

    @Override
    public void edit(Config entity) {
        dao.edit(entity);
    }

    @Override
    protected boolean updateInternal(String field, Config entity, Object newValue, Map<String, Object> context) {
        switch (field) {
            case "value":
                entity.setValue((String) newValue);
                return true;
            default:
                return super.updateInternal(field, entity, newValue, context);
        }
    }

    @Override
    protected Result<String> createInternal(Config entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Result<String> editInternal(Config entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(Config entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
