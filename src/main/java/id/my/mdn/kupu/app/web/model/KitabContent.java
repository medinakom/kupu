/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

/**
 *
 * @author Arief Prihasanto <aphasan at mdnx.dev>
 */
@Entity
public class KitabContent extends Content {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @OneToOne
    private Kitab kitab;
    
    private byte [] content;
    
    private String mimeType;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Kitab getKitab() {
        return kitab;
    }

    public void setKitab(Kitab kitab) {
        this.kitab = kitab;
    }
    
    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();
    }
    
}
