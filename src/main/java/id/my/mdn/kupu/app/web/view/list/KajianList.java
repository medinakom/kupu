/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.list;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.util.FileSystem;
import id.my.mdn.kupu.core.base.dao.AbstractFacade.DefaultChecker;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import id.my.mdn.kupu.core.base.view.widget.AbstractMutablePagedValueList;
import id.my.mdn.kupu.core.base.view.widget.CommonFilter;
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
public class KajianList extends AbstractMutablePagedValueList<Kajian> {

    @Inject
    private FileSystem fs;

    @Inject
    private KajianFacade dao;

    @Inject
    private CommonFilter filterContent;

    public KajianList() {
        super(Kajian.class);
    }

    @PostConstruct
    public void init() {
        filter.setContent(filterContent);
    }

    @Override
    protected List<Kajian> getPagedFetchedItemsInternal(int first, int pageSize,
            Map<String, Object> parameters, List<FilterData> filters,
            List<SorterData> sorters, DefaultList<Kajian> defaultList,
            DefaultChecker defaultChecker) {
        return dao.findAll(first, pageSize, parameters,
                filters, sorters, defaultList.get(), defaultChecker);
    }

    @Override
    protected long getItemsCountInternal(Map<String, Object> parameters,
            List<FilterData> filters, DefaultCount defaultCount, DefaultChecker defaultChecker) {
        return dao.countAll(parameters, filters, defaultCount.get(), defaultChecker);
    }

    @Override
    protected Result<String> createInternal(Kajian entity) {
        return dao.create(entity);
    }

    @Override
    protected Result<String> editInternal(Kajian entity) {
        return dao.edit(entity);
    }

    @Override
    protected Result<String> deleteInternal(Kajian entity) {
        Result<String> result = dao.remove(entity);

        try {
            fs.deleteDir(fs.createDirectory(fs.getVideoDir(), entity.getId()), false);
            fs.deleteDir(fs.createDirectory(fs.getAudioDir(), entity.getId()), false);
        } catch (IOException ex) {
            System.getLogger(KajianList.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        return result;

    }

}
