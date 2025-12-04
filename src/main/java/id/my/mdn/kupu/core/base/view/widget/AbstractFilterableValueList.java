/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 * @param <E>
 */
public abstract class AbstractFilterableValueList<E> implements IValueList<E> {  
    
    protected Parameters parameters = () -> Map.of();

    protected Filters filters = () -> List.of();
    
    private Sorters sorters = () -> List.of();

    protected abstract List<E> getFetchedItemsInternal(
            Map<String, Object> parameters,
            List<FilterData> filters,
            List<SorterData> sorters
    );

    @Override
    public List<E> getFetchedItems() {
            return getFetchedItemsInternal(
                    parameters.get(),
                    filters.get(),
                    sorters.get()
            );
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public void setSorters(Sorters sorters) {
        this.sorters = sorters;
    }
    
}
