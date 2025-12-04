/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.filter;

import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.core.base.view.annotation.Bookmark;
import id.my.mdn.kupu.core.base.view.widget.FilterContent;
import jakarta.enterprise.context.Dependent;
import java.io.Serializable;

/**
 *
 * @author aphasan
 */
@Dependent
public class KitabFilter extends FilterContent implements Serializable {
    
    @Bookmark(name = "k")
    private Kajian kajian; 

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }
    
}
