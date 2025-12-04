/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.my.mdn.kupu.app.web.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author aphasan
 */
@ApplicationScoped
@ServerEndpoint("/viewer/{videoId}")
public class ViewerEndpoint {

    private static final Map<String, Set<Session>> videoSessions = new ConcurrentHashMap<>();

    @Inject
    private ViewerCounter counter;

    @OnOpen
    public void onOpen(Session session, @PathParam("videoId") String videoId) {
        counter.addActiveViewer(videoId, session.getId());

        videoSessions.computeIfAbsent(videoId, k -> ConcurrentHashMap.newKeySet()).add(session);

        broadcastActiveCount(videoId);
    }

    @OnClose
    public void onClose(Session session, @PathParam("videoId") String videoId) {
        counter.removeActiveViewer(videoId, session.getId());

        Set<Session> sessions = videoSessions.get(videoId);
        if (sessions != null) {
            sessions.remove(session);
        }

        broadcastActiveCount(videoId);
    }

    private void broadcastActiveCount(String videoId) {
        String message = String.valueOf(counter.getTotalActiveVisitors(videoId));
        Set<Session> sessions = videoSessions.get(videoId);
        if (sessions != null) {
            sessions.forEach(s -> {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
