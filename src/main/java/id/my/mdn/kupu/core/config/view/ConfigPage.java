
package id.my.mdn.kupu.core.config.view;

import id.my.mdn.kupu.core.base.view.ChildPage;
import id.my.mdn.kupu.core.base.view.annotation.Bookmarked;
import id.my.mdn.kupu.core.config.view.widget.ConfigList;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author Arief Prihasanto <aphasan at medinacom.id>
 */
@Named(value = "configPage")
@ViewScoped
public class ConfigPage extends ChildPage implements Serializable {
    
    @Inject
    @Bookmarked
    private ConfigList configList;
    
    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    public ConfigList getConfigList() {
        return configList;
    }
}
