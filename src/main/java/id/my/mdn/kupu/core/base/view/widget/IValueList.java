
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 *
 * @author aphasan
 * @param <E>
 */
public interface IValueList<E> extends Serializable {

    @FunctionalInterface
    public static interface Parameters {
        Map<String, Object> get();
    }

    @FunctionalInterface
    public static interface Filters {
        List<FilterData> get();
    }

    @FunctionalInterface
    public static interface Sorters {
        List<SorterData> get();
    }
    
    public abstract List<E> getFetchedItems();

}
