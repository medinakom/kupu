/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import id.my.mdn.kupu.app.web.dao.VideoKajianFacade;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author aphasan
 */
@Named(value = "viewerCounter")
@ApplicationScoped
public class ViewerCounter implements Serializable {

    private final Map<String, Set<String>> activeViewers = new ConcurrentHashMap<>();

    @Inject
    private VideoKajianFacade videoKajianFacade;

    public void incVisitorCount(String videoId) {
        if(videoId == null) return;
        synchronized (videoId) {
            videoKajianFacade.incVisitorCount(videoId);
        }
    }

    public long getTotalActiveVisitors(String videoId) {
        return getActiveViewers().getOrDefault(videoId, Collections.emptySet()).size();
    }

    public void addActiveViewer(String videoId, String sessionId) {
        getActiveViewers()
                .computeIfAbsent(videoId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);;
    }

    public void removeActiveViewer(String videoId, String sessionId) {
        Set<String> viewers = getActiveViewers().get(videoId);
        if (viewers != null) {
            viewers.remove(sessionId);
        }
    }

    public Map<String, Set<String>> getActiveViewers() {
        return activeViewers;
    }
}
