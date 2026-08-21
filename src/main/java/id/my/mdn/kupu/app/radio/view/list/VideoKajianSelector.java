/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view.list;

import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes;
import id.my.mdn.kupu.core.base.view.widget.AbstractValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Named
@Dependent
public class VideoKajianSelector extends AbstractValueList<VideoKajian> {

    @Inject
    private VideoKajianFacade dao;
    
    public VideoKajianSelector() {
        super(VideoKajian.class);
    }

    @Override
    protected List<VideoKajian> getFetchedItemsInternal(Map<String, Object> parameters, List<FilterTypes.FilterData> filters, List<SorterData> sorters, DefaultList<VideoKajian> defaultList, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.findAll(parameters, filters, sorters);
    }
    
}
