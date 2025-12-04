/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.VideoKajianAdminFacade;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.service.MediaService;
import id.my.mdn.kupu.app.web.view.filter.VideoKajianFilter;
import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aphasan
 */
@Dependent
public class VideoKajianList extends AbstractMutablePagedValueList<VideoKajian> {
    
    @Inject
    private VideoKajianAdminFacade dao;
    
    @Inject
    private VideoKajianFilter filterContent;
    
    @Inject
    private MediaService mediaService;

    public VideoKajianList() {
        super(VideoKajian.class);
    }

    @PostConstruct
    public void init() {
        filter.setContent(filterContent);
    }

    @Override
    protected List<VideoKajian> getPagedFetchedItemsInternal(int first, int pageSize, 
            Map<String, Object> parameters, List<FilterData> filters, 
            List<SorterData> sorters, DefaultList<VideoKajian> defaultList, DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters, filters, sorters, defaultList.get(), defaultChecker);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters, List<FilterData> filters, 
            DefaultCount defaultCount, DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters, defaultCount.get(), defaultChecker);
    }

    @Override
    protected Result<String> createInternal(VideoKajian entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> deleteInternal(VideoKajian videoKajian) {
        Result<String> result = dao.remove(videoKajian);
        try {
            mediaService.deleteMedia(videoKajian);
        } catch (IOException ex) {
            System.getLogger(VideoKajianList.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        return result;
    }

    @Override
    protected Result<String> editInternal(VideoKajian entity) {
        return dao.edit(entity);
    }

    
    
}
