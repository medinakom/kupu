/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.core.base.view.converter;

import id.my.mdn.kupu.core.base.util.EntityUtil;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.convert.FacesConverter;

/**
 *
 * @author aphasan
 */
@Dependent @FacesConverter(managed = true, value = "SorterDataListConverter")
public class SorterDataListConverter extends SelectionsConverter<SorterData> {

    @Override
    protected SorterData getAsObject(String value) {
        return new SorterData(EntityUtil.parseCompositeId(value));
    }

    @Override
    protected String getAsString(SorterData obj) {
        return obj != null ? String.valueOf(obj) : null;
    }
    
}
