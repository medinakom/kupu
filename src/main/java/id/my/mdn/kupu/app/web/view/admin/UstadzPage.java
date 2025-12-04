
package id.my.mdn.kupu.app.web.view.admin;

import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.app.web.view.list.UstadzList;
import id.my.mdn.kupu.core.base.view.annotation.Creator;
import id.my.mdn.kupu.core.base.view.annotation.Deleter;
import id.my.mdn.kupu.core.base.view.annotation.Editor;
import jakarta.faces.event.ActionEvent;
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
public class UstadzPage implements Serializable {

    @Inject
    private UstadzFacade dao;
    
    @Inject
    private UstadzList ustadzList;
    
    private Ustadz selection;
    
    private Ustadz transientData;

    public UstadzList getUstadzList() {
        return ustadzList;
    }
    
    @Creator(of = "ustadzList")
    public void onCreateData(ActionEvent evt) {
        Ustadz data = new Ustadz();
        transientData = data;
    }
    
    @Editor(of = "ustadzList")
    public void onEditData(ActionEvent evt) {
        transientData = selection;
    }
    
    @Deleter(of = "ustadzList")
    public void onDeleteData(ActionEvent evt) {
        dao.remove(selection);
        selection = null;
    }
    
    public void save(ActionEvent evt) {
        if (transientData.getId() == null) {
            dao.create(transientData);
            selection = transientData;
        } else {
            dao.edit(transientData);
        }
        transientData = null;
    }
    
    public void onSelectData() {
    }
    
    public void onUnselectData() {
        selection = null;
    }
    

    public Ustadz getSelection() {
        return selection;
    }

    public void setSelection(Ustadz selection) {
        this.selection = selection;
    }

    public Ustadz getTransientData() {
        return transientData;
    }

    public void setTransientData(Ustadz transientData) {
        this.transientData = transientData;
    }
    
}
