/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.view.converter;

import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@FacesConverter(value = "VideoKajianConverter", managed = true)
public class VideoKajianConverter implements Converter<VideoKajian> {
    
    @Inject
    private VideoKajianFacade dao;

    @Override
    public VideoKajian getAsObject(FacesContext context, UIComponent component, String value) {
        return dao.find(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, VideoKajian value) {
        return value != null ? value.getId() : null;
    }
    
}
