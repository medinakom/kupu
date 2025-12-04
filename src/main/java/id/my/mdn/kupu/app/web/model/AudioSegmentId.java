/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author aphasan
 */
public class AudioSegmentId implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String video;
    
    private String name;

    public AudioSegmentId(String video, String name) {
        this.video = video;
        this.name = name;
    }

    public String getVideo() {
        return video;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.video);
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AudioSegmentId other = (AudioSegmentId) obj;
        if (!Objects.equals(this.video, other.video)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
}
