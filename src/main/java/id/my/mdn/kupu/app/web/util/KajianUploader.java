/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.util;

import id.my.mdn.kupu.app.web.service.VideoSlicer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 *
 * @author aphasan
 */
@Named(value = "kajianUploader")
@RequestScoped
public class KajianUploader {
    
    @Inject
    private VideoSlicer slicer;

    public void slice(ActionEvent evt) {
        
    }
    
}
