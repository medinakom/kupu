
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.view.list.UstadzList;
import id.my.mdn.kupu.core.base.view.Page;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;

/**
 *
 * @author aphasan
 */
@Named(value = "ustadzPage")
@ViewScoped
public class UstadzPage extends Page implements Serializable {
    
    @Inject
    private UstadzList ustadzList;

    @PostConstruct
    @Override
    public void init() {
        super.init();
    }

    public UstadzList getUstadzList() {
        return ustadzList;
    }
    
    @Creator(of = "ustadzList")
    public void onCreateData() {
        gotoChild(UstadzEditorPage.class)
                .open();
    }
    
    @Editor(of = "ustadzList")
    public void onEditData() {
        gotoChild(UstadzEditorPage.class)
                .addParam("entity").withValues(ustadzList.getSelected())
                .open();
    }
    
    @Deleter(of = "ustadzList")
    public void onDeleteData() {
        ustadzList.deleteSelected();
    }
    
}
