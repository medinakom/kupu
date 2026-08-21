package id.my.mdn.kupu.app.radio.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "RADIO_TRACK")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRACKTYPE")
public class Track implements Serializable{

    @Id
    @TableGenerator(name = "Radio_Track", table = "KEYGEN", allocationSize = 1)
    @GeneratedValue(generator = "Radio_Track", strategy = GenerationType.TABLE)
    private Long id;
    
    private String title;
    
    private String url;
    
    @Column(name = "TRACKTYPE", updatable = false)
    private String type;
    
    @OneToOne
    private Track previous;
    
    @OneToOne
    private Track next;

    public Track getPrevious() {
        return previous;
    }

    public void setPrevious(Track previous) {
        this.previous = previous;
    }

    public Track getNext() {
        return next;
    }

    public void setNext(Track next) {
        this.next = next;
    }

    public Track() {
    }

    public Track(String title, String sourceUrl) {
        this.title = title;
        this.url = sourceUrl;
    }

    @Override
    public String toString() {
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
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
        final Track other = (Track) obj;
        return Objects.equals(this.id, other.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
