package id.my.mdn.kupu.core.base.view.widget;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aphasan
 */
public abstract class FilterContent implements Serializable {
    
    private final Map<String, Object> flags = new HashMap<>();

    public Map<String, Object> getFlags() {
        return flags;
    }

    public void setFlag(String key, Object val) {
        flags.put(key, val);
    }
}
