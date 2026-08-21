/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

/**
 *
 * @author Arief Prihasanto <aphasan at mdnx.dev>
 */
@Entity
public class Kitab extends Content {

    private static final long serialVersionUID = 1L;
    
    private String author;

    @ManyToOne
    private Kajian kajian;
    
    @OneToOne(mappedBy = "kitab", cascade = CascadeType.ALL, orphanRemoval = true)
    private KitabContent content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Kajian getKajian() {
        return kajian;
    }

    public void setKajian(Kajian kajian) {
        this.kajian = kajian;
    }

    public KitabContent getContent() {
        if(content == null) return null;
        return content;
    }

    public void setContent(KitabContent content) {
        this.content = content;
    }
    
    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();
    }
    
}
