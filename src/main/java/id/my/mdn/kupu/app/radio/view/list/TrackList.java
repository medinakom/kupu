/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.radio.view.list;

import id.my.mdn.kupu.app.radio.dao.TrackFacade;
import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Dependent
public class TrackList extends AbstractMutablePagedValueList<Track> {

    @Inject
    private TrackFacade dao;

    public TrackList() {
        super(Track.class);
    }

    @Override
    protected List<Track> getPagedFetchedItemsInternal(int first, int pageSize, Map<String, Object> parameters, List<FilterTypes.FilterData> filters, List<SorterData> sorters, DefaultList<Track> defaultList, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, filters, sorters, defaultList.get(), defaultChecker);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterTypes.FilterData> filters, DefaultCount defaultCount, AbstractFacade.DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters, defaultCount.get(), defaultChecker);
    }

    @Override
    protected Result<String> createInternal(Track entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(Track entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(Track entity) {
        return dao.remove(entity);
    }
    
}
