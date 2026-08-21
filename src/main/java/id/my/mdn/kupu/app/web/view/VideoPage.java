/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.view;

import id.my.mdn.kupu.app.web.dao.KajianFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.Kajian;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.app.web.service.ViewerCounter;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.view.annotation.SorterField.Order;
import id.my.mdn.kupu.core.base.view.widget.SorterData;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.commons.configuration2.Configuration;

/**
 *
 * @author aphasan
 */
@Named(value = "videoPage")
@RequestScoped
public class VideoPage {

    private Kajian kajian;

    private VideoKajian videoKajian;

    private int pageSize;

    @Inject
    private KajianFacade kajianFacade;

    @Inject
    private KitabFacade kitabFacade;

    @Inject
    private VideoKajianFacade videoKajianFacade;

    @Inject
    private Configuration config;

    @Inject
    private ViewerCounter counter;

    @PostConstruct
    public void init() {
        var ext = FacesContext.getCurrentInstance().getExternalContext();
        var req = (HttpServletRequest) ext.getRequest();
        var res = (HttpServletResponse) ext.getResponse();

        String videoId = req.getParameter("v");

        String cookieName = "hasVisited_" + videoId;
        boolean visited = false;

        if (req.getCookies() != null) {
            for (var c : req.getCookies()) {
                if (cookieName.equals(c.getName())) {
                    visited = true;
                    break;
                }
            }
        }

        if (!visited) {
            counter.incVisitorCount(videoId);
            Cookie visitorCookie = new Cookie(cookieName, "true");
            visitorCookie.setMaxAge(60);

            res.addCookie(visitorCookie);

        } else {
//            visitorNumber = counterService.getCount("/index.xhtml");
        }
    }

    public void incPageSize(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();

        String strKajian = context.getExternalContext().getRequestParameterMap().get("kajian");
        setKajian(kajianFacade.find(strKajian));

        String strVideo = context.getExternalContext().getRequestParameterMap().get("video");
        setVideoKajian(videoKajianFacade.find(strVideo));

        String strPageSize = context.getExternalContext().getRequestParameterMap().get("page-size");
        setPageSize(Integer.parseInt(strPageSize) + config.getInt("web.videoPageSize"));
    }

    public int getPageSize() {
        if (pageSize == 0) {
            pageSize = config.getInt("web.videoPageSize");
        }
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

    public VideoKajian getVideoKajian() {
        return videoKajian;
    }

    public void setVideoKajian(VideoKajian videoKajian) {
        this.videoKajian = videoKajian;
    }

    public List<VideoKajian> getVideos() {
        List<FilterData> filters = List.of(
                FilterData.by("kajian", kajian), 
                FilterData.by("published", true));
        
        List<SorterData> sorters = List.of(
                SorterData.by("eventTime", Order.DESC));
        
        List<VideoKajian> res = videoKajianFacade.findAll(
                0, getPageSize(), filters, sorters);
        
        return res;
    }

    public List<Kitab> getKitab() {
        return kitabFacade.findAll(List.of(FilterData.by("kajian", kajian)));
    }

}
