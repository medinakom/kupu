/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.my.mdn.kupu.app.web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;
import java.io.Serializable;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;

/**
 *
 * @author aphasan
 */
@Entity
public class Ustadz implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @TableGenerator(name = "Ustadz", table = "KEYGEN", allocationSize = 1)
    @GeneratedValue(generator = "Ustadz", strategy = GenerationType.TABLE)
    private Long id;
    
    private String name;
    
    private String prefixTitle;
    
    private String suffixTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefixTitle() {
        return prefixTitle;
    }

    public void setPrefixTitle(String prefixTitle) {
        this.prefixTitle = prefixTitle;
    }

    public String getSuffixTitle() {
        return suffixTitle;
    }

    public void setSuffixTitle(String suffixTitle) {
        this.suffixTitle = suffixTitle;
    }
    
    public String getFullname() {
        return Stream.of(prefixTitle, name, suffixTitle).filter(s -> s != null).collect(joining(" ", "", ""));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ustadz)) {
            return false;
        }
        Ustadz other = (Ustadz) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
    
}
