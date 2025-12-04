/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.view;

import id.my.mdn.kupu.app.web.dao.ContentCategoryFacade;
import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.dao.UstadzFacade;
import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.ContentCategory;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.Ustadz;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author aphasan
 */
@Named(value = "page")
@RequestScoped
public class WebPage {

    @Inject
    private ContentCategoryFacade contentCategoryFacade;
    
    @Inject
    private UstadzFacade ustadzFacade;
    
    @Inject
    private KajianFacade kajianFacade;
    
    @Inject
    private KitabFacade kitabFacade;

    private String activeCategoryId;
    
    private Long activeUstadzId;
    
    private String activeKajianId;

    private int pageSize;
    
    @Inject
    private Configuration config;

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
            pageSize = config.getInt("web.categoryPageSize");
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getActiveCategoryId() {
        return activeCategoryId;
    }

    public void setActiveCategoryId(String activeCategoryId) {
        this.activeCategoryId = activeCategoryId;
    }

    public List<ContentCategory> getCategories() {
        return contentCategoryFacade.findAll();
    }
    
    public ContentCategory getCategory() {
        return contentCategoryFacade.find(getActiveCategoryId());
    }
    
    public Ustadz getUstadz() {
        return ustadzFacade.find(getActiveUstadzId());
    }

    public Long getActiveUstadzId() {
        return activeUstadzId;
    }

    public void setActiveUstadzId(Long activeUstadzId) {
        this.activeUstadzId = activeUstadzId;
    }

    public String getActiveKajianId() {
        return activeKajianId;
    }

    public void setActiveKajianId(String activeKajianId) {
        this.activeKajianId = activeKajianId;
    }
    
    public Kajian getKajian() {
        return kajianFacade.find(getActiveKajianId());
    }
    
    public List<Kitab> getKitab() {
        Kajian kajian = kajianFacade.find(getActiveKajianId());
        Map<String, Object> filters = new HashMap<>();
        filters.put("kajian", kajian);         
        return kitabFacade.findAll(filters);
    }  
    
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    @Inject
    private VideoKajianFacade videoFacade;
    
    public VideoKajian getLive() {
        return videoFacade.getLive();
    }
    
    public VideoKajian getPublicLive() {
        return videoFacade.getPublicLive();
    }
    
    public boolean isTokenValid() {
        return videoFacade.checkLiveTokenValidity(token);
    }
    
    public boolean isYoutubeLive() {
        String streamUrl = config.getString("web.liveStreamingAddress");
        return streamUrl.toLowerCase().contains("youtube");
    }

}
