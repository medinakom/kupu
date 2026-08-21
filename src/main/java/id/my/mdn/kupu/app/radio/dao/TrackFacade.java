/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.radio.dao;

import id.my.mdn.kupu.app.radio.model.Track;
import id.my.mdn.kupu.app.radio.playlist.TrackAdded;
import id.my.mdn.kupu.app.radio.playlist.TrackRemoved;
import id.my.mdn.kupu.core.base.dao.AbstractFacade;
import id.my.mdn.kupu.core.base.util.FilterTypes.FilterData;
import id.my.mdn.kupu.core.base.util.Result;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 *
 * @author aphasan
 */
@ApplicationScoped
@Transactional
public class TrackFacade extends AbstractFacade<Track> {

    @PersistenceContext(unitName = "KupuPersistenceUnit")
    private EntityManager em;

    @Inject
    @TrackAdded
    private Event<Track> trackAddedEvent;

    @Inject
    @TrackRemoved
    private Event<Track> trackRemovedEvent;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TrackFacade() {
        super(Track.class);
    }

    @Override
    public Result<String> remove(Track track) {

        Result<String> result = null;

        Track previousTrack = track.getPrevious();
        Track nextTrack = track.getNext();

        if (previousTrack != null && nextTrack != null) {

            previousTrack.setNext(nextTrack);
            nextTrack.setPrevious(previousTrack);
            track.setPrevious(null);
            track.setNext(null);

            Result<String> editPreviousTrack = edit(previousTrack);
            Result<String> editNextTrack = edit(nextTrack);
            Result<String> deleteTrack = super.remove(track);

            result = new Result<>(
                    deleteTrack.isSuccess() && editPreviousTrack.isSuccess() && editNextTrack.isSuccess(),
                    "TRACK: %s, PREV: %s, NEXT: %s".formatted(
                            deleteTrack.payload,
                            editPreviousTrack.payload,
                            editNextTrack.payload)
            );

        } else if (previousTrack == null && nextTrack != null) {

            nextTrack.setPrevious(null);
            track.setNext(null);

            Result<String> editNextTrack = edit(nextTrack);
            getEntityManager().flush();
            Result<String> deleteTrack = super.remove(track);

            result = new Result<>(
                    deleteTrack.isSuccess() && editNextTrack.isSuccess(),
                    "TRACK: %s, NEXT: %s".formatted(
                            deleteTrack.payload,
                            editNextTrack.payload)
            );
        } else if (previousTrack != null && nextTrack == null) {

            previousTrack.setNext(null);
            track.setPrevious(null);

            Result<String> editPreviousTrack = edit(previousTrack);
            getEntityManager().flush();
            Result<String> deleteTrack = super.remove(track);

            result = new Result<>(
                    deleteTrack.isSuccess() && editPreviousTrack.isSuccess(),
                    "TRACK: %s, PREV: %s".formatted(
                            deleteTrack.payload,
                            editPreviousTrack.payload)
            );

        } else {
            result = super.remove(track);
        }

        if (result.isSuccess()) {
            trackRemovedEvent.fireAsync(track);
        }

        return result;
    }

    @Override
    public Result<String> create(Track newTrack) {

        Result<String> result;

        Track previousTrack = newTrack.getPrevious();

        if (previousTrack != null && previousTrack.getNext() == null) {

            // Do tail append            
            previousTrack.setNext(newTrack);
            newTrack.setPrevious(previousTrack);

            Result<String> createNewTrack = super.create(newTrack);
            Result<String> editPreviousTrack = edit(previousTrack);

            String payloadCreateNewTrack = createNewTrack.payload;
            String payloadCreatePreviousTrack = editPreviousTrack.payload;

            result = new Result<>(
                    createNewTrack.isSuccess() && editPreviousTrack.isSuccess(),
                    "PREV: %s, NEW: %s".formatted(
                            payloadCreatePreviousTrack,
                            payloadCreateNewTrack)
            );

        } else if (previousTrack != null && previousTrack.getNext() != null) {
            // Do insertion
            Track nextTrack = previousTrack.getNext();
            previousTrack.setNext(newTrack);
            newTrack.setPrevious(previousTrack);
            newTrack.setNext(nextTrack);
            nextTrack.setPrevious(newTrack);

            Result<String> createNewTrack = super.create(newTrack);
            Result<String> editPreviousTrack = edit(previousTrack);
            Result<String> editNextTrack = edit(nextTrack);

            String payloadCreateNewTrack = createNewTrack.payload;
            String payloadCreatePreviousTrack = editPreviousTrack.payload;
            String payloadCreateNextTrack = editNextTrack.payload;

            result = new Result<>(
                    createNewTrack.isSuccess() && editPreviousTrack.isSuccess() && editNextTrack.isSuccess(),
                    "PREV: %s, NEW: %s, NEXT: %s".formatted(
                            payloadCreatePreviousTrack,
                            payloadCreateNewTrack,
                            payloadCreateNextTrack)
            );
        } else {
            // Empty playlist
            result = super.create(newTrack);
        }

        if (result.isSuccess()) {
            trackAddedEvent.fireAsync(newTrack);
        }

        return result;
    }

    @Override
    protected void addStaticOrders(List<Order> orders, From... from) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        orders.add(cb.asc(from[0].get("previous")));
    }

    @Override
    protected List<Track> postProcess(List<Track> entities) {
        entities.sort((a, b)
                -> a.getPrevious() == null ? -1 : a.getPrevious().getId().compareTo(b.getPrevious().getId())
        );
        return entities;
    }

    @Override
    protected Predicate applyFilter(String filterName, Object filterValue, CriteriaQuery cq, From... froms) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        switch (filterName) {
            case "previous":
                return filterValue != null ? cb.equal(froms[0].get("previous"), filterValue)
                        : cb.isNull(froms[0].get("previous"));
            case "next":
                return filterValue != null ? cb.equal(froms[0].get("next"), filterValue)
                        : cb.isNull(froms[0].get("next"));
            default:
                return super.applyFilter(filterName, filterValue, cq, froms);
        }
    }

    public Track getFirstTrack() {
        return findSingleByAttributes(FilterData.by("previous", null));
    }

    public Track getNextTrack(Track currentTrack) {
        return findSingleByAttributes(FilterData.by("previous", currentTrack));
    }

}
