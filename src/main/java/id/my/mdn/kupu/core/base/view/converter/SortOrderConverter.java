/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.converter;

import id.my.mdn.kupu.core.base.view.annotation.SorterField;
import id.my.mdn.kupu.core.base.util.K.KEnum;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

/**
 *
 * @author aphasan
 */
@Dependent
@FacesConverter(managed = true, value = "SortOrderConverter")
public class SortOrderConverter implements Converter<SorterField.Order> {

    @Override
    public SorterField.Order getAsObject(FacesContext context, UIComponent component, String value) {
        return KEnum.valueOf(SorterField.Order.class, value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, SorterField.Order value) {
        return value != null ? value.toString() : null;
    }

}
