/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author aphasan
 */
@Named(value = "kajianRetriever")
@RequestScoped
public class KajianRetriever {
    
    @Inject
    private UstadzFacade ustadzFacade;

    @Inject
    private KajianFacade kajianFacade;
    
    @Inject
    private KitabFacade kitabFacade;
    
    @Inject
    private VideoKajianFacade videoFacade;
    
    @Inject
    private Configuration config;

    private int pageSize;

    private String activeCategoryId;

    public String getActiveCategoryId() {
        return activeCategoryId;
    }

    public void setActiveCategoryId(String activeCategoryId) {
        this.activeCategoryId = activeCategoryId;
    }

    public void incPageSize(ActionEvent evt) {
        String strCategory = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("category");
        setActiveCategoryId(strCategory);
        String strPageSize = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("page-size");
        setPageSize(Integer.parseInt(strPageSize) + config.getInt("web.categoryPageSize"));
    }

    public int getPageSize() {
        if (pageSize == 0) {
            pageSize = config.getInt("application.categoryPageSize");
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Kajian> getTerbaru() {
        return kajianFacade.findAll(0, config.getInt("web.newestContentSize"), null);
    }

    public List<Kajian> getTerpenting() {
        List<FilterData> filters = List.of(FilterData.by("contentTags", Arrays.asList("Kajian Terpenting")));
        return kajianFacade.findAll(0, config.getInt("web.terpentingSize"), filters);
    }

    public List<Kajian> getByCategory(String category, Integer pageSize) {
        List<Kajian> ret = kajianFacade.findAll(0, pageSize, List.of(FilterData.by("contentCategory", category)));
        return ret;
    }

    public List<Kajian> getByCategory(String category) {
        List<Kajian> ret = kajianFacade.findAll(List.of(FilterData.by("contentCategory", category)));
        return ret;
    }
    
    public List<Ustadz> getAsatidz() {
        return ustadzFacade.findAll();
    }

    public List<Kajian> getByUstadz(Long id) {
        List<Kajian> ret = kajianFacade.findAll(List.of(FilterData.by("ustadz", id)));
        return ret;
    }

    public Long getKajianCountByUstadz(Long id) {
        return kajianFacade.countAll(List.of(FilterData.by("ustadz", id)));
    }

    public Long getVideoCountByKajian(Kajian kajian) {
        return videoFacade.countAll(List.of(FilterData.by("kajian", kajian)));
    }

    public VideoKajian getFirstVideo(Kajian kajian) {
        return kajianFacade.getFirstVideo(kajian);
    }
    
    public VideoKajian getNewestVideo() {
        return videoFacade.findNewestVideo();
    }
    
    public List<VideoKajian> getNewestVideos() {
        return videoFacade.findNewestVideos();
    }
    
    public boolean isLive() {
        return true;
    }
    
    public List<Kajian> getKajianWithKitab() {
        return kajianFacade.getKajianWithKitab();
    }

    public List<Kitab> getKitabByKajian(Kajian kajian) {
        return kitabFacade.findAll(List.of(FilterData.by("kajian", kajian)));
    }

    public Long getKitabCountByKajian(Kajian kajian) {
        return kitabFacade.countAll(List.of(FilterData.by("kajian", kajian)));
    }

}
