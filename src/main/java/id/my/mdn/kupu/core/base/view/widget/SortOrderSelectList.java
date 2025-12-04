/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.widget;

import id.my.mdn.kupu.core.base.view.annotation.SorterField;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author aphasan
 */
@Named(value = "sortOrderSelectList")
@RequestScoped
public class SortOrderSelectList implements IValueList<SorterField.Order> {

    @Override
    public List<SorterField.Order> getFetchedItems() {
        return Arrays.asList(SorterField.Order.values());
    }

    
    
}
