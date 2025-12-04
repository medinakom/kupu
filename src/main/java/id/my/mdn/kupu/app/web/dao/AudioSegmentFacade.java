/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.dao;

import id.my.mdn.kupu.app.web.model.AudioSegment;
import id.my.mdn.kupu.app.web.model.VideoKajian;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aphasan
 */
@Stateless
public class AudioSegmentFacade extends AbstractFacade<AudioSegment> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    public AudioSegmentFacade() {
        super(AudioSegment.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int removeAudio(VideoKajian videoKajian) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<AudioSegment> cd = cb.createCriteriaDelete(entityClass);
        Root<AudioSegment> segment = cd.from(entityClass);
        cd.where(cb.equal(segment.get("video").get("id"), videoKajian.getId()));

        Query q = getEntityManager().createQuery(cd);
        return q.executeUpdate();
    }

    public void storeChunk(InputStream in, String filename, VideoKajian videoKajian) {

        AudioSegment segment = new AudioSegment();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int b;
            while ((b = in.read()) != -1) {
                baos.write(b);
            }
            segment.setContent(baos.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(AudioSegmentFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        segment.setName(filename);
        segment.setVideo(videoKajian);
        create(segment);

    }

}
