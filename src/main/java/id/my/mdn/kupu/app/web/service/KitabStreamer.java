/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.KitabContentFacade;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.KitabContent;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author aphasan
 */
@Named(value = "kitabStreamer")
@ViewScoped
public class KitabStreamer implements Serializable{
    
    @Inject
    private KitabContentFacade contentFacade;

    public StreamedContent getStream() {
        
        FacesContext context = FacesContext.getCurrentInstance();
        String contentId = context.getExternalContext().getRequestParameterMap().get("kitab");
        byte[] content = contentFacade.getContent(contentId);
        return DefaultStreamedContent.builder()
                .contentType("application/pdf")
                .stream(() -> {
                    return new ByteArrayInputStream(content);
                })
                .build();
    }

    public StreamedContent streamed(Kitab kitab) {
        
        KitabContent kitabContent = kitab.getContent();
        byte[] content = contentFacade.getContent(kitabContent.getId());

        return DefaultStreamedContent.builder()
                .contentType("application/pdf")
                .stream(() -> {
                    return new ByteArrayInputStream(content);
                })
                .build();
    }
}
