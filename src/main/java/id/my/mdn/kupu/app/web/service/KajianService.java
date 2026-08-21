/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.KitabContentFacade;
import id.my.mdn.kupu.app.web.dao.KitabFacade;
import id.my.mdn.kupu.app.web.model.Kitab;
import id.my.mdn.kupu.app.web.model.KitabContent;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/**
 *
 * @author aphasan
 */
@Stateless
public class KajianService {
    
    @Inject
    private KitabFacade kitabDao;

    @Inject
    private KitabContentFacade kitabContentDao;

    public void updateKitabContent(Kitab kitab, byte[] content) {

        KitabContent kitabContent = kitabContentDao.findSingleByAttribute("kitab", kitab);
        if (kitabContent != null) {
            kitabContent.setContent(content);
            
            kitabContentDao.edit(kitabContent);
        } else  {
            kitabContent = new KitabContent();

            kitabContent.setContent(content);
            kitabContent.setKitab(kitab);
            kitabContent.setTitle(kitab.getTitle());
            
            kitab.setContent(kitabContent);
            
            kitabDao.edit(kitab);
        }

    }
}
