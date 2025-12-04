/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

/**
 *
 * @author aphasan
 */
@Entity
@IdClass(VideoSegmentId.class)
public class VideoSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    private VideoKajian video;
    
    @Id
    private String name;
    
    @Lob
    private byte [] content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VideoKajian getVideo() {
        return video;
    }

    public void setVideo(VideoKajian video) {
        this.video = video;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
}
